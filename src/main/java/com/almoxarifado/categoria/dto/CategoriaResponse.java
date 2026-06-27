package com.almoxarifado.categoria.dto;

import com.almoxarifado.categoria.entity.Categoria;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        Boolean ativo,
        LocalDateTime criadoEm
) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(), c.getAtivo(), c.getCriadoEm());
    }
}
