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

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EgresosTransferenciasController egresosTransferenciasController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String concepto,
            @RequestParam double valor) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);

        // Verificar si la programa existe
        if (presupuesto.isPresent()) {

            Ingresos ingreso = new Ingresos();
            ingreso.setConcepto(concepto);
            ingreso.setValor(valor);
            ingreso.setPresupuesto(presupuesto.get());
            ingreso.setFechaHoraCreacion(java.time.LocalDateTime.now().toString());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            ingreso.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getIngresos().add(ingreso);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = ingreso.getValor();

            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, 0, "ingreso");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();

            }

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<Ingresos> listar() {
        return ingresoRepository.findAllByOrderByPresupuestoAsc();
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<Ingresos> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return ingresoRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<Ingresos> buscar(@RequestParam int id) {
        return ingresoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String concepto,
            @RequestParam double valor) {
        Optional<Ingresos> ingreso = ingresoRepository.findById(id);

        if (ingreso.isPresent()) {

            double valorAnterior = ingreso.get().getValor();
            Ingresos ingresoActualizado = ingreso.get();
            ingresoActualizado.setConcepto(concepto);
            ingresoActualizado.setValor(valor);
            ingresoActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            int idPresupuesto = ingresoActualizado.getPresupuesto().getId();
            double valorNuevo = ingresoActualizado.getValor();
            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, valorAnterior, "ingreso");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
            }

            ingresoRepository.save(ingresoActualizado);

            return "OK";
        } else {
            return "Error: Ingreso o Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<Ingresos> ingreso = ingresoRepository.findById(id);

        if (!ingreso.isPresent()) {
            return "Error: Ingreso no encontrado";
        }

        int idPresupuesto = ingreso.get().getPresupuesto().getId();
        double valorAnterior = ingreso.get().getValor();

        presupuestoController.actualizarIngresosTotales(idPresupuesto, 0, valorAnterior, "ingreso");

        // Si existen egresos de transferencias entonces llamamos al metodo
        // 'actualizarIngresosTotales'
        if (egresosTransferenciasController.listar().iterator().hasNext()) {
            egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
        }
        ingresoRepository.deleteById(id);

        return "OK";
    }

    @GetMapping("/totalIngresos")
    public @ResponseBody double totalIngresos(int idPresupuesto) {
        double totalIngresos = 0;
        Iterable<Ingresos> ingresos = ingresoRepository.findByPresupuestoId(idPresupuesto);
        if (!ingresos.iterator().hasNext()) {
            return totalIngresos;
        }
        for (Ingresos ingreso : ingresos) {
            totalIngresos += ingreso.getValor();
        }
        return totalIngresos;
    }

}
