package com.almoxarifado.notafiscal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record NotaFiscalItemRequest(
        Long produtoId,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao,

        @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @NotBlank(message = "Unidade de medida é obrigatória")
        String unidadeMedida,

        @NotNull(message = "Preço unitário é obrigatório")
        @DecimalMin(value = "0.0001", message = "Preço unitário deve ser maior que zero")
        BigDecimal precoUnitario
) {}
