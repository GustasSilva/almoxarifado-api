package com.almoxarifado.produto.repository;

import com.almoxarifado.produto.entity.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Produto p WHERE p.id = :id")
    Optional<Produto> findByIdComLock(Long id);
}
