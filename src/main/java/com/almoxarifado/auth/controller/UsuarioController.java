package com.almoxarifado.auth.controller;

import com.almoxarifado.auth.dto.UsuarioResponse;
import com.almoxarifado.auth.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/me")
    @Operation(summary = "Retorna os dados do usuário autenticado")
    public UsuarioResponse me(@AuthenticationPrincipal UserDetails userDetails) {
        return usuarioRepository.findByEmail(userDetails.getUsername())
                .map(UsuarioResponse::from)
                .orElseThrow();
    }
}
