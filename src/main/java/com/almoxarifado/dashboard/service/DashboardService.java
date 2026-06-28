package com.almoxarifado.dashboard.service;

import com.almoxarifado.dashboard.dto.DashboardResponse;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import com.almoxarifado.movimentacao.repository.MovimentacaoRepository;
import com.almoxarifado.produto.entity.Produto;
import com.almoxarifado.produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    @Transactional(readOnly = true)
    public DashboardResponse obter() {
        var estoque = montarResumoEstoque();
        var movimentacoes = montarResumoMovimentacoes();
        var alertas = montarAlertas();
        return new DashboardResponse(estoque, movimentacoes, alertas);
    }

    private DashboardResponse.Estoque montarResumoEstoque() {
        long total = produtoRepository.count();
        long ativos = produtoRepository.countByAtivoTrue();
        long abaixoMinimo = produtoRepository.countAbaixoDoEstoqueMinimo();
        BigDecimal valorTotal = produtoRepository.calcularValorTotalEstoque();
        return new DashboardResponse.Estoque(total, ativos, abaixoMinimo, valorTotal);
    }

    private DashboardResponse.Movimentacoes montarResumoMovimentacoes() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1).minusNanos(1);

        YearMonth mesAtual = YearMonth.now();
        LocalDateTime inicioMes = mesAtual.atDay(1).atStartOfDay();
        LocalDateTime fimMes = mesAtual.atEndOfMonth().atTime(23, 59, 59);

        long hoje = movimentacaoRepository.countByCriadoEmBetween(inicioDia, fimDia);
        long mes = movimentacaoRepository.countByCriadoEmBetween(inicioMes, fimMes);

        var resumoMes = movimentacaoRepository.resumoPorTipoNoPeriodo(inicioMes, fimMes);

        var entradas = extrairResumo(resumoMes, TipoMovimentacao.ENTRADA);
        var saidas = extrairResumo(resumoMes, TipoMovimentacao.SAIDA);

        return new DashboardResponse.Movimentacoes(hoje, mes, entradas, saidas);
    }

    private DashboardResponse.ResumoTipo extrairResumo(List<Object[]> rows, TipoMovimentacao tipo) {
        return rows.stream()
                .filter(row -> tipo.equals(row[0]))
                .findFirst()
                .map(row -> new DashboardResponse.ResumoTipo(
                        ((Number) row[1]).longValue(),
                        (BigDecimal) row[2]
                ))
                .orElse(new DashboardResponse.ResumoTipo(0L, BigDecimal.ZERO));
    }

    private List<DashboardResponse.AlertaEstoque> montarAlertas() {
        return produtoRepository.findAbaixoDoEstoqueMinimo()
                .stream()
                .map(this::toAlerta)
                .toList();
    }

    private DashboardResponse.AlertaEstoque toAlerta(Produto p) {
        return new DashboardResponse.AlertaEstoque(
                p.getId(), p.getCodigo(), p.getNome(),
                p.getEstoqueAtual(), p.getEstoqueMinimo()
        );
    }
}
