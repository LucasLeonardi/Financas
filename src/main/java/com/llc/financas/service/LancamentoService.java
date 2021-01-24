package com.llc.financas.service;

import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.enums.StatusLancamento;

import java.util.List;

public interface LancamentoService {

    Lancamento salvarLancamento(Lancamento lancamento);

    Lancamento atualizarLancamento(Lancamento lancamento);

    void deletarLancamento(Lancamento lancamento);

    List<Lancamento> buscarLancamento(Lancamento lancamentoFiltro);

    void atualizarStatus(Lancamento lancamento, StatusLancamento statusLancamento);

    void validarLancamento(Lancamento lancamento);

}
