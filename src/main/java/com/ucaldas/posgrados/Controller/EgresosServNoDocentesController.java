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

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String servicio,
            @RequestParam double valorUnitario,
            @RequestParam int cantidad, @RequestParam int idTipoCosto) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoCosto.isPresent()) {
            EgresosServNoDocentes egresosServNoDocentes = new EgresosServNoDocentes();

            egresosServNoDocentes.setServicio(servicio);
            egresosServNoDocentes.setValorUnitario(valorUnitario);
            egresosServNoDocentes.setCantidad(cantidad);
            egresosServNoDocentes.setValorTotal(cantidad * valorUnitario);

            egresosServNoDocentes.setPresupuesto(presupuesto.get());
            egresosServNoDocentes.setTipoCosto(tipoCosto.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosServNoDocentes.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosServNoDocentes.setFechaHoraUltimaModificacion("No ha sido modificado");

            // La etiqueta sólo se usa en la ejecución presupuestal
            egresosServNoDocentes.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosServNoDocentes().add(egresosServNoDocentes);

            int idPresupuesto = egresosServNoDocentes.getPresupuesto().getId();
            double valorNuevo = egresosServNoDocentes.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
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
            @RequestParam String servicio, @RequestParam double valorUnitario,
            @RequestParam int cantidad) {

        Optional<EgresosServNoDocentes> egreso = egresoServNoDocenteRepository.findById(id);
        Optional<TipoCosto> tipoCosto = tipoCostoRepository.findById(idTipoCosto);

        if (egreso.isPresent() && tipoCosto.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosServNoDocentes egresosServNoDocentesActualizado = egreso.get();

            egresosServNoDocentesActualizado.setServicio(servicio);
            egresosServNoDocentesActualizado.setValorUnitario(valorUnitario);
            egresosServNoDocentesActualizado.setCantidad(cantidad);
            egresosServNoDocentesActualizado.setValorTotal(cantidad * valorUnitario);

            egresosServNoDocentesActualizado.setTipoCosto(tipoCosto.get());

            egresosServNoDocentesActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            int idPresupuesto = egresosServNoDocentesActualizado.getPresupuesto().getId();
            double valorNuevo = egresosServNoDocentesActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoServNoDocenteRepository.save(egresosServNoDocentesActualizado);
            return "OK";
        } else {
            return "Error: Egreso de servicio no docente no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosServNoDocentes> egreso = egresoServNoDocenteRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de servicio no docente no encontrado";
        }
        double valorAnterior = egreso.get().getValorTotal();
        int idPresupuesto = egreso.get().getPresupuesto().getId();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoServNoDocenteRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosServNoDocentes> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoServNoDocenteRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para el presupuesto
    @GetMapping("/totalEgresosServNoDocentes")
    public @ResponseBody double totalEgresosServNoDocentes(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosServNoDocentes> egresosServNoDocentes = egresoServNoDocenteRepository
                .findByPresupuestoId(idPresupuesto);

        // Si no hay egresos
        if (!egresosServNoDocentes.iterator().hasNext()) {
            return total;
        }
        for (EgresosServNoDocentes egresoRecurrenteAdm : egresosServNoDocentes) {
            total += egresoRecurrenteAdm.getValorTotal();
        }
        return total;
    }

    /*
     * // Este es para la ejecución presupuestal
     * 
     * @GetMapping("/totalEgresosServNoDocentesEjecucion")
     * public @ResponseBody double totalEgresosServNoDocentesEjecucion(int
     * idEjecucionPresupuestal) {
     * double total = 0;
     * Iterable<EgresosServNoDocentes> egresosServNoDocentes =
     * egresoServNoDocenteRepository
     * .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
     * 
     * // Si no hay egresos
     * if (!egresosServNoDocentes.iterator().hasNext()) {
     * return total;
     * }
     * for (EgresosServNoDocentes egresoRecurrenteAdm : egresosServNoDocentes) {
     * total += egresoRecurrenteAdm.getValorTotal();
     * }
     * return total;
     * }
     */

}
