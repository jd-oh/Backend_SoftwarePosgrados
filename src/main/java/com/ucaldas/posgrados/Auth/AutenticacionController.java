package com.ucaldas.posgrados.Auth;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    public ResponseEntity<AuthResponse> registro(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.registro(registerRequest));
    }

}
