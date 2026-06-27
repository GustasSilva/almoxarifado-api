package com.almoxarifado.auth.dto;

import com.almoxarifado.auth.entity.Perfil;

public record AuthResponse(
        String token,
        String tipo,
        Long id,
        String nome,
        String email,
        Perfil perfil
) {
    public static AuthResponse of(String token, com.almoxarifado.auth.entity.Usuario usuario) {
        return new AuthResponse(token, "Bearer", usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}
