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
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.EgresosViajesRepository;

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

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String descripcion,
            @RequestParam int numPersonas,
            @RequestParam double apoyoDesplazamiento, @RequestParam int numViajesPorPersona,
            @RequestParam double valorTransporte) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosViajes egresoViaje = new EgresosViajes();

            egresoViaje.setDescripcion(descripcion);
            egresoViaje.setNumPersonas(numPersonas);
            egresoViaje.setApoyoDesplazamiento(apoyoDesplazamiento);
            egresoViaje.setNumViajesPorPersona(numViajesPorPersona);
            egresoViaje.setValorTransporte(valorTransporte);
            egresoViaje.setValorTotal((valorTransporte * numPersonas * numViajesPorPersona) + apoyoDesplazamiento);

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresoViaje.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosViaje().add(egresoViaje);

            int idPresupuesto = egresoViaje.getPresupuesto().getId();
            double valorNuevo = egresoViaje.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "Egreso de viaje guardado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
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

            int idPresupuesto = egresoViajeActualizado.getPresupuesto().getId();
            double valorNuevo = egresoViajeActualizado.getValorTotal();

            egresoViajeRepository.save(egresoViajeActualizado);

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);
            return "Egreso de viaje actualizado";
        } else {
            return "Error: Egreso de viaje o Presupuesto no encontrado";
        }
    }

    // Antes de eliminar el egreso, se debe actualizar el total de egresos del
    // presupuesto
    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        Optional<EgresosViajes> egresoViaje = egresoViajeRepository.findById(id);

        int idPresupuesto = egresoViaje.get().getPresupuesto().getId();
        double valorAnterior = egresoViaje.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoViajeRepository.deleteById(id);
        return "Egreso de viaje eliminado";
    }

}