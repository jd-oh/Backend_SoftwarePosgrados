package com.ucaldas.posgrados.Auth;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String username, @RequestParam String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping(value = "/registro")
    public ResponseEntity<AuthResponse> registro(@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam String password) {

        // numero al azar de dos cifras
        int numero = (int) (Math.random() * 90 + 10);

        // obtener nombre hasta que haya un espacio si es que lo hay
        String[] nombreArray = nombre.split(" ");
        String nombreUsuario = nombreArray[0];

        // convertir a minisculas
        nombreUsuario = nombreUsuario.toLowerCase();

        String[] apellidoArray = apellido.split(" ");
        String apellidoUsuario = apellidoArray[0];
        apellidoUsuario = apellidoUsuario.toLowerCase();

        String username = nombreUsuario + "." + apellidoUsuario + numero; // nombre.apellidoXX
        RegisterRequest registerRequest = new RegisterRequest(nombre, apellido, email, username, password);

        return ResponseEntity.ok(authService.registro(registerRequest));
    }

}
