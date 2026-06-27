package com.almoxarifado.produto.repository;

import com.almoxarifado.produto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByAtivoTrue();
    Optional<Produto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.estoqueAtual < p.estoqueMinimo")
    List<Produto> findAbaixoDoEstoqueMinimo();

    List<Produto> findByCategoria_IdAndAtivoTrue(Long categoriaId);
}
