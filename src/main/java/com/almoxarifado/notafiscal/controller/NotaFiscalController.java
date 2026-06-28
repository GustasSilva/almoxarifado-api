package com.almoxarifado.notafiscal.controller;

import com.almoxarifado.notafiscal.dto.NotaFiscalRequest;
import com.almoxarifado.notafiscal.dto.NotaFiscalResponse;
import com.almoxarifado.notafiscal.service.NotaFiscalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notas-fiscais")
@RequiredArgsConstructor
@Tag(name = "Notas Fiscais", description = "Emissão e consulta de notas fiscais (simulado)")
@SecurityRequirement(name = "bearerAuth")
public class NotaFiscalController {

    private final NotaFiscalService notaFiscalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Emitir nova nota fiscal e gerar PDF")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public NotaFiscalResponse emitir(@Valid @RequestBody NotaFiscalRequest request) {
        return notaFiscalService.emitir(request);
    }

    @GetMapping
    @Operation(summary = "Listar notas fiscais emitidas")
    public Page<NotaFiscalResponse> listar(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return notaFiscalService.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar nota fiscal por ID")
    public NotaFiscalResponse buscarPorId(@PathVariable Long id) {
        return notaFiscalService.buscarPorId(id);
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Download do PDF da nota fiscal")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = notaFiscalService.obterPdf(id);
        var nf = notaFiscalService.buscarPorId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "NF-%s-%s.pdf".formatted(nf.numero(), nf.serie()));

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
