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

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoTransferencia.isPresent()) {
            EgresosTransferencias egresosDescuentos = new EgresosTransferencias();

            egresosDescuentos.setDescripcion(descripcion);
            egresosDescuentos.setPorcentaje(porcentaje);

            // La creación de los gastos por transferencia deben hacerse después de haber
            // calculado los ingresos totales

            egresosDescuentos.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);

            egresosDescuentos.setPresupuesto(presupuesto.get());
            egresosDescuentos.setTipoTransferencia(tipoTransferencia.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosDescuentos.setEjecucionPresupuestal(null);

            return "Egreso de descuento guardado";
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

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam String descripcion,
            @RequestParam double porcentaje,
            @RequestParam int idTipoTransferencia, @RequestParam int idPresupuesto) {

        Optional<EgresosTransferencias> egreso = egresoTransferenciaRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoTransferencia> tipoTransferencia = tipoTransferenciaRepository.findById(idTipoTransferencia);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoTransferencia.isPresent()) {
            EgresosTransferencias egresosDescuentosActualizado = egreso.get();

            egresosDescuentosActualizado.setDescripcion(descripcion);
            egresosDescuentosActualizado.setPorcentaje(porcentaje);
            egresosDescuentosActualizado.setValorTotal(presupuesto.get().getIngresosTotales() * porcentaje / 100);

            egresosDescuentosActualizado.setPresupuesto(presupuesto.get());

            egresoTransferenciaRepository.save(egresosDescuentosActualizado);
            return "Egreso de descuento actualizado";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoTransferenciaRepository.deleteById(id);
        return "Egreso de descuento eliminado";
    }

}
