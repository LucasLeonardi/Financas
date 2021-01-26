package com.llc.financas.service.impl;

import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.enums.StatusLancamento;
import com.llc.financas.model.repository.LancamentoRepository;
import com.llc.financas.service.LancamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LancamentoServiceImpl implements LancamentoService {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Override
    @Transactional
    public Lancamento salvarLancamento(Lancamento lancamento) {
        validarLancamento(lancamento);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional(readOnly = true)
    public Lancamento atualizarLancamento(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        validarLancamento(lancamento);
        return lancamentoRepository.save(lancamento);
    }

    @Override
    @Transactional
    public void deletarLancamento(Lancamento lancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamentoRepository.delete(lancamento);
    }

    @Override
    public List<Lancamento> buscarLancamento(Lancamento lancamentoFiltro) {
        Example example = Example.of(lancamentoFiltro,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return lancamentoRepository.findAll(example);
    }

    @Override
    public void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento) {
        Objects.requireNonNull(lancamento.getId());
        lancamento.setStatus(statusLancamento);
        lancamentoRepository.save(lancamento);
    }

    @Override
    public void validarLancamento(Lancamento lancamento) {
        if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")){
            throw new RegraNegocioException("Coloque uma descrição valida");
        }

        if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12){
            throw new RegraNegocioException("Coloque um mês valida");
        }

        if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4){
            throw new RegraNegocioException("Coloque um ano valida");
        }

        if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null){
            throw new RegraNegocioException("Informe um usuario");
        }

        if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1){
            throw new RegraNegocioException("Informe um valor valido");
        }

        if(lancamento.getTipo() == null){
            throw new RegraNegocioException("Informe um tipo valido");
        }
    }

    @Override
    public Optional<Lancamento> obterPorId(Long id) {
        Optional<Lancamento> lancamento = lancamentoRepository.findById(id);
        return lancamento;
    }


}
