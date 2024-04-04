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

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam int numEstudiantes,
            @RequestParam double valor, @RequestParam int numPeriodos, @RequestParam double totalDescuento,
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

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosDescuentos.setEjecucionPresupuestal(null);

            return "Egreso de descuento guardado";
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
            @RequestParam int numPeriodos, @RequestParam double totalDescuento, @RequestParam int idTipoDescuento,
            @RequestParam int idPresupuesto) {

        Optional<EgresosDescuentos> egreso = egresoDescuentoRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoDescuento> tipoDescuento = tipoDescuentoRepository.findById(idTipoDescuento);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoDescuento.isPresent()) {
            EgresosDescuentos egresosDescuentosActualizado = egreso.get();
            egresosDescuentosActualizado.setNumEstudiantes(numEstudiantes);
            egresosDescuentosActualizado.setValor(valor);
            egresosDescuentosActualizado.setNumPeriodos(numPeriodos);
            egresosDescuentosActualizado.setTotalDescuento(valor * numEstudiantes * numPeriodos);
            egresosDescuentosActualizado.setTipoDescuento(tipoDescuento.get());
            egresosDescuentosActualizado.setPresupuesto(presupuesto.get());

            egresoDescuentoRepository.save(egresosDescuentosActualizado);
            return "Egreso de descuento actualizado";
        } else {
            return "Error: Egreso de descuento no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoDescuentoRepository.deleteById(id);
        return "Egreso de descuento eliminado";
    }

}
