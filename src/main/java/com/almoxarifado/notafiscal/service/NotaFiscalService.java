package com.almoxarifado.notafiscal.service;

import com.almoxarifado.notafiscal.dto.NotaFiscalItemRequest;
import com.almoxarifado.notafiscal.dto.NotaFiscalRequest;
import com.almoxarifado.notafiscal.dto.NotaFiscalResponse;
import com.almoxarifado.notafiscal.entity.NotaFiscal;
import com.almoxarifado.notafiscal.entity.NotaFiscalItem;
import com.almoxarifado.notafiscal.repository.NotaFiscalRepository;
import com.almoxarifado.produto.repository.ProdutoRepository;
import com.almoxarifado.shared.config.EmpresaProperties;
import com.almoxarifado.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ProdutoRepository produtoRepository;
    private final EmpresaProperties empresaProperties;
    private final PdfNotaFiscalService pdfService;

    @Transactional
    public NotaFiscalResponse emitir(NotaFiscalRequest request) {
        NotaFiscal nf = NotaFiscal.builder()
                .numero(proximoNumero())
                .tipo(request.tipo())
                .emitenteRazaoSocial(empresaProperties.getRazaoSocial())
                .emitenteCnpj(empresaProperties.getCnpj())
                .emitenteEndereco(empresaProperties.getEndereco())
                .destinatarioNome(request.destinatarioNome())
                .destinatarioCnpjCpf(request.destinatarioCnpjCpf())
                .destinatarioEndereco(request.destinatarioEndereco())
                .dataEmissao(LocalDateTime.now())
                .observacao(request.observacao())
                .valorTotal(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (NotaFiscalItemRequest itemReq : request.itens()) {
            NotaFiscalItem item = buildItem(nf, itemReq);
            nf.getItens().add(item);
            total = total.add(item.getValorTotal());
        }
        nf.setValorTotal(total);

        NotaFiscal saved = notaFiscalRepository.save(nf);

        String pdfPath = pdfService.gerarPdf(saved);
        saved.setPdfPath(pdfPath);
        notaFiscalRepository.save(saved);

        return NotaFiscalResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public NotaFiscalResponse buscarPorId(Long id) {
        return NotaFiscalResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public Page<NotaFiscalResponse> listar(Pageable pageable) {
        return notaFiscalRepository.findAllByOrderByCriadoEmDesc(pageable)
                .map(NotaFiscalResponse::from);
    }

    public byte[] obterPdf(Long id) {
        var nf = buscarEntidade(id);
        if (nf.getPdfPath() == null) {
            throw new ResourceNotFoundException("PDF não encontrado para a Nota Fiscal " + id);
        }
        try {
            return pdfService.lerPdf(nf.getPdfPath());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler o PDF da Nota Fiscal: " + e.getMessage(), e);
        }
    }

    private NotaFiscalItem buildItem(NotaFiscal nf, NotaFiscalItemRequest req) {
        var item = NotaFiscalItem.builder()
                .notaFiscal(nf)
                .descricao(req.descricao())
                .quantidade(req.quantidade())
                .unidadeMedida(req.unidadeMedida())
                .precoUnitario(req.precoUnitario())
                .valorTotal(req.quantidade().multiply(req.precoUnitario()))
                .build();

        if (req.produtoId() != null) {
            var produto = produtoRepository.findById(req.produtoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto", req.produtoId()));
            item.setProduto(produto);
        }

        return item;
    }

    private String proximoNumero() {
        return String.format("%09d", notaFiscalRepository.nextNumero());
    }

    private NotaFiscal buscarEntidade(Long id) {
        return notaFiscalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota Fiscal", id));
    }
}
