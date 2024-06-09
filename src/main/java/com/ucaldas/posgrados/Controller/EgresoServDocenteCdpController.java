package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cdp;
import com.ucaldas.posgrados.Entity.EgresoServDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresosServDocentes;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoCompensacion;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoServDocenteCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosServDocentesRepository;
import com.ucaldas.posgrados.Repository.TipoCompensacionRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoServDocenteCdp")
public class EgresoServDocenteCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoServDocenteCdpRepository egresoServDocenteCDPRepository;

    @Autowired
    private EgresosServDocentesRepository egresoServDocenteRepository;

    @Autowired
    private TipoCompensacionRepository tipoCompensacionRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoServDocenteCDP> listar() {
        return egresoServDocenteCDPRepository.findAll();
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que se tuvo en
     * cuenta en el presupuesto.
     * En el frontend habrá una lista de egresos del presupuesto en la sección de
     * descuentos. Cuando se elija uno, se cargará
     * toda la información de este en los campos, si se guarda así tal como está
     * entonces se pondrá la etiqueta MISMOVALOR, en cambio
     * si se cambia algún valor entonces se pondrá la etiqueta OTROVALOR.
     * 
     * No se puede modificar nombreMateria
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion).orElseThrow();

        EgresosServDocentes egresoServDocente = new EgresosServDocentes();

        egresoServDocente = guardarValoresEgresoCDP(egresoServDocente, nombreMateria, esDocentePlanta, nombreDocente,
                escalafon, titulo, horasTeoricasMat, horasPracticasMat, valorHoraProfesor, tipoCompensacion);

        EgresosServDocentes egresoDelPresupuesto = egresoServDocenteRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getNombreMateria().equals(nombreMateria)
                && egresoDelPresupuesto.isEsDocentePlanta() == esDocentePlanta
                && egresoDelPresupuesto.getNombreDocente().equals(nombreDocente)
                && egresoDelPresupuesto.getEscalafon().equals(escalafon)
                && egresoDelPresupuesto.getTitulo().equals(titulo)
                && egresoDelPresupuesto.getHorasTeoricasMat() == horasTeoricasMat
                && egresoDelPresupuesto.getHorasPracticasMat() == horasPracticasMat
                && egresoDelPresupuesto.getValorHoraProfesor() == valorHoraProfesor
                && egresoDelPresupuesto.getTipoCompensacion().getId() == idTipoCompensacion) {
            egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoServDocenteRepository.save(egresoServDocente);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoServDocenteRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoServDocenteCDP egresoServDocenteCdp = new EgresoServDocenteCDP();
        egresoServDocenteCdp.setCpc(cpc);
        egresoServDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoServDocenteCdp.setCdp(cdp);
        egresoServDocenteCdp.setEgresoServDocente(egresoServDocente);

        egresoServDocenteCDPRepository.save(egresoServDocenteCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosServDocentes guardarValoresEgresoCDP(EgresosServDocentes egresoServDocente,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            TipoCompensacion tipoCompensacion) {

        egresoServDocente.setNombreMateria(nombreMateria);
        egresoServDocente.setEsDocentePlanta(esDocentePlanta);
        egresoServDocente.setNombreDocente(nombreDocente);
        egresoServDocente.setEscalafon(escalafon);
        egresoServDocente.setTitulo(titulo);
        egresoServDocente.setHorasTeoricasMat(horasTeoricasMat);
        egresoServDocente.setHorasPracticasMat(horasPracticasMat);
        egresoServDocente.setValorHoraProfesor(valorHoraProfesor);
        egresoServDocente.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);
        egresoServDocente.setTotalPagoProfesor(valorHoraProfesor * egresoServDocente.getTotalHorasProfesor());
        egresoServDocente.setTipoCompensacion(tipoCompensacion);

        egresoServDocente.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoServDocente.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoServDocente;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCompensacion tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion).orElseThrow();

        EgresosServDocentes egresoServDocente = new EgresosServDocentes();

        egresoServDocente = guardarValoresEgresoCDP(egresoServDocente, nombreMateria, esDocentePlanta, nombreDocente,
                escalafon, titulo, horasTeoricasMat, horasPracticasMat, valorHoraProfesor, tipoCompensacion);

        egresoServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoServDocenteRepository.save(egresoServDocente);

        EgresoServDocenteCDP egresoServDocenteCdp = new EgresoServDocenteCDP();
        egresoServDocenteCdp.setCpc(cpc);
        egresoServDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoServDocenteCdp.setCdp(cdp);
        egresoServDocenteCdp.setEgresoServDocente(egresoServDocente);

        egresoServDocenteCDPRepository.save(egresoServDocenteCdp);

        return "OK";
    }
}
