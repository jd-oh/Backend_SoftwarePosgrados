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
import com.ucaldas.posgrados.Entity.TipoCompensacion;
import com.ucaldas.posgrados.Entity.EgresosServDocentes;
import com.ucaldas.posgrados.Repository.PresupuestoRepository;
import com.ucaldas.posgrados.Repository.TipoCompensacionRepository;
import com.ucaldas.posgrados.Repository.EgresosServDocentesRepository;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@CrossOrigin
@RequestMapping(path = "/egresoServDocente")
public class EgresosServDocentesController {

    @Autowired
    private PresupuestoRepository presupuestoRepository;

    @Autowired
    private EgresosServDocentesRepository egresoServDocenteRepository;

    @Autowired
    private TipoCompensacionRepository tipoCompensacionRepository;

    @Autowired
    private PresupuestoController presupuestoController;

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuestoEjecucion, @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuestoEjecucion);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoCompensacion.isPresent()) {
            EgresosServDocentes egresosServDocentes = new EgresosServDocentes();

            egresosServDocentes.setNombreMateria(nombreMateria);
            egresosServDocentes.setEsDocentePlanta(esDocentePlanta);
            egresosServDocentes.setNombreDocente(nombreDocente);
            egresosServDocentes.setEscalafon(escalafon);
            egresosServDocentes.setTitulo(titulo);
            egresosServDocentes.setHorasTeoricasMat(horasTeoricasMat);
            egresosServDocentes.setHorasPracticasMat(horasPracticasMat);

            egresosServDocentes.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosServDocentes.setValorHoraProfesor(valorHoraProfesor);

            egresosServDocentes.setTotalPagoProfesor(valorHoraProfesor * egresosServDocentes.getTotalHorasProfesor());

            egresosServDocentes.setPresupuesto(presupuesto.get());
            egresosServDocentes.setTipoCompensacion(tipoCompensacion.get());

            // Si el gasto hace parte de un presupuesto, la ejecución debe ser null siempre
            // y viceversa
            egresosServDocentes.setEjecucionPresupuestal(null);

            // Guardar el egreso general en el presupuesto
            presupuesto.get().getEgresosServDocentes().add(egresosServDocentes);

            // Guardar el Presupuesto actualizado
            presupuestoRepository.save(presupuesto.get());

            return "OK";
        } else {
            return "Error: Presupuesto no encontrado";
        }
    }

    @GetMapping("/listar")
    public @ResponseBody Iterable<EgresosServDocentes> listar() {
        return egresoServDocenteRepository.findAllByOrderByPresupuestoAsc();
    }

    @GetMapping("/buscar")
    public @ResponseBody Optional<EgresosServDocentes> buscar(@RequestParam int id) {
        return egresoServDocenteRepository.findById(id);
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam int id, @RequestParam int idTipoCompensacion,
            @RequestParam String nombreMateria, @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat, @RequestParam int horasPracticasMat,
            @RequestParam double valorHoraProfesor) {

        Optional<EgresosServDocentes> egreso = egresoServDocenteRepository.findById(id);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);

        if (egreso.isPresent() && tipoCompensacion.isPresent()) {
            EgresosServDocentes egresosServDocentesActualizado = egreso.get();

            egresosServDocentesActualizado.setNombreMateria(nombreMateria);
            egresosServDocentesActualizado.setEsDocentePlanta(esDocentePlanta);
            egresosServDocentesActualizado.setNombreDocente(nombreDocente);
            egresosServDocentesActualizado.setEscalafon(escalafon);
            egresosServDocentesActualizado.setTitulo(titulo);
            egresosServDocentesActualizado.setHorasTeoricasMat(horasTeoricasMat);
            egresosServDocentesActualizado.setHorasPracticasMat(horasPracticasMat);

            egresosServDocentesActualizado.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosServDocentesActualizado.setValorHoraProfesor(valorHoraProfesor);

            egresosServDocentesActualizado
                    .setTotalPagoProfesor(valorHoraProfesor * egresosServDocentesActualizado.getTotalHorasProfesor());

            egresosServDocentesActualizado.setTipoCompensacion(tipoCompensacion.get());
            int idPresupuesto = egresosServDocentesActualizado.getPresupuesto().getId();
            egresoServDocenteRepository.save(egresosServDocentesActualizado);

            return "OK";
        } else {
            return "Error: Egreso de servicios docentes no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {

        egresoServDocenteRepository.deleteById(id);
        return "OK";
    }

    // Listar por presupuesto
    @GetMapping("/listarPorPresupuesto")
    public @ResponseBody Iterable<EgresosServDocentes> listarPorPresupuesto(@RequestParam int idPresupuesto) {
        return egresoServDocenteRepository.findByPresupuestoId(idPresupuesto);
    }

    @GetMapping("/totalEgresosServDocentes")
    public @ResponseBody double totalEgresosServDocentes() {
        double total = 0;
        Iterable<EgresosServDocentes> egresosServDocentes = egresoServDocenteRepository.findAll();

        // Si no hay egresos de otros
        if (!egresosServDocentes.iterator().hasNext()) {
            return total;
        }
        for (EgresosServDocentes egresoRecurrenteAdm : egresosServDocentes) {
            total += egresoRecurrenteAdm.getTotalPagoProfesor();
        }
        return total;
    }

}
