package com.almoxarifado.movimentacao.service;

import com.almoxarifado.auth.repository.UsuarioRepository;
import com.almoxarifado.fornecedor.repository.FornecedorRepository;
import com.almoxarifado.movimentacao.dto.MovimentacaoRequest;
import com.almoxarifado.movimentacao.dto.MovimentacaoResponse;
import com.almoxarifado.movimentacao.entity.Movimentacao;
import com.almoxarifado.movimentacao.entity.TipoMovimentacao;
import com.almoxarifado.movimentacao.repository.MovimentacaoRepository;
import com.almoxarifado.produto.repository.ProdutoRepository;
import com.almoxarifado.shared.exception.BusinessException;
import com.almoxarifado.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public MovimentacaoResponse registrar(MovimentacaoRequest request) {
        var produto = produtoRepository.findByIdComLock(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto", request.produtoId()));

        var estoqueAntes = produto.getEstoqueAtual();

        if (request.tipo() == TipoMovimentacao.SAIDA) {
            if (estoqueAntes.compareTo(request.quantidade()) < 0) {
                throw new BusinessException(
                        "Estoque insuficiente. Disponível: %s, Solicitado: %s"
                                .formatted(estoqueAntes.toPlainString(), request.quantidade().toPlainString())
                );
            }
            produto.setEstoqueAtual(estoqueAntes.subtract(request.quantidade()));
        } else {
            produto.setEstoqueAtual(estoqueAntes.add(request.quantidade()));
        }

        var estoqueDepois = produto.getEstoqueAtual();
        produtoRepository.save(produto);

        var emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        var usuario = usuarioRepository.findByEmail(emailLogado).orElseThrow();

        var movimentacao = Movimentacao.builder()
                .tipo(request.tipo())
                .motivo(request.motivo())
                .produto(produto)
                .quantidade(request.quantidade())
                .precoUnitario(request.precoUnitario())
                .estoqueAntes(estoqueAntes)
                .estoqueDepois(estoqueDepois)
                .usuario(usuario)
                .observacao(request.observacao())
                .numeroDocumento(request.numeroDocumento())
                .build();

        if (request.fornecedorId() != null) {
            var fornecedor = fornecedorRepository.findById(request.fornecedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", request.fornecedorId()));
            movimentacao.setFornecedor(fornecedor);
        }

        return MovimentacaoResponse.from(movimentacaoRepository.save(movimentacao));
    }

    @Transactional(readOnly = true)
    public MovimentacaoResponse buscarPorId(Long id) {
        return MovimentacaoResponse.from(
                movimentacaoRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Movimentação", id))
        );
    }

    @Transactional(readOnly = true)
    public Page<MovimentacaoResponse> filtrar(Long produtoId,
                                              TipoMovimentacao tipo,
                                              LocalDateTime inicio,
                                              LocalDateTime fim,
                                              Pageable pageable) {
        var dataInicio = inicio != null ? inicio : LocalDateTime.of(2000, 1, 1, 0, 0);
        var dataFim = fim != null ? fim : LocalDateTime.now();
        return movimentacaoRepository.filtrar(produtoId, tipo, dataInicio, dataFim, pageable)
                .map(MovimentacaoResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponse> listarRecentes() {
        return movimentacaoRepository.findTop10ByOrderByCriadoEmDesc()
                .stream()
                .map(MovimentacaoResponse::from)
                .toList();
    }
}
