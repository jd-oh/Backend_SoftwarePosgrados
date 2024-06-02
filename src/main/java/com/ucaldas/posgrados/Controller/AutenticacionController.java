package com.ucaldas.posgrados.Controller;

import org.springframework.web.bind.annotation.RestController;

import com.ucaldas.posgrados.DTO.AuthResponse;
import com.ucaldas.posgrados.DTO.LoginRequest;
import com.ucaldas.posgrados.DTO.RegisterRequest;
import com.ucaldas.posgrados.Services.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.passay.*;
import org.passay.CharacterData;

@RestController
@RequestMapping("/autenticacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AutenticacionController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String username, @RequestParam String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        AuthResponse authResponse = authService.login(loginRequest);

        // Verifica si la autenticación fue exitosa
        if (authResponse != null && authResponse.getToken() != null) {

            return ResponseEntity.ok(authResponse);
        } else {
            // Maneja el caso de autenticación fallida
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping(value = "/registro")
    public ResponseEntity<AuthResponse> registro(@RequestParam String nombre, @RequestParam String apellido,
            @RequestParam String email, @RequestParam int idRol,
            @RequestParam int idFacultad) {

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

        // Crear una contraseña aleatoria
        String password = generarPassword();

        String username = nombreUsuario + "." + apellidoUsuario + numero; // nombre.apellidoXX
        RegisterRequest registerRequest = new RegisterRequest(nombre, apellido, email, username, password, idRol,
                idFacultad);
        return ResponseEntity.ok(authService.registro(registerRequest));
    }

    @PostMapping(value = "/refrescarToken")
    public ResponseEntity<AuthResponse> refrescarToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.refrescarToken(token));
    }

    @PostMapping(value = "/cambiarPassword")
    public ResponseEntity<String> cambiarPassword(@RequestParam String token, @RequestParam String password) {
        // Tiene que haber un token para cambiar la contraseña
        return ResponseEntity.ok(authService.cambiarPasswordConToken(token, password));
    }

    @PostMapping(value = "olvideMiPassword")
    public ResponseEntity<String> olvideMiPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.olvideMiPassword(email));
    }

    private String generarPassword() {
        PasswordGenerator generator = new PasswordGenerator();

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(1);

        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(1);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(1);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return "ERROR_CODE";
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(1);

        String password = generator.generatePassword(8, splCharRule, lowerCaseRule, upperCaseRule, digitRule);

        System.out.println(password);
        return password;
    }

}
