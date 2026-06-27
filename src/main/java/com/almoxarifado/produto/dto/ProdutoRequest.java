package com.almoxarifado.produto.dto;

import com.almoxarifado.produto.entity.UnidadeMedida;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "Código obrigatório")
        @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
        String codigo,

        @NotBlank(message = "Nome obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        String nome,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao,

        Long categoriaId,

        @NotNull(message = "Unidade de medida obrigatória")
        UnidadeMedida unidadeMedida,

        @NotNull(message = "Estoque mínimo obrigatório")
        @DecimalMin(value = "0", message = "Estoque mínimo não pode ser negativo")
        BigDecimal estoqueMinimo,

        @DecimalMin(value = "0", message = "Preço de custo não pode ser negativo")
        BigDecimal precoCusto
) {}
