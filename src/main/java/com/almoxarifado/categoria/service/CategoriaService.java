package com.almoxarifado.categoria.service;

import com.almoxarifado.categoria.dto.CategoriaRequest;
import com.almoxarifado.categoria.dto.CategoriaResponse;
import com.almoxarifado.categoria.entity.Categoria;
import com.almoxarifado.categoria.repository.CategoriaRepository;
import com.almoxarifado.shared.exception.BusinessException;
import com.almoxarifado.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findByAtivoTrue().stream().map(CategoriaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(Long id) {
        return CategoriaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        if (categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + request.nome());
        }
        var categoria = Categoria.builder().nome(request.nome()).descricao(request.descricao()).build();
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        var categoria = buscarEntidade(id);
        if (!categoria.getNome().equalsIgnoreCase(request.nome())
                && categoriaRepository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma categoria com o nome: " + request.nome());
        }
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        return CategoriaResponse.from(categoriaRepository.save(categoria));
    }

    @Transactional
    public void desativar(Long id) {
        var categoria = buscarEntidade(id);
        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }

    private Categoria buscarEntidade(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }
}
