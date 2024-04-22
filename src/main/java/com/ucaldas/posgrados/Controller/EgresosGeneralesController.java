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
import com.ucaldas.posgrados.Entity.EgresosGenerales;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCostoRepository;
import com.ucaldas.posgrados.Repository.EgresosGeneralesRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoGeneral")
public class EgresosGeneralesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosGeneralesRepository egresoGeneralRepository;

    @Autowired
    private TipoCostoRepository tipoCostoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    /*
     * 
     * @Autowired
     * private EjecucionPresupuestalRepository ejecucionPresupuestalRepository;
     * 
     * 
     * @Autowired
     * private EjecucionPresupuestalController ejecucionPresupuestalController;
     */

    /**
     * Crea un egreso general para un presupuesto o una ejecución presupuestal
     * 
     * @param idPresupuestoEjecucion recibe ya sea el id del presupuesto o de la
     *                               ejecución presupuestal (para reutilizar el
     *                               metodo crear)
     * @param concepto
     * @param valorUnitario
     * @param cantidad
     * @param idTipoCosto
     * @return
     */
    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        // Optional<EjecucionPresupuestal> ejecucion =
        // ejecucionPresupuestalRepository.findById(idPresupuestoEjecucion);

        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si el presupuesto o la ejecución existen (XOR) y si el tipo de
        // costo existe
        // if ((presupuesto.isPresent() && tipoCosto.isPresent()) ^
        // (ejecucion.isPresent() && tipoCosto.isPresent())) {
        if (presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosGenerales egresosGenerales = new EgresosGenerales();
            egresosGenerales.setConcepto(concepto);
            egresosGenerales.setValorUnitario(valorUnitario);
            egresosGenerales.setCantidad(cantidad);
            egresosGenerales.setValorTotal(cantidad * valorUnitario);

            egresosGenerales.setPresupuesto(presupuesto.get());
            egresosGenerales.setTipoCosto(tipoCosto.get());

            // if (presupuesto.isPresent()) {
            // Si el gasto hace parte de un presupuesto, la ejecución debe ser null siempre
            egresosGenerales.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosGenerales().add(egresosGenerales);

            int idPresupuesto = egresosGenerales.getPresupuesto().getId();
            double valorNuevo = egresosGenerales.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());
            /*
             * } else {
             * // Si el gasto hace parte de una ejecución, el presupuesto debe ser null
             * siempre
             * egresosGenerales.setPresupuesto(null);
             * 
             * // Guardar el egreso general en la ejecución
             * ejecucion.get().getEgresosGenerales().add(egresosGenerales);
             * presupuestoController.actualizarEgresosProgramaTotales(presupuesto.get().
             * getId());
             * 
             * // Guardar el Presupuesto actualizado
             * presupuestoRepository.save(presupuesto.get());
             * }
             */

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosGenerales> listar() {
        return egresoGeneralRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosGenerales> buscar(@RequestParam int id) {
        return egresoGeneralRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCosto,
            @RequestParam String concepto, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosGenerales> egreso = egresoGeneralRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {
            double valorAnterior = egreso.get().getValorTotal();

            EgresosGenerales egresosGeneralesActualizado = egreso.get();
            egresosGeneralesActualizado.setConcepto(concepto);
            egresosGeneralesActualizado.setValorUnitario(valorUnitario);
            egresosGeneralesActualizado.setCantidad(cantidad);
            egresosGeneralesActualizado.setValorTotal(cantidad * valorUnitario);

            egresosGeneralesActualizado.setTipoCosto(tipoCosto.get());
            int idPresupuesto = egresosGeneralesActualizado.getPresupuesto().getId();

            double valorNuevo = egresosGeneralesActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoGeneralRepository.save(egresosGeneralesActualizado);
            return "OK";
        } else {
            return "Error: Egreso general no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosGenerales> egreso = egresoGeneralRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso general no encontrado";
        }

        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoGeneralRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosGenerales> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoGeneralRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/totalEgresosGenerales")
    public @ResponseBody double totalEgresosGenerales() {
        double total = 0;
        Iterable<EgresosGenerales> egresosGenerales = egresoGeneralRepository.findAll();

        // Si no hay egresos generales
        if (!egresosGenerales.iterator().hasNext()) {
            return total;
        }
        for (EgresosGenerales egresoGeneral : egresosGenerales) {
            total += egresoGeneral.getValorTotal();
        }
        return total;
    }

}
