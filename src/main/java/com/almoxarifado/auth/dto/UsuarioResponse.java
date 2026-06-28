package com.almoxarifado.auth.dto;

import com.almoxarifado.auth.entity.Perfil;
import com.almoxarifado.auth.entity.Usuario;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        Boolean ativo
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getAtivo());
    }
}
