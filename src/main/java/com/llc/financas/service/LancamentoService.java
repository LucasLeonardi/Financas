package com.llc.financas.service;

import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.enums.StatusLancamento;

import java.util.List;
import java.util.Optional;

public interface LancamentoService {

    Lancamento salvarLancamento(Lancamento lancamento);

    Lancamento atualizarLancamento(Lancamento lancamento);

    void deletarLancamento(Lancamento lancamento);

    List<Lancamento> buscarLancamento(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);

    void validarLancamento(Lancamento lancamento);

    Optional<Lancamento> obterPorId(Long id);

}
