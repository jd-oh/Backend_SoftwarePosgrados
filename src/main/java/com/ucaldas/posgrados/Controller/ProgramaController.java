package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Programa;
import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Repository.ProgramaRepository;
import com.ucaldas.posgrados.Repository.FacultadRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/programa")
public class ProgramaController {

    @Autowired
    // Esta anotacion nos permite inyectar la dependencia de OdontologoRepository.
    // Todas las operaciones (CRUD) se van a poder hacer por la conexión entre el
    // controlador y el repositorio
    private ProgramaRepository programaRepository;

    @Autowired
    private FacultadRepository facultadRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombre, @RequestParam int idFacultad,
            @RequestParam boolean priorizado) {

        // Buscar la facultad por su ID
        Optional<Facultad> facultad = facultadRepository.findById(idFacultad);

        // Verificar si la facultad existe
        if (facultad.isPresent()) {
            Programa programa = new Programa();
            programa.setNombre(nombre);
            // Asignar la facultad al programa
            programa.setFacultad(facultad.get());

            // Asegurarse de que no haya otro programa que ya sea priorizado en la misma
            // facultad
            if (priorizado) {
                Iterable<Programa> programas = programaRepository.findAllByFacultad(facultad.get());
                for (Programa p : programas) {
                    if (p.isPriorizado()) {
                        return "Error: Ya existe un programa priorizado en la facultad";
                    }
                }

                programa.setPriorizado(true);
            }

            programaRepository.save(programa);
            return "OK";
        } else {
            return "Error: Facultad no encontrado";
        }

    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<Programa> listarTodos() {
        return programaRepository.findAllByOrderByNombreAsc();
    }

    @GetMapping(path = "/buscar")
    public @ResponseBody String buscarPorId(@RequestParam int id) {
        return programaRepository.findById(id).get().toString();
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminarPorId(@RequestParam int id) {
        programaRepository.deleteById(id);
        return "OK";
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String nombre, @RequestParam int idPrograma,
            @RequestParam int idFacultad, @RequestParam boolean priorizado) {

        // Buscar la facultad por su ID
        Optional<Facultad> facultad = facultadRepository.findById(idFacultad);
        Optional<Programa> programa = programaRepository.findById(idPrograma);

        // Verificar si la facultad y el programa existen
        if (facultad.isPresent() && programa.isPresent()) {
            Programa programaActualizado = programa.get();
            programaActualizado.setNombre(nombre);
            // Asignar la facultad al programa
            programaActualizado.setFacultad(facultad.get());

            // Asegurarse de que no haya otro programa que ya sea priorizado en la misma
            // facultad
            if (priorizado) {
                Iterable<Programa> programas = programaRepository.findAllByFacultad(facultad.get());
                for (Programa p : programas) {
                    if (p.isPriorizado() && p.getId() != idPrograma) {
                        return "Error: Ya existe un programa priorizado en la facultad";
                    }
                }

                programaActualizado.setPriorizado(true);
            } else {
                programaActualizado.setPriorizado(false);
            }

            programaRepository.save(programaActualizado);
            return "OK";
        } else {
            return "Error: Facultad o Programa no encontrados";
        }

    }

}
