package com.almoxarifado.produto.dto;

import com.almoxarifado.produto.entity.Produto;
import com.almoxarifado.produto.entity.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProdutoResponse(
        Long id,
        String codigo,
        String nome,
        String descricao,
        Long categoriaId,
        String categoriaNome,
        UnidadeMedida unidadeMedida,
        BigDecimal estoqueAtual,
        BigDecimal estoqueMinimo,
        BigDecimal precoCusto,
        boolean abaixoDoMinimo,
        Boolean ativo,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {
    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(
                p.getId(),
                p.getCodigo(),
                p.getNome(),
                p.getDescricao(),
                p.getCategoria() != null ? p.getCategoria().getId() : null,
                p.getCategoria() != null ? p.getCategoria().getNome() : null,
                p.getUnidadeMedida(),
                p.getEstoqueAtual(),
                p.getEstoqueMinimo(),
                p.getPrecoCusto(),
                p.estaBaixoEstoqueMinimo(),
                p.getAtivo(),
                p.getCriadoEm(),
                p.getAtualizadoEm()
        );
    }
}
