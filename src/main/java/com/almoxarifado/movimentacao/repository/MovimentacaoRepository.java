package com.almoxarifado.movimentacao.repository;

import com.almoxarifado.movimentacao.entity.Movimentacao;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    Page<Movimentacao> findByProduto_Id(Long produtoId, Pageable pageable);

    Page<Movimentacao> findByTipo(TipoMovimentacao tipo, Pageable pageable);

    @Query("""
            SELECT m FROM Movimentacao m
            WHERE (:produtoId IS NULL OR m.produto.id = :produtoId)
              AND (:tipo IS NULL OR m.tipo = :tipo)
              AND (m.criadoEm BETWEEN :inicio AND :fim)
            ORDER BY m.criadoEm DESC
            """)
    Page<Movimentacao> filtrar(
            @Param("produtoId") Long produtoId,
            @Param("tipo") TipoMovimentacao tipo,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim,
            Pageable pageable
    );

    List<Movimentacao> findTop10ByOrderByCriadoEmDesc();
}
