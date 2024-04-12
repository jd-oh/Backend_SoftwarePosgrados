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

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos,
            @RequestParam int idTipoDescuento) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
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

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosDescuentos.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosDescuentos().add(egresosDescuentos);

            int idPresupuesto = presupuesto.get().getId();
            double valorNuevo = egresosDescuentos.getTotalDescuento();

            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, 0, "descuento");

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

            int idPresupuesto = egresosDescuentosActualizado.getPresupuesto().getId();
            double valorNuevo = egresosDescuentosActualizado.getTotalDescuento();
            presupuestoController.actualizarIngresosTotales(idPresupuesto, valorNuevo, valorAnterior, "egreso");
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

        presupuestoController.actualizarIngresosTotales(idPresupuesto, 0, valorAnterior, "egreso");

        egresoDescuentoRepository.deleteById(id);
        return "OK";
    }

}
