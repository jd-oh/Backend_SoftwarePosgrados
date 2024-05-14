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
import com.ucaldas.posgrados.Entity.Rol;
import com.ucaldas.posgrados.Entity.Usuario;
import com.ucaldas.posgrados.Jwt.JwtService;
import com.ucaldas.posgrados.Repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UsuarioRepository userRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;
        private final AuthenticationManager authenticacionManager;
        @Autowired
        private final JavaMailSender mailSender;

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
                Usuario usuario = Usuario.builder()
                                .username(registerRequest.getUsername())
                                .password(passwordEncoder.encode(registerRequest.getPassword()))
                                .nombre(registerRequest.getNombre())
                                .apellido(registerRequest.getApellido())
                                .email(registerRequest.getEmail())
                                .rol(Rol.USUARIO)
                                .build();

                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(registerRequest.getEmail());
                message.setSubject("Bienvenido a nuestra plataforma de posgrados");
                message.setText("Tu nombre de usuario es: " + registerRequest.getUsername() + "\n"
                                + "Tu contraseña es: " + registerRequest.getPassword());
                mailSender.send(message);

                userRepository.save(usuario);
                return AuthResponse.builder()
                                .token(jwtService.getToken(usuario))
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

}
