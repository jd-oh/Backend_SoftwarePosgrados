package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.ucaldas.posgrados.Entity.Rol;
import com.ucaldas.posgrados.Repository.RolRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/rol")
@RequiredArgsConstructor
@CrossOrigin
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<Rol> listar() {
        return rolRepository.findAll();
    }

    @PostMapping(path = "/crear")
    public @ResponseBody String crear(@RequestParam String nombre) {

        Rol rol = new Rol();
        rol.setNombre(nombre);

        rolRepository.save(rol);
        return "OK";

    }

    @PutMapping(path = "/actualizar")
    public @ResponseBody String actualizar(@RequestParam String nombre, @RequestParam int id) {

        Rol rol = rolRepository.findById(id).get();
        rol.setNombre(nombre);

        rolRepository.save(rol);
        return "OK";

    }

}
