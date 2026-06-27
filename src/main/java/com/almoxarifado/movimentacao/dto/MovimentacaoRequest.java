package com.almoxarifado.movimentacao.dto;

import com.almoxarifado.movimentacao.entity.MotivoMovimentacao;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record MovimentacaoRequest(
        @NotNull(message = "Tipo obrigatório")
        TipoMovimentacao tipo,

        @NotNull(message = "Motivo obrigatório")
        MotivoMovimentacao motivo,

        @NotNull(message = "Produto obrigatório")
        Long produtoId,

        @NotNull(message = "Quantidade obrigatória")
        @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @DecimalMin(value = "0", message = "Preço unitário não pode ser negativo")
        BigDecimal precoUnitario,

        Long fornecedorId,

        @Size(max = 500)
        String observacao,

        @Size(max = 50)
        String numeroDocumento
) {}
