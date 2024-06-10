package com.ucaldas.posgrados.Controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Cohorte;
import com.ucaldas.posgrados.Entity.Programa;
import com.ucaldas.posgrados.Repository.CohorteRepository;
import com.ucaldas.posgrados.Repository.ProgramaRepository;

@RestController
@CrossOrigin
@RequestMapping(path = "/cohorte")
public class CohorteController {

    @Autowired
    // Esta anotacion nos permite inyectar la dependencia de OdontologoRepository.
    // Todas las operaciones (CRUD) se van a poder hacer por la conexión entre el
    // controlador y el repositorio
    private CohorteRepository cohorteRepository;

    @Autowired
    private ProgramaRepository programaRepository;

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String numero,
            @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha,
            @RequestParam int idPrograma) {

        // Buscar la programa por su ID
        Optional<Programa> programa = programaRepository.findById(idPrograma);

        // Verificar si la programa existe
        if (programa.isPresent()) {
            Cohorte cohorte = new Cohorte();
            cohorte.setNumero(numero);
            cohorte.setFecha(fecha);
            // Asignar la programa al cohorte
            cohorte.setPrograma(programa.get());

            cohorteRepository.save(cohorte);
            return "OK";
        } else {
            return "Error: Programa no encontrado";
        }

    }

    @GetMapping(path = "/listarPorPrograma")
    public @ResponseBody Iterable<Cohorte> listarPorPrograma(@RequestParam int idPrograma) {
        return cohorteRepository.findByProgramaId(idPrograma);
    }

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<Cohorte> listarTodos() {

        return cohorteRepository.findAllByOrderByFechaDesc();
    }

    @GetMapping(path = "/buscar")
    public @ResponseBody Optional<Cohorte> buscarPorId(@RequestParam int id) {
        return cohorteRepository.findById(id);
    }

    @DeleteMapping(path = "/eliminar")
    public @ResponseBody String eliminarPorId(@RequestParam int id) {
        cohorteRepository.deleteById(id);
        return "OK";
    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String numero, @RequestParam LocalDate fecha,
            @RequestParam int idCohorte,
            @RequestParam int idPrograma) {

        // Buscar la programa por su ID
        Optional<Programa> programa = programaRepository.findById(idPrograma);
        Optional<Cohorte> cohorte = cohorteRepository.findById(idCohorte);

        // Verificar si la programa y el cohorte existen
        if (programa.isPresent() && cohorte.isPresent()) {
            Cohorte cohorteActualizado = cohorte.get();
            cohorteActualizado.setNumero(numero);
            cohorteActualizado.setFecha(fecha);

            // Asignar la programa al cohorte
            cohorteActualizado.setPrograma(programa.get());

            cohorteRepository.save(cohorteActualizado);
            return "OK";
        } else {
            return "Error: Programa o Cohorte no encontrados";
        }

    }

}
