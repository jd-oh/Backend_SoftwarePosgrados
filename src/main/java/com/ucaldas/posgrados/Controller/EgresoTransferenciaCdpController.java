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
import com.ucaldas.posgrados.Entity.EgresoTransferenciaCDP;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoTransferenciaCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosTransferenciasRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoTransferenciaCdp")
public class EgresoTransferenciaCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoTransferenciaCdpRepository egresoTransferenciaCDPRepository;

    @Autowired
    private EgresosTransferenciasRepository egresoTransferenciaRepository;

    @Autowired
    private TipoTransferenciaRepository tipoTransferenciaRepository;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoTransferenciaCDP> listar() {
        return egresoTransferenciaCDPRepository.findAll();
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
     * Descripcion no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoTransferencia tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia).orElseThrow();

        EgresosTransferencias egresoTransferencia = new EgresosTransferencias();

        egresoTransferencia = guardarValoresEgresoCDP(egresoTransferencia, descripcion, porcentaje, tipoTransferencia,
                cdp);

        EgresosTransferencias egresoDelPresupuesto = egresoTransferenciaRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getDescripcion().equals(descripcion)
                && egresoDelPresupuesto.getPorcentaje() == porcentaje
                && egresoDelPresupuesto.getTipoTransferencia().getId() == idTipoTransferencia) {
            egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoTransferenciaRepository.save(egresoTransferencia);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoTransferenciaRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoTransferenciaCDP egresoTransferenciaCdp = new EgresoTransferenciaCDP();
        egresoTransferenciaCdp.setCpc(cpc);
        egresoTransferenciaCdp.setDescripcion(descripcionEgresoCDP);
        egresoTransferenciaCdp.setCdp(cdp);
        egresoTransferenciaCdp.setEgresoTransferencia(egresoTransferencia);

        egresoTransferenciaCDPRepository.save(egresoTransferenciaCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosTransferencias guardarValoresEgresoCDP(EgresosTransferencias egresoTransferencia,
            @RequestParam String descripcion,
            @RequestParam double porcentaje, TipoTransferencia tipoTransferencia, Cdp cdp) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository
                .findById(cdp.getEjecucionPresupuestal().getId()).orElseThrow();

        egresoTransferencia.setDescripcion(descripcion);
        egresoTransferencia.setPorcentaje(porcentaje);
        egresoTransferencia.setValorTotal(ejecucionPresupuestal.getIngresosTotalesEjecucion() * porcentaje / 100);
        egresoTransferencia.setTipoTransferencia(tipoTransferencia);

        egresoTransferencia.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoTransferencia.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoTransferencia;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        TipoTransferencia tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia).orElseThrow();

        EgresosTransferencias egresoTransferencia = new EgresosTransferencias();

        egresoTransferencia = guardarValoresEgresoCDP(egresoTransferencia, descripcion, porcentaje, tipoTransferencia,
                cdp);
        egresoTransferencia.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoTransferenciaRepository.save(egresoTransferencia);

        EgresoTransferenciaCDP egresoTransferenciaCdp = new EgresoTransferenciaCDP();
        egresoTransferenciaCdp.setCpc(cpc);
        egresoTransferenciaCdp.setDescripcion(descripcionEgresoCDP);
        egresoTransferenciaCdp.setCdp(cdp);
        egresoTransferenciaCdp.setEgresoTransferencia(egresoTransferencia);

        egresoTransferenciaCDPRepository.save(egresoTransferenciaCdp);

        return "OK";
    }
}
