package com.mechanicsoft.Features.Auth.controller;

import com.mechanicsoft.Features.Auth.dto.LoginRequest;
import com.mechanicsoft.Features.Auth.dto.LoginResponse;
import com.mechanicsoft.Features.Usuarios.entity.Usuario;
import com.mechanicsoft.Features.Usuarios.repository.UsuarioRepository;
import com.mechanicsoft.exception.CredencialesInvalidasException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsuario(request.getUsuario())
                .orElseThrow(() -> new CredencialesInvalidasException("Usuario o contraseña incorrectos."));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new CredencialesInvalidasException("Este usuario está inactivo. Contacta a un administrador.");
        }

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException("Usuario o contraseña incorrectos.");
        }

        return LoginResponse.desde(usuario);
    }
}
