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
import com.ucaldas.posgrados.Entity.EgresosOtros;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosOtrosRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoOtro")
public class EgresosOtrosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosOtrosRepository egresoOtroRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @PostMapping("/crearParaPresupuesto")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {
            EgresosOtros egresosOtros = new EgresosOtros();
            egresosOtros.setConcepto(concepto);
            egresosOtros.setValorUnitario(valorUnitario);
            egresosOtros.setCantidad(cantidad);
            egresosOtros.setValorTotal(cantidad * valorUnitario);

            egresosOtros.setPresupuesto(presupuesto.get());
            egresosOtros.setTipoCosto(tipoCosto.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosOtros.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosOtros().add(egresosOtros);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "Egreso de otros guardado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosOtros> listar() {
        return egresoOtroRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosOtros> buscar(@RequestParam int id) {
        return egresoOtroRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam int idPresupuesto, @RequestParam String concepto, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosOtros> egreso = egresoOtroRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosOtros egresosOtrosActualizado = egreso.get();
            egresosOtrosActualizado.setConcepto(concepto);
            egresosOtrosActualizado.setValorUnitario(valorUnitario);
            egresosOtrosActualizado.setCantidad(cantidad);
            egresosOtrosActualizado.setValorTotal(cantidad * valorUnitario);

            egresosOtrosActualizado.setTipoCosto(tipoCosto.get());
            egresosOtrosActualizado.setPresupuesto(presupuesto.get());

            egresoOtroRepository.save(egresosOtrosActualizado);
            return "Egreso de otros actualizado";
        } else {
            return "Error: Egreso de otros no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoOtroRepository.deleteById(id);
        return "Egreso de otros eliminado";
    }

}
