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
import com.ucaldas.posgrados.Entity.TipoTransferencia;
import com.ucaldas.posgrados.Entity.EgresosTransferencias;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoTransferenciaRepository;
import com.ucaldas.posgrados.Repository.EgresosTransferenciasRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoTransferencia")
public class EgresosTransferenciasController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosTransferenciasRepository egresoTransferenciaRepository;

    @Autowired
    private TipoTransferenciaRepository tipoTransferenciaRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoTransferencia.isPresent()) {
            EgresosTransferencias egresosTransferencias = new EgresosTransferencias();

            egresosTransferencias.setDescripcion(descripcion);
            egresosTransferencias.setPorcentaje(porcentaje);

            // La creación de los gastos por transferencia deben hacerse después de haber
            // calculado los ingresos totales

            egresosTransferencias.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);

            egresosTransferencias.setPresupuesto(presupuesto.get());
            egresosTransferencias.setTipoTransferencia(tipoTransferencia.get());

            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosTransferencias.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosTransferencias.setFechaHoraUltimaModificacion("No ha sido modificado");

            // La etiqueta sólo se usa en la ejecución presupuestal
            egresosTransferencias.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosTransferencias().add(egresosTransferencias);

            int idPresupuesto = egresosTransferencias.getPresupuesto().getId();
            double valorNuevo = egresosTransferencias.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, 0);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosTransferencias> listar() {
        return egresoTransferenciaRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosTransferencias> buscar(@RequestParam int id) {
        return egresoTransferenciaRepository.findById(id);
    }

    // Este metodo se hizo para que cuando se edite o se cree un ingreso después de
    // haber creado una transferencia,
    // se actualice el valor total de la transferencia, ya que este depende del
    // ingreso total del presupuesto
    public void actualizarValoresTransferenciasPorIngresos() {
        Iterable<EgresosTransferencias> egresosTransferencias = egresoTransferenciaRepository.findAll();
        for (EgresosTransferencias egreso : egresosTransferencias) {
            egreso.setValorTotal(egreso.getPresupuesto().getIngresosTotales() * egreso.getPorcentaje() / 100);
            egresoTransferenciaRepository.save(egreso);
        }
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {

        Optional<EgresosTransferencias> egreso = egresoTransferenciaRepository.findById(id);
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoTransferencia.isPresent()) {

            double valorAnterior = egreso.get().getValorTotal();

            EgresosTransferencias egresosTransferenciasActualizado = egreso.get();

            egresosTransferenciasActualizado.setDescripcion(descripcion);
            egresosTransferenciasActualizado.setPorcentaje(porcentaje);
            egresosTransferenciasActualizado.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);
            egresosTransferenciasActualizado.setTipoTransferencia(tipoTransferencia.get());

            egresosTransferenciasActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().toString());

            double valorNuevo = egresosTransferenciasActualizado.getValorTotal();

            presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, valorNuevo, valorAnterior);

            egresoTransferenciaRepository.save(egresosTransferenciasActualizado);
            return "OK";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosTransferencias> egreso = egresoTransferenciaRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de transferencia no encontrado";
        }
        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getValorTotal();

        presupuestoController.actualizarEgresosProgramaTotales(idPresupuesto, 0, valorAnterior);
        egresoTransferenciaRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosTransferencias> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoTransferenciaRepository.findByPresupuestoId(idPresupuesto);
    }

    // Este es para el presupuesto
    @GetMapping("/totalEgresosTransferencias")
    public @ResponseBody double totalEgresosTransferencias(int idPresupuesto) {
        Iterable<EgresosTransferencias> egresosTransferencias = egresoTransferenciaRepository
                .findByPresupuestoId(idPresupuesto);
        double total = 0;

        if (!egresosTransferencias.iterator().hasNext()) {
            return 0;
        }

        for (EgresosTransferencias egreso : egresosTransferencias) {
            total += egreso.getValorTotal();
        }
        return total;
    }

    /*
     * // Este es para la ejecución presupuestal
     * 
     * @GetMapping("/totalEgresosTransferenciasEjecucion")
     * public @ResponseBody double totalEgresosTransferenciasEjecucion(int
     * idEjecucionPresupuestal) {
     * Iterable<EgresosTransferencias> egresosTransferencias =
     * egresoTransferenciaRepository
     * .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
     * double total = 0;
     * 
     * if (!egresosTransferencias.iterator().hasNext()) {
     * return 0;
     * }
     * 
     * for (EgresosTransferencias egreso : egresosTransferencias) {
     * total += egreso.getValorTotal();
     * }
     * return total;
     * }
     */

}
