package com.ucaldas.posgrados.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.Entity.Usuario;
import com.ucaldas.posgrados.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/usuario")
@RequiredArgsConstructor
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping(path = "/listar")
    public @ResponseBody Iterable<Usuario> listar() {
        System.out.println("Listando usuarios");
        return usuarioRepository.findAll();
    }

    @PostMapping("desactivar")
    public ResponseEntity<String> desactivar(@RequestParam String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        usuario.setEnabled(false);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Usuario desactivado");
    }

    @PostMapping("activar")
    public ResponseEntity<String> activar(@RequestParam String username) {
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        usuario.setEnabled(true);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario activado");
    }

    @PutMapping("editarDatosBasicos")
    public ResponseEntity<String> editarDatosBasicos(@RequestParam int id, @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("OK");
    }

}
