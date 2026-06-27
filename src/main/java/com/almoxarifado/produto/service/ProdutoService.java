package com.almoxarifado.produto.service;

import com.almoxarifado.categoria.repository.CategoriaRepository;
import com.almoxarifado.produto.dto.ProdutoRequest;
import com.almoxarifado.produto.dto.ProdutoResponse;
import com.almoxarifado.produto.entity.Produto;
import com.almoxarifado.produto.repository.ProdutoRepository;
import com.almoxarifado.shared.exception.BusinessException;
import com.almoxarifado.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public Page<ProdutoResponse> listar(Pageable pageable) {
        return produtoRepository.findAll(pageable).map(ProdutoResponse::from);
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return ProdutoResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorCodigo(String codigo) {
        return produtoRepository.findByCodigo(codigo)
                .map(ProdutoResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Produto com código " + codigo + " não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarAbaixoDoMinimo() {
        return produtoRepository.findAbaixoDoEstoqueMinimo()
                .stream()
                .map(ProdutoResponse::from)
                .toList();
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        if (produtoRepository.existsByCodigo(request.codigo())) {
            throw new BusinessException("Já existe um produto com o código: " + request.codigo());
        }

        var produto = Produto.builder()
                .codigo(request.codigo())
                .nome(request.nome())
                .descricao(request.descricao())
                .unidadeMedida(request.unidadeMedida())
                .estoqueMinimo(request.estoqueMinimo())
                .precoCusto(request.precoCusto())
                .build();

        if (request.categoriaId() != null) {
            var categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", request.categoriaId()));
            produto.setCategoria(categoria);
        }

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        var produto = buscarEntidade(id);

        if (!produto.getCodigo().equals(request.codigo()) && produtoRepository.existsByCodigo(request.codigo())) {
            throw new BusinessException("Já existe um produto com o código: " + request.codigo());
        }

        produto.setCodigo(request.codigo());
        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setUnidadeMedida(request.unidadeMedida());
        produto.setEstoqueMinimo(request.estoqueMinimo());
        produto.setPrecoCusto(request.precoCusto());

        if (request.categoriaId() != null) {
            var categoria = categoriaRepository.findById(request.categoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria", request.categoriaId()));
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }

        return ProdutoResponse.from(produtoRepository.save(produto));
    }

    @Transactional
    public void desativar(Long id) {
        var produto = buscarEntidade(id);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public void reativar(Long id) {
        var produto = buscarEntidade(id);
        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto", id));
    }
}
