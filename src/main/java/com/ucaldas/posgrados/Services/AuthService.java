package com.ucaldas.posgrados.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ucaldas.posgrados.DTO.AuthResponse;
import com.ucaldas.posgrados.DTO.LoginRequest;
import com.ucaldas.posgrados.DTO.RegisterRequest;
import com.ucaldas.posgrados.Entity.Facultad;
import com.ucaldas.posgrados.Entity.Rol;
import com.ucaldas.posgrados.Entity.Usuario;
import com.ucaldas.posgrados.Jwt.JwtService;
import com.ucaldas.posgrados.Repository.FacultadRepository;
import com.ucaldas.posgrados.Repository.RolRepository;
import com.ucaldas.posgrados.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UsuarioRepository userRepository;
        private final RolRepository rolRepository;
        private final FacultadRepository facultadRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticacionManager;
        @Autowired
        private final JavaMailSender mailSender;
        private final String urlCambiarPassword = "http://localhost:4200/login/cambiar-contrasena";

        public AuthResponse login(LoginRequest loginRequest) {
                authenticacionManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));
                UserDetails usuario = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

                String token = jwtService.getToken(usuario);
                return AuthResponse.builder()
                                .token(token)
                                .build();
        }

        public AuthResponse registro(RegisterRequest registerRequest) {
                Rol rol = rolRepository.findById(registerRequest.getIdRol()).orElseThrow();
                Facultad facultad = facultadRepository.findById(registerRequest.getIdFacultad()).orElseThrow();
                Usuario usuario = Usuario.builder()
                                .username(registerRequest.getUsername())
                                .password(passwordEncoder.encode(registerRequest.getPassword()))
                                .nombre(registerRequest.getNombre())
                                .apellido(registerRequest.getApellido())
                                .email(registerRequest.getEmail())
                                .rol(rol)
                                .facultad(facultad)
                                .enabled(true)
                                .build();

                userRepository.save(usuario);

                String token = jwtService.getToken(usuario);
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(registerRequest.getEmail());
                message.setSubject("Bienvenido a nuestra plataforma de posgrados");
                message.setText("Tu nombre de usuario es: " + registerRequest.getUsername() + "\n"
                                + "Tu contraseña es: " + registerRequest.getPassword() + "\n"
                                + "Para cambiar tu contraseña, haz clic en el siguiente enlace: "
                                + urlCambiarPassword + "?token=" + jwtService.getToken(usuario));
                mailSender.send(message);

                return AuthResponse.builder()
                                .token(token)
                                .build();

        }

        // Refrescar token
        public AuthResponse refrescarToken(String token) {
                String username = jwtService.getUsernameFromToken(token);
                UserDetails usuario = userRepository.findByUsername(username).orElseThrow();
                return AuthResponse.builder()
                                .token(jwtService.getToken(usuario))
                                .build();
        }

        public String cambiarPassword(String username, String password) {
                Usuario usuario = userRepository.findByUsername(username).orElseThrow();
                usuario.setPassword(passwordEncoder.encode(password));
                userRepository.save(usuario);
                return "OK";
        }

        public String cambiarPasswordConToken(String token, String password) {
                String username = jwtService.getUsernameFromToken(token);
                return cambiarPassword(username, password);
        }

        public String olvideMiPassword(String email) {
                Usuario usuario = userRepository.findByEmail(email).orElseThrow();
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(email);
                message.setSubject("Recuperar contraseña");
                message.setText("Cambie la contraseña haciendo clic en el siguiente enlace: "
                                + urlCambiarPassword + "?token=" + jwtService.getToken(usuario));
                mailSender.send(message);
                return "OK";
        }
}
