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

    @PostMapping("/crear")
    public @ResponseBody String crear(@RequestParam int idPresupuesto, @RequestParam String nombreMateria,
            @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat,
            @RequestParam int horasPracticasMat, @RequestParam double valorHoraProfesor,
            @RequestParam int idTipoCompensacion) {
        // Buscar la programa por su ID
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);

        // Verificar si la programa existe
        if (presupuesto.isPresent() && tipoCompensacion.isPresent()) {
            EgresosServDocentes egresosDescuentos = new EgresosServDocentes();

            egresosDescuentos.setNombreMateria(nombreMateria);
            egresosDescuentos.setEsDocentePlanta(esDocentePlanta);
            egresosDescuentos.setNombreDocente(nombreDocente);
            egresosDescuentos.setEscalafon(escalafon);
            egresosDescuentos.setTitulo(titulo);
            egresosDescuentos.setHorasTeoricasMat(horasTeoricasMat);
            egresosDescuentos.setHorasPracticasMat(horasPracticasMat);

            egresosDescuentos.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosDescuentos.setValorHoraProfesor(valorHoraProfesor);

            egresosDescuentos.setTotalPagoProfesor(valorHoraProfesor * egresosDescuentos.getTotalHorasProfesor());

            egresosDescuentos.setPresupuesto(presupuesto.get());
            egresosDescuentos.setTipoCompensacion(tipoCompensacion.get());

            // Aún no hay ejecución presupuestal porque no se sabe si el presupuesto será
            // aprobado o no
            egresosDescuentos.setEjecucionPresupuestal(null);

            return "Egreso de servicios docentes guardado";
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
            @RequestParam int idPresupuesto, @RequestParam String nombreMateria, @RequestParam boolean esDocentePlanta,
            @RequestParam String nombreDocente, @RequestParam String escalafon, @RequestParam String titulo,
            @RequestParam int horasTeoricasMat, @RequestParam int horasPracticasMat,
            @RequestParam double valorHoraProfesor) {

        Optional<EgresosServDocentes> egreso = egresoServDocenteRepository.findById(id);
        Optional<Presupuesto> presupuesto = presupuestoRepository.findById(idPresupuesto);
        Optional<TipoCompensacion> tipoCompensacion = tipoCompensacionRepository.findById(idTipoCompensacion);

        if (egreso.isPresent() && presupuesto.isPresent() && tipoCompensacion.isPresent()) {
            EgresosServDocentes egresosDescuentosActualizado = egreso.get();

            egresosDescuentosActualizado.setNombreMateria(nombreMateria);
            egresosDescuentosActualizado.setEsDocentePlanta(esDocentePlanta);
            egresosDescuentosActualizado.setNombreDocente(nombreDocente);
            egresosDescuentosActualizado.setEscalafon(escalafon);
            egresosDescuentosActualizado.setTitulo(titulo);
            egresosDescuentosActualizado.setHorasTeoricasMat(horasTeoricasMat);
            egresosDescuentosActualizado.setHorasPracticasMat(horasPracticasMat);

            egresosDescuentosActualizado.setTotalHorasProfesor(horasPracticasMat + horasTeoricasMat);

            egresosDescuentosActualizado.setValorHoraProfesor(valorHoraProfesor);

            egresosDescuentosActualizado
                    .setTotalPagoProfesor(valorHoraProfesor * egresosDescuentosActualizado.getTotalHorasProfesor());

            egresosDescuentosActualizado.setTipoCompensacion(tipoCompensacion.get());
            egresosDescuentosActualizado.setPresupuesto(presupuesto.get());

            egresoServDocenteRepository.save(egresosDescuentosActualizado);
            return "Egreso de servicios docentes actualizado";
        } else {
            return "Error: Egreso de servicios docentes no encontrado";
        }
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminar(@RequestParam int id) {
        egresoServDocenteRepository.deleteById(id);
        return "Egreso de servicios docentes eliminado";
    }

}
