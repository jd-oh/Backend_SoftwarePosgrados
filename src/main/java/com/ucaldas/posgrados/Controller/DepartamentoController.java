package com.ucaldas.posgrados.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Departamento;
import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Repository.DepartamentoRepository;
import com.ucaldas.posgrados.Repository.FacultadRepository;

@RestController
@RequestMapping(path = "/departamento")
public class DepartamentoController {

    @Autowired
    // Esta anotacion nos permite inyectar la dependencia de OdontologoRepository.
    // Todas las operaciones (CRUD) se van a poder hacer por la conexión entre el
    // controlador y el repositorio
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private FacultadRepository facultadRepository;

    @PostMapping(path = "/nuevo")
    public @ResponseBody String nuevo(@RequestParam String nombre, @RequestParam int idFacultad) {

        // Buscar la facultad por su ID
        Optional<Facultad> facultad = facultadRepository.findById(idFacultad);

        // Verificar si la facultad existe
        if (facultad.isPresent()) {
            Departamento departamento = new Departamento();
            departamento.setNombre(nombre);
            // Asignar la facultad al departamento
            departamento.setFacultad(facultad.get());

            departamentoRepository.save(departamento);
            return "Departamento guardado";
        } else {
            return "Error: Facultad no encontrada";
        }

    }

    @GetMapping(path = "/todos")
    public @ResponseBody Iterable<Departamento> listarTodos() {
        return departamentoRepository.findAll();
    }

    @GetMapping(path = "/buscar")
    public @ResponseBody String buscarPorId(@RequestParam int id) {
        return departamentoRepository.findById(id).get().toString();
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminarPorId(@RequestParam int id) {
        departamentoRepository.deleteById(id);
        return "Departamento eliminado";
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String nombre, @RequestParam int idDepartamento,
            @RequestParam int idFacultad) {

        // Buscar la facultad por su ID
        Optional<Facultad> facultad = facultadRepository.findById(idFacultad);
        Optional<Departamento> departamento = departamentoRepository.findById(idDepartamento);

        // Verificar si la facultad y el departamento existen
        if (facultad.isPresent() && departamento.isPresent()) {
            Departamento departamentoActualizado = departamento.get();
            departamentoActualizado.setNombre(nombre);
            // Asignar la facultad al departamento
            departamentoActualizado.setFacultad(facultad.get());

            departamentoRepository.save(departamentoActualizado);
            return "Departamento actualizado";
        } else {
            return "Error: Facultad o Departamento no encontrados";
        }

    }

}
