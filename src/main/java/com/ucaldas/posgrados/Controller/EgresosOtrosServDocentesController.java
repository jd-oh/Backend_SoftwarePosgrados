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

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String servicio,
            @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal,
            @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
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
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosOtrosServDocentes.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosOtrosServDocentes.setFechaHoraUltimaModificacion("No ha sido modificado");

            // La etiqueta sólo se usa en la ejecución presupuestal
            egresosOtrosServDocentes.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosOtrosServDocentes().add(egresosOtrosServDocentes);

            int idPresupuesto = egresosOtrosServDocentes.getPresupuesto().getId();
            double valorNuevo = egresosOtrosServDocentes.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
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
            @RequestParam String servicio, @RequestParam String descripcion,
            @RequestParam int numHoras, @RequestParam double valorTotal) {

        Optional<EgresosOtrosServDocentes> egreso = egresoOtrosServDocenteRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosOtrosServDocentes egresosOtrosServDocentesActualizado = egreso.get();
            egresosOtrosServDocentesActualizado.setServicio(servicio);
            egresosOtrosServDocentesActualizado.setDescripcion(descripcion);
            egresosOtrosServDocentesActualizado.setNumHoras(numHoras);
            egresosOtrosServDocentesActualizado.setValorTotal(valorTotal);
            egresosOtrosServDocentesActualizado
                    .setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                            + java.time.LocalDateTime.now().getMonthValue() + "/"
                            + java.time.LocalDateTime.now().getYear()
                            + " "
                            + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute()
                            + ":"
                            + java.time.LocalDateTime.now().getSecond());

            egresosOtrosServDocentesActualizado.setTipoCosto(tipoCosto.get());

            int idPresupuesto = egresosOtrosServDocentesActualizado.getPresupuesto().getId();
            double valorNuevo = egresosOtrosServDocentesActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoOtrosServDocenteRepository.save(egresosOtrosServDocentesActualizado);
            return "OK";
        } else {
            return "Error: Egreso de otros servicios docentes no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosOtrosServDocentes> egreso = egresoOtrosServDocenteRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de otros servicios docentes no encontrado";
        }
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoOtrosServDocenteRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosOtrosServDocentes> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoOtrosServDocenteRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para el presupuesto
    @GetMapping("/totalEgresosOtrosServDocentes")
    public @ResponseBody double totalEgresosOtrosServDocentes(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosOtrosServDocentes> egresosOtrosServDocentes = egresoOtrosServDocenteRepository
                .findByPresupuestoId(idPresupuesto);

        // Si no hay egresos
        if (!egresosOtrosServDocentes.iterator().hasNext()) {
            return total;
        }
        for (EgresosOtrosServDocentes egresoOtro : egresosOtrosServDocentes) {
            total += egresoOtro.getValorTotal();
        }
        return total;
    }

    /*
     * // Este es para la ejecución presupuestal
     * 
     * @GetMapping("/totalEgresosOtrosServDocentesEjecucion")
     * public @ResponseBody double totalEgresosOtrosServDocentesEjecucion(int
     * idEjecucionPresupuestal) {
     * double total = 0;
     * Iterable<EgresosOtrosServDocentes> egresosOtrosServDocentes =
     * egresoOtrosServDocenteRepository
     * .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
     * 
     * // Si no hay egresos
     * if (!egresosOtrosServDocentes.iterator().hasNext()) {
     * return total;
     * }
     * for (EgresosOtrosServDocentes egresoOtro : egresosOtrosServDocentes) {
     * total += egresoOtro.getValorTotal();
     * }
     * return total;
     * }
     */
}
