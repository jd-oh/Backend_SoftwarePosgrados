package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Entity.EgresosViajes;
import com.ucaldas.posgrados.Entity.EjecucionPresupuestal;
import com.ucaldas.posgrados.Entity.EtiquetaEgresoIngreso;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.EgresosViajesRepository;
import com.ucaldas.posgrados.Repository.EjecucionPresupuestalRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoViaje")
public class EgresosViajesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosViajesRepository egresoViajeRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;

    // Crear un egreso de viaje en un presupuesto
    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte) {

        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosViajes egresoViaje = new EgresosViajes();

            egresoViaje.setDescripcion(descripcion);
            egresoViaje.setNumPersonas(numPersonas);
            egresoViaje.setApoyoDesplazamiento(apoyoDesplazamiento);
            egresoViaje.setNumViajesPorPersona(numViajesPorPersona);
            egresoViaje.setValorTransporte(valorTransporte);
            egresoViaje.setPresupuesto(presupuesto.get());
            egresoViaje.setValorTotal((valorTransporte * numPersonas * numViajesPorPersona) + apoyoDesplazamiento);

            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresoViaje.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresoViaje.setFechaHoraUltimaModificacion("No ha sido modificado");

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no.
            // La etiqueta también es nula porque se usa en la ejecución presupuestal
            egresoViaje.setEjecucionPresupuestal(null);
            egresoViaje.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosViaje().add(egresoViaje);

            int idPresupuesto = idPresupuestoEjecucion;
            double valorNuevo = egresoViaje.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
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
     */
    @PostMapping("/crearEgresoEjecucionDelPresupuesto")
    public @ResponseBody String crearEgresoEjecucionDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, @RequestParam int idEgreso) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        EgresosViajes egresoViaje = new EgresosViajes();

        egresoViaje = guardarValoresEgresoEjecucion(egresoViaje, descripcion, numPersonas, apoyoDesplazamiento,
                numViajesPorPersona, valorTransporte, ejecucionPresupuestal);

        EgresosViajes egresoDelPresupuesto = egresoViajeRepository.findById(idEgreso).orElseThrow();

        // Si todos los datos del egresoDelPresupuesto son iguales a los que se quieren
        // guardar en este nuevo egreso, entonces poner la etiqueta MISMOVALOR, pues
        // significa que es el mismo que se presupuestó
        if (egresoDelPresupuesto.getDescripcion().equals(descripcion)
                && egresoDelPresupuesto.getNumPersonas() == numPersonas
                && egresoDelPresupuesto.getApoyoDesplazamiento() == apoyoDesplazamiento
                && egresoDelPresupuesto.getNumViajesPorPersona() == numViajesPorPersona
                && egresoDelPresupuesto.getValorTransporte() == valorTransporte) {
            egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_MISMOVALOR);
        } else {
            egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.DELPRESUPUESTO_OTROVALOR);
        }

        egresoViajeRepository.save(egresoViaje);

        return "OK";
    }

    /*
     * Guarda los valores en un objeto del tipo del egreso.
     */

    private EgresosViajes guardarValoresEgresoEjecucion(EgresosViajes egresoViaje,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte, EjecucionPresupuestal ejecucionPresupuestal) {

        egresoViaje.setEjecucionPresupuestal(ejecucionPresupuestal);
        egresoViaje.setPresupuesto(null);
        egresoViaje.setDescripcion(descripcion);
        egresoViaje.setNumPersonas(numPersonas);
        egresoViaje.setApoyoDesplazamiento(apoyoDesplazamiento);
        egresoViaje.setNumViajesPorPersona(numViajesPorPersona);
        egresoViaje.setValorTransporte(valorTransporte);
        egresoViaje.setValorTotal((valorTransporte * numPersonas * numViajesPorPersona) + apoyoDesplazamiento);

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
    @PostMapping("/crearEgresoFueraDelPresupuesto")
    public @ResponseBody String crearEgresoFueraDelPresupuesto(@RequestParam int idEjecucionPresupuestal,
            @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte) {

        EjecucionPresupuestal ejecucionPresupuestal = ejecucionPresupuestalRepository.findById(idEjecucionPresupuestal)
                .orElseThrow();

        EgresosViajes egresoViaje = new EgresosViajes();

        egresoViaje = guardarValoresEgresoEjecucion(egresoViaje, descripcion, numPersonas, apoyoDesplazamiento,
                numViajesPorPersona, valorTransporte, ejecucionPresupuestal);

        egresoViaje.setEtiquetaEgresoIngreso(EtiquetaEgresoIngreso.FUERADELPRESUPUESTO);

        egresoViajeRepository.save(egresoViaje);

        return "OK";
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosViajes> listar() {
        return egresoViajeRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosViajes> buscar(@RequestParam int id) {
        return egresoViajeRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte) {
        Optional<EgresosViajes> egresoViaje = egresoViajeRepository.findById(id);

        if (egresoViaje.isPresent()) {

            double valorAnterior = egresoViaje.get().getValorTotal();

            EgresosViajes egresoViajeActualizado = egresoViaje.get();

            egresoViajeActualizado.setDescripcion(descripcion);
            egresoViajeActualizado.setNumPersonas(numPersonas);
            egresoViajeActualizado.setApoyoDesplazamiento(apoyoDesplazamiento);
            egresoViajeActualizado.setNumViajesPorPersona(numViajesPorPersona);
            egresoViajeActualizado.setValorTransporte(valorTransporte);
            egresoViajeActualizado
                    .setValorTotal((valorTransporte * numPersonas * numViajesPorPersona) + apoyoDesplazamiento);

            egresoViajeActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());
            int idPresupuesto = egresoViajeActualizado.getPresupuesto().getId();
            double valorNuevo = egresoViajeActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoViajeRepository.save(egresoViajeActualizado);
            return "OK";
        } else {
            return "Error: Egreso de viaje o Presupuesto no encontrado";
        }
    }

    // Antes de eliminar el egreso, se debe actualizar el total de egresos del
    // presupuesto
    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        Optional<EgresosViajes> egresoViaje = egresoViajeRepository.findById(id);

        if (!egresoViaje.isPresent()) {
            return "Error: Egreso de viaje no encontrado";
        }

        int idPresupuesto = egresoViaje.get().getPresupuesto().getId();
        double valorAnterior = egresoViaje.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoViajeRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosViajes> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoViajeRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/totalEgresosViajes")
    // Total de egresos de viajes por presupuesto
    public @ResponseBody double totalEgresosViajes(int idPresupuesto) {

        Iterable<EgresosViajes> egresosViajes = egresoViajeRepository.findByPresupuestoId(idPresupuesto);
        double total = 0;

        if (!egresosViajes.iterator().hasNext()) {
            return total;
        }

        for (EgresosViajes egresoViaje : egresosViajes) {
            total += egresoViaje.getValorTotal();
        }

        return total;
    }

    // Este es para la ejecución presupuestal
    @GetMapping("/totalEgresosViajesEjecucion")
    // Total de egresos de viajes por ejecución presupuestal
    public @ResponseBody double totalEgresosViajesEjecucion(int idEjecucionPresupuestal) {

        Iterable<EgresosViajes> egresosViajes = egresoViajeRepository
                .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
        double total = 0;

        if (!egresosViajes.iterator().hasNext()) {
            return total;
        }

        for (EgresosViajes egresoViaje : egresosViajes) {
            total += egresoViaje.getValorTotal();
        }

        return total;
    }

}
