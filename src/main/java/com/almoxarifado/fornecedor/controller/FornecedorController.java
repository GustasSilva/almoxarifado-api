package com.almoxarifado.fornecedor.controller;

import com.almoxarifado.fornecedor.dto.FornecedorRequest;
import com.almoxarifado.fornecedor.dto.FornecedorResponse;
import com.almoxarifado.fornecedor.service.FornecedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
@Tag(name = "Fornecedores", description = "Gerenciamento de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class FornecedorController {

    private final FornecedorService fornecedorService;

    @GetMapping
    @Operation(summary = "Listar todos os fornecedores ativos")
    public List<FornecedorResponse> listar() {
        return fornecedorService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar fornecedor por ID")
    public FornecedorResponse buscarPorId(@PathVariable Long id) {
        return fornecedorService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar fornecedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public FornecedorResponse criar(@Valid @RequestBody FornecedorRequest request) {
        return fornecedorService.criar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar fornecedor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public FornecedorResponse atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorRequest request) {
        return fornecedorService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar fornecedor")
    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(@PathVariable Long id) {
        fornecedorService.desativar(id);
    }
}
