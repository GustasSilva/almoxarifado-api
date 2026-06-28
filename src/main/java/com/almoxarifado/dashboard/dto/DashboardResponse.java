package com.almoxarifado.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(
        Estoque estoque,
        Movimentacoes movimentacoes,
        List<AlertaEstoque> alertasEstoqueMinimo
) {
    public record Estoque(
            long totalProdutos,
            long produtosAtivos,
            long produtosAbaixoDoMinimo,
            BigDecimal valorTotalEmEstoque
    ) {}

    public record Movimentacoes(
            long hoje,
            long mes,
            ResumoTipo entradasMes,
            ResumoTipo saidasMes
    ) {}

    public record ResumoTipo(
            long quantidade,
            BigDecimal valorTotal
    ) {}

    public record AlertaEstoque(
            Long id,
            String codigo,
            String nome,
            BigDecimal estoqueAtual,
            BigDecimal estoqueMinimo
    ) {}
}
