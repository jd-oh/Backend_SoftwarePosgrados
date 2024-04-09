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

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
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

            int idPresupuesto = egresosOtros.getPresupuesto().getId();
            double valorNuevo = egresosOtros.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
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
            @RequestParam String concepto, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosOtros> egreso = egresoOtroRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosOtros egresosOtrosActualizado = egreso.get();
            egresosOtrosActualizado.setConcepto(concepto);
            egresosOtrosActualizado.setValorUnitario(valorUnitario);
            egresosOtrosActualizado.setCantidad(cantidad);
            egresosOtrosActualizado.setValorTotal(cantidad * valorUnitario);

            egresosOtrosActualizado.setTipoCosto(tipoCosto.get());

            int idPresupuesto = egresosOtrosActualizado.getPresupuesto().getId();
            double valorNuevo = egresosOtrosActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);
            egresoOtroRepository.save(egresosOtrosActualizado);
            return "OK";
        } else {
            return "Error: Egreso de otros no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        Optional<EgresosOtros> egreso = egresoOtroRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de otros no encontrado";
        }
        double valorAnterior = egreso.get().getValorTotal();
        int idPresupuesto = egreso.get().getPresupuesto().getId();

        egresoOtroRepository.deleteById(id);

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        return "OK";
    }

}
