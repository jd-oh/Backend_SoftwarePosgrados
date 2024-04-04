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

import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.Presupuesto;
import com.ucaldas.posgrados.Repository.CohorteRepository;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/presupuesto")
public class PresupuestoController {

    @Autowired
    private CohorteRepository cohorteRepository;

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idCohorte, @RequestParam String observaciones) {
        // Buscar la programa por su ID
        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        // Verificar si la programa existe
        if (cohorte.isPresent()) {
            Presupuesto presupuesto = new Presupuesto();

            presupuesto.setObservaciones(observaciones);
            presupuesto.setIngresosTotales(0);
            presupuesto.setEgresosProgramaTotales(0);
            presupuesto.setEgresosRecurrentesUniversidadTotales(0);

            // Siempre se crea como borrador, para que puedan ser ingresados los valores de
            // los gastos e ingresos, después de este paso se puede cambiar el estado
            presupuesto.setEstado("borrador");

            // Asignar la programa al cohorte
            presupuesto.setCohorte(cohorte.get());

            presupuestoRepository.save(presupuesto);
            return "Presupuesto guardado";
        } else {
            return "Error: Cohorte no encontrada";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<Presupuesto> listar() {
        return presupuestoRepository.findAllByOrderByEstadoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<Presupuesto> buscar(@RequestParam int id) {
        return presupuestoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String observaciones,
            @RequestParam int idCohorte) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);
        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        if (presupuesto.isPresent() && cohorte.isPresent()) {
            presupuesto.get().setObservaciones(observaciones);
            presupuesto.get().setCohorte(cohorte.get());
            presupuestoRepository.save(presupuesto.get());
            return "Presupuesto actualizado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        presupuestoRepository.deleteById(id);
        return "Presupuesto eliminado";
    }

    @PutMapping(path = "/cambiarEstado")
    public @ResponseBody String cambiarEstado(@RequestParam int id, @RequestParam String estado) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            presupuesto.get().setEstado(estado);
            presupuestoRepository.save(presupuesto.get());
            return "Estado del presupuesto actualizado";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/actualizarIngresosTotales")
    public @ResponseBody String actualizarIngresosTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            double ingresosTotal = presupuesto.get().getIngresos().stream().mapToDouble(i -> i.getValor()).sum();
            double descuentosTotal = presupuesto.get().getEgresosDescuentos().stream().mapToDouble(i -> i.getValor())
                    .sum();
            presupuesto.get().setIngresosTotales(ingresosTotal - descuentosTotal);

            presupuestoRepository.save(presupuesto.get());
            return "Ingresos totales actualizados";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/actualizarEgresosProgramaTotales")
    public @ResponseBody String actualizarEgresosProgramaTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {
            double egresosTotal = presupuesto.get().getEgresosOtros().stream().mapToDouble(i -> i.getValorTotal())
                    .sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosGenerales().stream().mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosTransferencias().stream().mapToDouble(i -> i.getValorTotal()).sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosViaje().stream().mapToDouble(i -> i.getValorTotal()).sum();

            // Faltan los egresos de Servicios Personales

            presupuesto.get().setEgresosProgramaTotales(egresosTotal);

            presupuestoRepository.save(presupuesto.get());
            return "Egresos totales actualizados";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @PutMapping(path = "/actualizarEgresosRecurrentesUniversidadTotales")
    public @ResponseBody String actualizarEgresosRecurrentesUniversidadTotales(@RequestParam int id) {
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(id);

        if (presupuesto.isPresent()) {

            double egresosTotal = presupuesto.get().getEgresosRecurrentesAdm().stream()
                    .mapToDouble(i -> i.getValorTotal())
                    .sum();

            egresosTotal = egresosTotal
                    + presupuesto.get().getEgresosInversiones().stream().mapToDouble(i -> i.getValor()).sum();

            // Faltan los egresos de Servicios Personales

            presupuesto.get().setEgresosRecurrentesUniversidadTotales(egresosTotal);
            presupuestoRepository.save(presupuesto.get());
            return "Egresos recurrentes totales actualizados";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

}
