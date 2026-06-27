package com.almoxarifado.movimentacao.dto;

import com.almoxarifado.movimentacao.entity.Movimentacao;
import com.almoxarifado.movimentacao.entity.MotivoMovimentacao;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
        Long id,
        TipoMovimentacao tipo,
        MotivoMovimentacao motivo,
        ProdutoResumo produto,
        BigDecimal quantidade,
        BigDecimal precoUnitario,
        BigDecimal valorTotal,
        BigDecimal estoqueAntes,
        BigDecimal estoqueDepois,
        FornecedorResumo fornecedor,
        UsuarioResumo usuario,
        String observacao,
        String numeroDocumento,
        LocalDateTime criadoEm
) {
    public record ProdutoResumo(Long id, String codigo, String nome) {}
    public record FornecedorResumo(Long id, String razaoSocial) {}
    public record UsuarioResumo(Long id, String nome) {}

    public static MovimentacaoResponse from(Movimentacao m) {
        return new MovimentacaoResponse(
                m.getId(),
                m.getTipo(),
                m.getMotivo(),
                new ProdutoResumo(m.getProduto().getId(), m.getProduto().getCodigo(), m.getProduto().getNome()),
                m.getQuantidade(),
                m.getPrecoUnitario(),
                m.getValorTotal(),
                m.getEstoqueAntes(),
                m.getEstoqueDepois(),
                m.getFornecedor() != null
                        ? new FornecedorResumo(m.getFornecedor().getId(), m.getFornecedor().getRazaoSocial())
                        : null,
                new UsuarioResumo(m.getUsuario().getId(), m.getUsuario().getNome()),
                m.getObservacao(),
                m.getNumeroDocumento(),
                m.getCriadoEm()
        );
    }
}
