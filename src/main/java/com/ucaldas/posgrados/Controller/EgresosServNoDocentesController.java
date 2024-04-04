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
import com.ucaldas.posgrados.Entity.EgresosServNoDocentes;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosServNoDocentesRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoServNoDocente")
public class EgresosServNoDocentesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosServNoDocentesRepository egresoServNoDocenteRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosServNoDocentes egresosDescuentos = new EgresosServNoDocentes();

            egresosDescuentos.setServicio(servicio);
            egresosDescuentos.setValorUnitario(valorUnitario);
            egresosDescuentos.setCantidad(cantidad);
            egresosDescuentos.setValorTotal(cantidad * valorUnitario);

            egresosDescuentos.setPresupuesto(presupuesto.get());
            egresosDescuentos.setTipoCosto(tipoCosto.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosDescuentos.setEjecucionPresupuestal(null);

            return "Egreso de servicio no docente guardado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosServNoDocentes> listar() {
        return egresoServNoDocenteRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosServNoDocentes> buscar(@RequestParam int id) {
        return egresoServNoDocenteRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam int idPresupuesto, @RequestParam String servicio, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosServNoDocentes> egreso = egresoServNoDocenteRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosServNoDocentes egresosDescuentosActualizado = egreso.get();

            egresosDescuentosActualizado.setServicio(servicio);
            egresosDescuentosActualizado.setValorUnitario(valorUnitario);
            egresosDescuentosActualizado.setCantidad(cantidad);
            egresosDescuentosActualizado.setValorTotal(cantidad * valorUnitario);

            egresosDescuentosActualizado.setTipoCosto(tipoCosto.get());
            egresosDescuentosActualizado.setPresupuesto(presupuesto.get());

            egresoServNoDocenteRepository.save(egresosDescuentosActualizado);
            return "Egreso de servicio no docente actualizado";
        } else {
            return "Error: Egreso de servicio no docente no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoServNoDocenteRepository.deleteById(id);
        return "Egreso de servicio no docente eliminado";
    }

}
