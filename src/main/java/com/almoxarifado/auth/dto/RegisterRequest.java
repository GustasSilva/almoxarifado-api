package com.almoxarifado.auth.dto;

import com.almoxarifado.auth.entity.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nome obrigatório")
        String nome,

        @NotBlank(message = "E-mail obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String senha,

        Perfil perfil
) {}
