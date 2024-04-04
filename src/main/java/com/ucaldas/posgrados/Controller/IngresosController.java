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
import com.ucaldas.posgrados.Entity.Ingresos;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.IngresosRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/ingreso")
public class IngresosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private IngresosRepository ingresoRepository;

    @PostMapping("/crearParaPresupuesto")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String concepto,
            @RequestParam double valor) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            Ingresos ingreso = new Ingresos();
            ingreso.setConcepto(concepto);
            ingreso.setValor(valor);
            ingreso.setPresupuesto(presupuesto.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            ingreso.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getIngresos().add(ingreso);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "Ingreso guardado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<Ingresos> listar() {
        return ingresoRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<Ingresos> buscar(@RequestParam int id) {
        return ingresoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String concepto,
            @RequestParam double valor,
            @RequestParam int idPresupuesto) {
        Optional<Ingresos> ingreso = ingresoRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);

        if (ingreso.isPresent() && presupuesto.isPresent()) {
            Ingresos ingresoActualizado = ingreso.get();
            ingresoActualizado.setConcepto(concepto);
            ingresoActualizado.setValor(valor);
            ingresoActualizado.setPresupuesto(presupuesto.get());

            ingresoRepository.save(ingresoActualizado);
            return "Ingreso actualizado";
        } else {
            return "Error: Ingreso o Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        ingresoRepository.deleteById(id);
        return "Ingreso eliminado";
    }

}
