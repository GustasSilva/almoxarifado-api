package com.almoxarifado.fornecedor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(
        @NotBlank(message = "Razão social obrigatória")
        @Size(max = 150, message = "Razão social deve ter no máximo 150 caracteres")
        String razaoSocial,

        @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido. Formato: 00.000.000/0000-00")
        String cnpj,

        @Size(max = 100)
        String contato,

        @Size(max = 20)
        String telefone,

        @Email(message = "E-mail inválido")
        @Size(max = 150)
        String email
) {}
