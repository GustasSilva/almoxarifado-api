package com.almoxarifado.notafiscal.dto;

import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record NotaFiscalRequest(
        @NotNull(message = "Tipo é obrigatório (ENTRADA ou SAIDA)")
        TipoMovimentacao tipo,

        @NotBlank(message = "Nome do destinatário é obrigatório")
        String destinatarioNome,

        String destinatarioCnpjCpf,
        String destinatarioEndereco,
        String observacao,

        @NotEmpty(message = "A nota fiscal deve ter pelo menos um item")
        @Valid
        List<NotaFiscalItemRequest> itens
) {}
