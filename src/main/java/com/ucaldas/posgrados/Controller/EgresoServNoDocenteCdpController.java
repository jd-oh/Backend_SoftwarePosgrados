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
import com.ucaldas.posgrados.Entity.EgresoServNoDocenteCDP;
import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoServNoDocenteCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosServNoDocentesRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoServNoDocenteCdp")
public class EgresoServNoDocenteCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoServNoDocenteCdpRepository egresoServNoDocenteCDPRepository;

    @Autowired
    private EgresosServNoDocentesRepository egresoServNoDocenteRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoServNoDocenteCDP> listar() {
        return egresoServNoDocenteCDPRepository.findAll();
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
     * Servicio no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosServNoDocentes egresoServNoDocente = new EgresosServNoDocentes();

        egresoServNoDocente = guardarValoresEgresoCDP(egresoServNoDocente, servicio, valorUnitario, cantidad,
                tipoCosto);

        EgresosServNoDocentes egresoDelPresupuesto = egresoServNoDocenteRepository.findById(idEgresoDelPresupuesto)
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
                && egresoDelPresupuesto.getValorUnitario() == valorUnitario
                && egresoDelPresupuesto.getCantidad() == cantidad) {
            egresoServNoDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoServNoDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoServNoDocenteRepository.save(egresoServNoDocente);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoServNoDocenteRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoServNoDocenteCDP egresoServNoDocenteCdp = new EgresoServNoDocenteCDP();
        egresoServNoDocenteCdp.setCpc(cpc);
        egresoServNoDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoServNoDocenteCdp.setCdp(cdp);
        egresoServNoDocenteCdp.setEgresoServNoDocente(egresoServNoDocente);

        egresoServNoDocenteCDPRepository.save(egresoServNoDocenteCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosServNoDocentes guardarValoresEgresoCDP(EgresosServNoDocentes egresoServNoDocente,
            @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, TipoCosto tipoCosto) {

        egresoServNoDocente.setServicio(servicio);
        egresoServNoDocente.setValorUnitario(valorUnitario);
        egresoServNoDocente.setCantidad(cantidad);
        egresoServNoDocente.setValorTotal(valorUnitario * cantidad);
        egresoServNoDocente.setTipoCosto(tipoCosto);

        egresoServNoDocente.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoServNoDocente.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoServNoDocente;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad,
            @RequestParam int idTipoCosto, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoCosto tipoCosto = tipoCostoRepository.findById(idTipoCosto).orElseThrow();

        EgresosServNoDocentes egresoServNoDocente = new EgresosServNoDocentes();

        egresoServNoDocente = guardarValoresEgresoCDP(egresoServNoDocente, servicio, valorUnitario, cantidad,
                tipoCosto);

        egresoServNoDocente.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoServNoDocenteRepository.save(egresoServNoDocente);

        EgresoServNoDocenteCDP egresoServNoDocenteCdp = new EgresoServNoDocenteCDP();
        egresoServNoDocenteCdp.setCpc(cpc);
        egresoServNoDocenteCdp.setDescripcion(descripcionEgresoCDP);
        egresoServNoDocenteCdp.setCdp(cdp);
        egresoServNoDocenteCdp.setEgresoServNoDocente(egresoServNoDocente);

        egresoServNoDocenteCDPRepository.save(egresoServNoDocenteCdp);

        return "OK";
    }
}
