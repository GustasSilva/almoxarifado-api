package com.almoxarifado.movimentacao.controller;

import com.almoxarifado.movimentacao.dto.MovimentacaoRequest;
import com.almoxarifado.movimentacao.dto.MovimentacaoResponse;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import com.almoxarifado.movimentacao.service.MovimentacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Movimentações", description = "Registro de entradas e saídas de estoque")
@SecurityRequirement(name = "bearerAuth")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar entrada ou saída de estoque")
    public MovimentacaoResponse registrar(@Valid @RequestBody MovimentacaoRequest request) {
        return movimentacaoService.registrar(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID")
    public MovimentacaoResponse buscarPorId(@PathVariable Long id) {
        return movimentacaoService.buscarPorId(id);
    }

    @GetMapping
    @Operation(summary = "Listar movimentações com filtros opcionais")
    public Page<MovimentacaoResponse> filtrar(
            @RequestParam(required = false) Long produtoId,
            @RequestParam(required = false) TipoMovimentacao tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return movimentacaoService.filtrar(produtoId, tipo, inicio, fim, pageable);
    }

    @GetMapping("/recentes")
    @Operation(summary = "Listar as 10 movimentações mais recentes")
    public List<MovimentacaoResponse> recentes() {
        return movimentacaoService.listarRecentes();
    }
}
