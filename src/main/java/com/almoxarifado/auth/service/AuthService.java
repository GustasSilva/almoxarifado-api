package com.almoxarifado.auth.service;

import com.almoxarifado.auth.dto.AuthResponse;
import com.almoxarifado.auth.dto.LoginRequest;
import com.almoxarifado.auth.dto.RegisterRequest;
import com.almoxarifado.auth.entity.Perfil;
import com.almoxarifado.auth.entity.Usuario;
import com.almoxarifado.auth.repository.UsuarioRepository;
import com.almoxarifado.auth.security.JwtService;
import com.almoxarifado.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("E-mail já cadastrado: " + request.email());
        }

        var usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .perfil(request.perfil() != null ? request.perfil() : Perfil.OPERADOR)
                .build();

        usuarioRepository.save(usuario);
        return AuthResponse.of(jwtService.gerarToken(usuario), usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );

        var usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        return AuthResponse.of(jwtService.gerarToken(usuario), usuario);
    }
}
