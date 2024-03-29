package com.ucaldas.posgrados.Controller;

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

import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Repository.FacultadRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/facultad")
public class FacultadController {

    @Autowired
    // Esta anotacion nos permite inyectar la dependencia de OdontologoRepository.
    // Todas las operaciones (CRUD) se van a poder hacer por la conexión entre el
    // controlador y el repositorio
    private FacultadRepository facultadRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombre) {

        Facultad facultad = new Facultad();
        facultad.setNombre(nombre);

        facultadRepository.save(facultad);
        return "Facultad guardada";

    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<Facultad> listarTodos() {
        return facultadRepository.findAll();
    }

    @GetMapping(path = "/buscar")
    public @ResponseBody String buscarPorId(@RequestParam int id) {
        return facultadRepository.findById(id).get().toString();
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminarPorId(@RequestParam int id) {

        facultadRepository.deleteById(id);
        return "Facultad eliminado";
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String nombre, @RequestParam int id) {

        Facultad facultad = facultadRepository.findById(id).get();

        facultad.setNombre(nombre);

        facultadRepository.save(facultad);
        return "Facultad actualizado";
    }

}
