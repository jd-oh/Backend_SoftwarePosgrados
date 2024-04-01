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
import com.ucaldas.posgrados.Entity.Departamento;
import com.ucaldas.posgrados.Repository.ProgramaRepository;
import com.ucaldas.posgrados.Repository.DepartamentoRepository;

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
    private DepartamentoRepository departamentoRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombre, @RequestParam int idDepartamento) {

        // Buscar la departamento por su ID
        Optional<Departamento> departamento = departamentoRepository.findById(idDepartamento);

        // Verificar si la departamento existe
        if (departamento.isPresent()) {
            Programa programa = new Programa();
            programa.setNombre(nombre);
            // Asignar la departamento al programa
            programa.setDepartamento(departamento.get());

            programaRepository.save(programa);
            return "Programa guardado";
        } else {
            return "Error: Departamento no encontrado";
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
        return "Programa eliminado";
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String nombre, @RequestParam int idPrograma,
            @RequestParam int idDepartamento) {

        // Buscar la departamento por su ID
        Optional<Departamento> departamento = departamentoRepository.findById(idDepartamento);
        Optional<Programa> programa = programaRepository.findById(idPrograma);

        // Verificar si la departamento y el programa existen
        if (departamento.isPresent() && programa.isPresent()) {
            Programa programaActualizado = programa.get();
            programaActualizado.setNombre(nombre);
            // Asignar la departamento al programa
            programaActualizado.setDepartamento(departamento.get());

            programaRepository.save(programaActualizado);
            return "Programa actualizado";
        } else {
            return "Error: Departamento o Programa no encontrados";
        }

    }

}
