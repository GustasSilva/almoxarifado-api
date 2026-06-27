package com.almoxarifado.fornecedor.dto;

import com.almoxarifado.fornecedor.entity.Fornecedor;

import java.time.LocalDateTime;

public record FornecedorResponse(
        Long id,
        String razaoSocial,
        String cnpj,
        String contato,
        String telefone,
        String email,
        Boolean ativo,
        LocalDateTime criadoEm
) {
    public static FornecedorResponse from(Fornecedor f) {
        return new FornecedorResponse(
                f.getId(), f.getRazaoSocial(), f.getCnpj(),
                f.getContato(), f.getTelefone(), f.getEmail(),
                f.getAtivo(), f.getCriadoEm()
        );
    }
}
