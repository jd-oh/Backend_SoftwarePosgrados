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
import com.ucaldas.posgrados.Entity.EgresosRecurrentesAdm;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.EgresosRecurrentesAdmRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoRecurrenteAdm")
public class EgresosRecurrentesAdmController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosRecurrentesAdmRepository egresoRecurrenteAdmRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosRecurrentesAdm egresoRecurrenteAdm = new EgresosRecurrentesAdm();

            egresoRecurrenteAdm.setUnidad(unidad);
            egresoRecurrenteAdm.setCargo(cargo);
            egresoRecurrenteAdm.setValorHora(valorHora);
            egresoRecurrenteAdm.setNumHoras(numHoras);
            egresoRecurrenteAdm.setValorTotal(valorHora * numHoras);

            egresoRecurrenteAdm.setPresupuesto(presupuesto.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresoRecurrenteAdm.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosRecurrentesAdm().add(egresoRecurrenteAdm);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = egresoRecurrenteAdm.getValorTotal();

            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosRecurrentesAdm> listar() {
        return egresoRecurrenteAdmRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosRecurrentesAdm> buscar(@RequestParam int id) {
        return egresoRecurrenteAdmRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String unidad,
            @RequestParam String cargo,
            @RequestParam double valorHora, @RequestParam int numHoras) {
        Optional<EgresosRecurrentesAdm> egresoRecurrenteAdm = egresoRecurrenteAdmRepository.findById(id);

        if (egresoRecurrenteAdm.isPresent()) {

            double valorAnterior = egresoRecurrenteAdm.get().getValorTotal();

            EgresosRecurrentesAdm egresoRecurrenteAdmActualizado = egresoRecurrenteAdm.get();
            egresoRecurrenteAdmActualizado.setUnidad(unidad);
            egresoRecurrenteAdmActualizado.setCargo(cargo);
            egresoRecurrenteAdmActualizado.setValorHora(valorHora);
            egresoRecurrenteAdmActualizado.setNumHoras(numHoras);
            egresoRecurrenteAdmActualizado.setValorTotal(valorHora * numHoras);

            int idPresupuesto = egresoRecurrenteAdmActualizado.getPresupuesto().getId();
            double valorNuevo = egresoRecurrenteAdmActualizado.getValorTotal();
            presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, valorNuevo,
                    valorAnterior);

            egresoRecurrenteAdmRepository.save(egresoRecurrenteAdmActualizado);
            return "OK";
        } else {
            return "Error: Egreso recurrente administracion o Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosRecurrentesAdm> egresoRecurrenteAdm = egresoRecurrenteAdmRepository.findById(id);

        if (!egresoRecurrenteAdm.isPresent()) {
            return "Error: Egreso recurrente administracion no encontrado";
        }
        int idPresupuesto = egresoRecurrenteAdm.get().getPresupuesto().getId();
        double valorAnterior = egresoRecurrenteAdm.get().getValorTotal();
        presupuestoController.actualizarEgresosRecurrentesUniversidadTotales(idPresupuesto, 0, valorAnterior);
        egresoRecurrenteAdmRepository.deleteById(id);
        return "OK";
    }

}
