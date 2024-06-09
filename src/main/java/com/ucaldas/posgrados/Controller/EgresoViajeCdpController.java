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
import com.ucaldas.posgrados.Entity.EgresoViajeCDP;
import com.ucaldas.posgrados.Entity.EgresosViajes;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.CdpRepository;
import com.ucaldas.posgrados.Repository.EgresoViajeCdpRepository;
import com.ucaldas.posgrados.Repository.EgresosViajesRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoViajeCdp")
public class EgresoViajeCdpController {

    @Autowired
    private CdpRepository cdpRepository;

    @Autowired
    private EgresoViajeCdpRepository egresoViajeCDPRepository;

    @Autowired
    private EgresosViajesRepository egresoViajeRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<EgresoViajeCDP> listar() {
        return egresoViajeCDPRepository.findAll();
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
     * La descripción no se puede modificar.
     */
    @PostMapping("/crearEgresoCDPDelPresupuesto")
    public @ResponseBody String crearEgresoCDPDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, @RequestParam int idEgresoDelPresupuesto,
            @RequestParam String descripcionEgresoCDP, @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        EgresosViajes egresoViaje = new EgresosViajes();

        egresoViaje = guardarValoresEgresoCDP(egresoViaje, descripcion, numPersonas, apoyoDesplazamiento,
                numViajesPorPersona, valorTransporte);

        EgresosViajes egresoDelPresupuesto = egresoViajeRepository.findById(idEgresoDelPresupuesto)
                .orElseThrow();

        // Si el egreso ya fue utilizado en la ejecución presupuestal, entonces no se
        // puede volver a utilizar
        if (egresoDelPresupuesto.getEtiquetaEgresoIngreso() == EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION) {
            return "ERROR: Ya se creó un CDP con este egreso";
        }

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getNumPersonas() == numPersonas
                && egresoDelPresupuesto.getApoyoDesplazamiento() == apoyoDesplazamiento
                && egresoDelPresupuesto.getNumViajesPorPersona() == numViajesPorPersona
                && egresoDelPresupuesto.getValorTransporte() == valorTransporte) {
            egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoViajeRepository.save(egresoViaje);

        // Marcar cómo utilizado para que no se pueda volver a utilizar
        egresoDelPresupuesto.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.UTILIZADOENLAEJECUCION);
        egresoViajeRepository.save(egresoDelPresupuesto);

        // Después de creado el egreso general, se puede pasar como atributo al egreso
        // general CDP pues en la base de datos se guardará el id del egreso general

        EgresoViajeCDP egresoViajeCdp = new EgresoViajeCDP();
        egresoViajeCdp.setCpc(cpc);
        egresoViajeCdp.setDescripcion(descripcionEgresoCDP);
        egresoViajeCdp.setCdp(cdp);
        egresoViajeCdp.setEgresoViaje(egresoViaje);

        egresoViajeCDPRepository.save(egresoViajeCdp);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosViajes guardarValoresEgresoCDP(EgresosViajes egresoViaje,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte) {

        egresoViaje.setDescripcion(descripcion);
        egresoViaje.setNumPersonas(numPersonas);
        egresoViaje.setApoyoDesplazamiento(apoyoDesplazamiento);
        egresoViaje.setNumViajesPorPersona(numViajesPorPersona);
        egresoViaje.setValorTransporte(valorTransporte);
        egresoViaje.setValorTotal(numPersonas * apoyoDesplazamiento * numViajesPorPersona * valorTransporte);

        egresoViaje.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear() + " "
                + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                + java.time.LocalDateTime.now().getSecond());
        egresoViaje.setFechaHoraUltimaModificacion("No ha sido modificado");
        return egresoViaje;
    }

    /*
     * Se crea un egreso en la ejecución presupuestal de un elemento que no se tuvo
     * en cuenta en el presupuesto. (Valores en blanco)
     */
    @PostMapping("/crearEgresoCDPFueraDelPresupuesto")
    public @ResponseBody String crearEgresoCDPFueraDelPresupuesto(@RequestParam int idCdp,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, @RequestParam String descripcionEgresoCDP,
            @RequestParam String cpc) {

        Cdp cdp = cdpRepository.findById(idCdp)
                .orElseThrow();

        EgresosViajes egresoViaje = new EgresosViajes();

        egresoViaje = guardarValoresEgresoCDP(egresoViaje, descripcion, numPersonas, apoyoDesplazamiento,
                numViajesPorPersona, valorTransporte);
        egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoViajeRepository.save(egresoViaje);

        EgresoViajeCDP egresoViajeCdp = new EgresoViajeCDP();
        egresoViajeCdp.setCpc(cpc);
        egresoViajeCdp.setDescripcion(descripcionEgresoCDP);
        egresoViajeCdp.setCdp(cdp);
        egresoViajeCdp.setEgresoViaje(egresoViaje);

        egresoViajeCDPRepository.save(egresoViajeCdp);

        return "OK";
    }
}
