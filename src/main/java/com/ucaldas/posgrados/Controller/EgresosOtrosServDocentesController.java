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
import com.ucaldas.posgrados.Entity.TipoCosto;
import com.ucaldas.posgrados.Entity.EgresosOtrosServDocentes;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosServDocentesRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoOtrosServDocente")
public class EgresosOtrosServDocentesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosOtrosServDocentesRepository egresoOtrosServDocenteRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @PostMapping("/crearParaPresupuesto")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosOtrosServDocentes egresosOtrosServDocentes = new EgresosOtrosServDocentes();

            egresosOtrosServDocentes.setPresupuesto(presupuesto.get());
            egresosOtrosServDocentes.setTipoCosto(tipoCosto.get());
            egresosOtrosServDocentes.setServicio(servicio);
            egresosOtrosServDocentes.setDescripcion(descripcion);
            egresosOtrosServDocentes.setNumHoras(numHoras);
            egresosOtrosServDocentes.setValorTotal(valorTotal);

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosOtrosServDocentes.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosOtrosServDocentes().add(egresosOtrosServDocentes);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "Egreso de otros servicios docentes guardado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosOtrosServDocentes> listar() {
        return egresoOtrosServDocenteRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosOtrosServDocentes> buscar(@RequestParam int id) {
        return egresoOtrosServDocenteRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam int idPresupuesto, @RequestParam String servicio, @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal) {

        Optional<EgresosOtrosServDocentes> egreso = egresoOtrosServDocenteRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosOtrosServDocentes egresosOtrosServDocentesActualizado = egreso.get();
            egresosOtrosServDocentesActualizado.setServicio(servicio);
            egresosOtrosServDocentesActualizado.setDescripcion(descripcion);
            egresosOtrosServDocentesActualizado.setNumHoras(numHoras);
            egresosOtrosServDocentesActualizado.setValorTotal(valorTotal);

            egresosOtrosServDocentesActualizado.setTipoCosto(tipoCosto.get());
            egresosOtrosServDocentesActualizado.setPresupuesto(presupuesto.get());

            egresoOtrosServDocenteRepository.save(egresosOtrosServDocentesActualizado);
            return "Egreso de otros servicios docentes actualizado";
        } else {
            return "Error: Egreso de otros servicios docentes no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoOtrosServDocenteRepository.deleteById(id);
        return "Egreso de otros servicios docentes eliminado";
    }

}
