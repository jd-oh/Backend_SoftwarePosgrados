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
import com.ucaldas.posgrados.Entity.TipoDescuento;
import com.ucaldas.posgrados.Entity.EgresosDescuentos;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoDescuentoRepository;
import com.ucaldas.posgrados.Repository.EgresosDescuentosRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoDescuento")
public class EgresosDescuentosController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosDescuentosRepository egresoDescuentoRepository;

    @Autowired
    private TipoDescuentoRepository tipoDescuentoRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @Autowired
    private EgresosTransferenciasController egresosTransferenciasController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoDescuento> tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoDescuento.isPresent()) {
            EgresosDescuentos egresosDescuentos = new EgresosDescuentos();
            egresosDescuentos.setNumEstudiantes(numEstudiantes);
            egresosDescuentos.setValor(valor);
            egresosDescuentos.setNumPeriodos(numPeriodos);
            egresosDescuentos.setTotalDescuento(valor * numEstudiantes * numPeriodos);
            egresosDescuentos.setPresupuesto(presupuesto.get());
            egresosDescuentos.setTipoDescuento(tipoDescuento.get());
            // La fecha y hora se asigna en el momento de la creación con la del sistema
            egresosDescuentos.setFechaHoraCreacion(java.time.LocalDateTime.now().getDayOfMonth() + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            egresosDescuentos.setFechaHoraUltimaModificacion("No ha sido modificado");

            // La etiqueta sólo se usa en la ejecución presupuestal
            egresosDescuentos.setEtiquetaEgresoIngreso(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosDescuentos().add(egresosDescuentos);

            double valorNuevo = egresosDescuentos.getTotalDescuento();

            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, 0, "descuento");

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
    public @ResponseBody Iterable<EgresosDescuentos> listar() {
        return egresoDescuentoRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosDescuentos> buscar(@RequestParam int id) {
        return egresoDescuentoRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int numEstudiantes,
            @RequestParam double valor,
            @RequestParam int numPeriodos, @RequestParam int idTipoDescuento) {

        Optional<EgresosDescuentos> egreso = egresoDescuentoRepository.findById(id);
        Optional<TipoDescuento> tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento);

        if (egreso.isPresent() && tipoDescuento.isPresent()) {
            double valorAnterior = egreso.get().getTotalDescuento();
            EgresosDescuentos egresosDescuentosActualizado = egreso.get();
            egresosDescuentosActualizado.setNumEstudiantes(numEstudiantes);
            egresosDescuentosActualizado.setValor(valor);
            egresosDescuentosActualizado.setNumPeriodos(numPeriodos);
            egresosDescuentosActualizado.setTotalDescuento(valor * numEstudiantes * numPeriodos);
            egresosDescuentosActualizado.setTipoDescuento(tipoDescuento.get());
            egresosDescuentosActualizado.setFechaHoraUltimaModificacion(java.time.LocalDateTime.now().getDayOfMonth()
                    + "/"
                    + java.time.LocalDateTime.now().getMonthValue() + "/" + java.time.LocalDateTime.now().getYear()
                    + " "
                    + java.time.LocalDateTime.now().getHour() + ":" + java.time.LocalDateTime.now().getMinute() + ":"
                    + java.time.LocalDateTime.now().getSecond());
            int idPresupuesto = egresosDescuentosActualizado.getPresupuesto().getId();
            double valorNuevo = egresosDescuentosActualizado.getTotalDescuento();
            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, valorAnterior, "descuento");

            // Si existen egresos de transferencias entonces llamamos al metodo
            // 'actualizarIngresosTotales'
            if (egresosTransferenciasController.listar().iterator().hasNext()) {
                egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
            }
            egresoDescuentoRepository.save(egresosDescuentosActualizado);

            return "OK";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosDescuentos> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoDescuentoRepository.findByPresupuestoId(idPresupuesto);
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        Optional<EgresosDescuentos> egreso = egresoDescuentoRepository.findById(id);

        if (!egreso.isPresent()) {
            return "Error: Egreso de descuento no encontrado";
        }

        int idPresupuesto = egreso.get().getPresupuesto().getId();
        double valorAnterior = egreso.get().getTotalDescuento();

        presupuestoController.actualizarIngresosTotales(idPresupuesto, 0, valorAnterior, "descuento");

        // Si existen egresos de transferencias entonces llamamos al metodo
        // 'actualizarIngresosTotales'
        if (egresosTransferenciasController.listar().iterator().hasNext()) {
            egresosTransferenciasController.actualizarValoresTransferenciasPorIngresos();
        }

        egresoDescuentoRepository.deleteById(id);

        return "OK";
    }

    // Este es para presupuesto
    @GetMapping("/totalEgresosDescuentos")
    public @ResponseBody double totalEgresosDescuentos(int idPresupuesto) {
        double total = 0;
        Iterable<EgresosDescuentos> egresosDescuentos = egresoDescuentoRepository.findByPresupuestoId(idPresupuesto);

        // Si no hay egresos
        if (!egresosDescuentos.iterator().hasNext()) {
            return total;
        }
        for (EgresosDescuentos egresoDescuento : egresosDescuentos) {
            total += egresoDescuento.getTotalDescuento();
        }
        return total;
    }

    /*
     * // Este es para ejecucion presupuestal
     * 
     * @GetMapping("/totalEgresosDescuentosEjecucion")
     * public @ResponseBody double totalEgresosDescuentosEjecucion(int
     * idEjecucionPresupuestal) {
     * double total = 0;
     * Iterable<EgresosDescuentos> egresosDescuentos = egresoDescuentoRepository
     * .findByEjecucionPresupuestalId(idEjecucionPresupuestal);
     * 
     * // Si no hay egresos
     * if (!egresosDescuentos.iterator().hasNext()) {
     * return total;
     * }
     * for (EgresosDescuentos egresoDescuento : egresosDescuentos) {
     * total += egresoDescuento.getTotalDescuento();
     * }
     * return total;
     * }
     */
}
