package com.almoxarifado.notafiscal.dto;

import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import com.almoxarifado.notafiscal.entity.NotaFiscal;
import com.almoxarifado.notafiscal.entity.NotaFiscalItem;
import com.almoxarifado.notafiscal.entity.StatusNotaFiscal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record NotaFiscalResponse(
        Long id,
        String numero,
        String serie,
        TipoMovimentacao tipo,
        StatusNotaFiscal status,
        String emitenteRazaoSocial,
        String emitenteCnpj,
        String destinatarioNome,
        String destinatarioCnpjCpf,
        LocalDateTime dataEmissao,
        BigDecimal valorTotal,
        String observacao,
        List<ItemResponse> itens,
        LocalDateTime criadoEm
) {
    public record ItemResponse(
            Long id,
            Long produtoId,
            String descricao,
            BigDecimal quantidade,
            String unidadeMedida,
            BigDecimal precoUnitario,
            BigDecimal valorTotal
    ) {
        public static ItemResponse from(NotaFiscalItem item) {
            return new ItemResponse(
                    item.getId(),
                    item.getProduto() != null ? item.getProduto().getId() : null,
                    item.getDescricao(),
                    item.getQuantidade(),
                    item.getUnidadeMedida(),
                    item.getPrecoUnitario(),
                    item.getValorTotal()
            );
        }
    }

    public static NotaFiscalResponse from(NotaFiscal nf) {
        return new NotaFiscalResponse(
                nf.getId(),
                nf.getNumero(),
                nf.getSerie(),
                nf.getTipo(),
                nf.getStatus(),
                nf.getEmitenteRazaoSocial(),
                nf.getEmitenteCnpj(),
                nf.getDestinatarioNome(),
                nf.getDestinatarioCnpjCpf(),
                nf.getDataEmissao(),
                nf.getValorTotal(),
                nf.getObservacao(),
                nf.getItens().stream().map(ItemResponse::from).toList(),
                nf.getCriadoEm()
        );
    }
}
