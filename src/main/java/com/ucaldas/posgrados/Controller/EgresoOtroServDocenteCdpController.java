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
import com.ucaldas.posgrados.Entity.EgresoOtroServDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoOtroServDocenteCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosServDocentesRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoOtroServDocenteCdp")
public class EgresoOtroServDocenteCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoOtroServDocenteCdpRepository egresoOtroServDocenteCDPRepository;

    @Autowired
    private EgresosOtrosServDocentesRepository egresoOtroServDocenteRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoOtroServDocenteCDP> listar() {
        return egresoOtroServDocenteCDPRepository.findAll();
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
     * El servicio no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosOtrosServDocentes egresoOtroServDocente = new EgresosOtrosServDocentes();

        egresoOtroServDocente = guardarValoresEgresoCDP(egresoOtroServDocente, servicio, descripcion, numHoras,
                valorTotal,
                tipoCosto);

        EgresosOtrosServDocentes egresoDelPresupuesto = egresoOtroServDocenteRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getServicio().equals(servicio)
                && egresoDelPresupuesto.getDescripcion().equals(descripcion)
                && egresoDelPresupuesto.getNumHoras() == numHoras
                && egresoDelPresupuesto.getValorTotal() == valorTotal
                && egresoDelPresupuesto.getTipoCosto().equals(tipoCosto)) {
            egresoOtroServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoOtroServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoOtroServDocenteRepository.save(egresoOtroServDocente);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoOtroServDocenteRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoOtroServDocenteCDP egresoOtroServDocenteCdp = new EgresoOtroServDocenteCDP();
        egresoOtroServDocenteCdp.setCpc(cpc);
        egresoOtroServDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoOtroServDocenteCdp.setCdp(cdp);
        egresoOtroServDocenteCdp.setEgresoOtroServDocente(egresoOtroServDocente);

        egresoOtroServDocenteCDPRepository.save(egresoOtroServDocenteCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosOtrosServDocentes guardarValoresEgresoCDP(EgresosOtrosServDocentes egresoOtroServDocente,
            @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal, TipoCosto tipoCosto) {

        egresoOtroServDocente.setServicio(servicio);
        egresoOtroServDocente.setDescripcion(descripcion);
        egresoOtroServDocente.setNumHoras(numHoras);
        egresoOtroServDocente.setValorTotal(valorTotal);
        egresoOtroServDocente.setTipoCosto(tipoCosto);

        egresoOtroServDocente.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoOtroServDocente.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoOtroServDocente;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosOtrosServDocentes egresoOtroServDocente = new EgresosOtrosServDocentes();

        egresoOtroServDocente = guardarValoresEgresoCDP(egresoOtroServDocente, servicio, descripcion, numHoras,
                valorTotal,
                tipoCosto);

        egresoOtroServDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoOtroServDocenteRepository.save(egresoOtroServDocente);

        EgresoOtroServDocenteCDP egresoOtroServDocenteCdp = new EgresoOtroServDocenteCDP();
        egresoOtroServDocenteCdp.setCpc(cpc);
        egresoOtroServDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoOtroServDocenteCdp.setCdp(cdp);
        egresoOtroServDocenteCdp.setEgresoOtroServDocente(egresoOtroServDocente);

        egresoOtroServDocenteCDPRepository.save(egresoOtroServDocenteCdp);

        return "OK";
    }
}
