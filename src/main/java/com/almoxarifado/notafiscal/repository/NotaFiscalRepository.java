package com.almoxarifado.notafiscal.repository;

import com.almoxarifado.notafiscal.entity.NotaFiscal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, Long> {

    @Query(value = "SELECT nextval('nota_fiscal_numero_seq')", nativeQuery = true)
    long nextNumero();

    Page<NotaFiscal> findAllByOrderByCriadoEmDesc(Pageable pageable);
}
