package com.almoxarifado.produto.controller;

import com.almoxarifado.produto.dto.ProdutoRequest;
import com.almoxarifado.produto.dto.ProdutoResponse;
import com.almoxarifado.produto.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Gerenciamento do catálogo de produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos paginados")
    public Page<ProdutoResponse> listar(
            @ParameterObject @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return produtoService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID")
    public ProdutoResponse buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar produto por código")
    public ProdutoResponse buscarPorCodigo(@PathVariable String codigo) {
        return produtoService.buscarPorCodigo(codigo);
    }

    @GetMapping("/alertas/estoque-minimo")
    @Operation(summary = "Listar produtos abaixo do estoque mínimo")
    public List<ProdutoResponse> abaixoDoMinimo() {
        return produtoService.listarAbaixoDoMinimo();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cadastrar novo produto")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ProdutoResponse criar(@Valid @RequestBody ProdutoRequest request) {
        return produtoService.criar(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ProdutoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest request) {
        return produtoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar produto")
    @PreAuthorize("hasRole('ADMIN')")
    public void desativar(@PathVariable Long id) {
        produtoService.desativar(id);
    }

    @PatchMapping("/{id}/reativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Reativar produto desativado")
    @PreAuthorize("hasRole('ADMIN')")
    public void reativar(@PathVariable Long id) {
        produtoService.reativar(id);
    }
}
