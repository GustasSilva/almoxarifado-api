package com.almoxarifado.fornecedor.service;

import com.almoxarifado.fornecedor.dto.FornecedorRequest;
import com.almoxarifado.fornecedor.dto.FornecedorResponse;
import com.almoxarifado.fornecedor.entity.Fornecedor;
import com.almoxarifado.fornecedor.repository.FornecedorRepository;
import com.almoxarifado.shared.exception.BusinessException;
import com.almoxarifado.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        return fornecedorRepository.findByAtivoTrue().stream().map(FornecedorResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscarPorId(Long id) {
        return FornecedorResponse.from(buscarEntidade(id));
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        if (request.cnpj() != null && fornecedorRepository.existsByCnpj(request.cnpj())) {
            throw new BusinessException("Já existe um fornecedor com o CNPJ: " + request.cnpj());
        }
        var fornecedor = Fornecedor.builder()
                .razaoSocial(request.razaoSocial())
                .cnpj(request.cnpj())
                .contato(request.contato())
                .telefone(request.telefone())
                .email(request.email())
                .build();
        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        var fornecedor = buscarEntidade(id);
        if (request.cnpj() != null
                && !request.cnpj().equals(fornecedor.getCnpj())
                && fornecedorRepository.existsByCnpj(request.cnpj())) {
            throw new BusinessException("Já existe um fornecedor com o CNPJ: " + request.cnpj());
        }
        fornecedor.setRazaoSocial(request.razaoSocial());
        fornecedor.setCnpj(request.cnpj());
        fornecedor.setContato(request.contato());
        fornecedor.setTelefone(request.telefone());
        fornecedor.setEmail(request.email());
        return FornecedorResponse.from(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public void desativar(Long id) {
        var fornecedor = buscarEntidade(id);
        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
    }

    private Fornecedor buscarEntidade(Long id) {
        return fornecedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
    }
}
