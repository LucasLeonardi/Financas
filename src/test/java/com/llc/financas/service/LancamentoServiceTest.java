package com.llc.financas.service;

import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.entity.Usuario;
import com.llc.financas.model.enums.StatusLancamento;
import com.llc.financas.model.enums.TipoLancamento;
import com.llc.financas.model.repository.LancamentoRepository;
import com.llc.financas.model.repository.LancamentoRepositoryTest;
import com.llc.financas.service.impl.LancamentoServiceImpl;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

    @SpyBean
    LancamentoServiceImpl lancamentoService;

    @MockBean
    LancamentoRepository lancamentoRepository;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doNothing().when(lancamentoService).validarLancamento(lancamentoSalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvar.setId(1l);
        Mockito.when(lancamentoRepository.save(lancamentoSalvar)).thenReturn(lancamentoSalvo);


        Lancamento lancamento =  lancamentoService.salvarLancamento(lancamentoSalvar);

        Assertions.assertEquals(lancamento.getId(), lancamentoSalvo.getId());
        Assertions.assertEquals(lancamentoSalvo.getStatus(), StatusLancamento.PENDENTE);
    }

    @Test
    public void naoDeveSalvarSeHouverErroDeValidacao(){
        Lancamento lancamentoSalvar = LancamentoRepositoryTest.criarLancamento();
        Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validarLancamento(lancamentoSalvar);

        Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.salvarLancamento(lancamentoSalvar));

        Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoSalvar);
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
        Mockito.doNothing().when(lancamentoService).validarLancamento(lancamentoSalvo);

        lancamentoService.atualizarLancamento(lancamentoSalvo);

        Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
    }

    @Test
    public void deveLancarErroAoTentarAtualizarLancamentoNaoSalvo(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrows(NullPointerException.class,
                () -> lancamentoService.atualizarLancamento(lancamento));
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);

        lancamentoService.deletarLancamento(lancamento);

        Mockito.verify(lancamentoRepository).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUsuarioInexistente(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

        Assertions.assertThrows(NullPointerException.class,
                () -> lancamentoService.deletarLancamento(lancamento));

        Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);
    }

    @Test
    public void deveRetornarTodosOsLancamentos(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        List<Lancamento> lancamentos = Arrays.asList(lancamento);

        Mockito.when(lancamentoRepository.findAll(Mockito.any(Example.class))).thenReturn(lancamentos);

        Assertions.assertTrue(lancamentos.contains(lancamento));
    }

    @Test
    public void deveAtualizarStatusDeUmLancamento(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);
        StatusLancamento statusLancamento = StatusLancamento.EFETIVADO;
        Mockito.doReturn(lancamento).when(lancamentoService).atualizarLancamento(lancamento);
        Mockito.doNothing().when(lancamentoService).validarLancamento(lancamento);

        lancamentoService.atualizarStatus(lancamento, statusLancamento);

        Mockito.verify(lancamentoRepository).save(lancamento);
        Assertions.assertTrue(lancamento.getStatus().equals(statusLancamento));
    }

    @Test
    public void deveReceberUmLancamentoPorId(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.of(lancamento));

        Optional<Lancamento> lancamentoBuscado = lancamentoService.obterPorId(lancamento.getId());

        Assertions.assertTrue(lancamentoBuscado.isPresent());
    }

    @Test
    public void deveReceberVazioSeNaoEncontrarLancamentoPorId(){
        Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
        lancamento.setId(1l);
        Mockito.when(lancamentoRepository.findById(lancamento.getId())).thenReturn(Optional.empty());

        Optional<Lancamento> lancamentoBuscado = lancamentoService.obterPorId(lancamento.getId());

        Assertions.assertFalse(lancamentoBuscado.isPresent());
    }

    @Test
    public void deveJogarUmaExcessaoAoTentarValidarLancamento(){
        Lancamento lancamento = new Lancamento();

        Throwable exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Coloque uma descrição valida"));

        lancamento.setDescricao("Descrisao");

        exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Coloque um mês valida"));

        lancamento.setMes(1);

        exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Coloque um ano valida"));

        lancamento.setAno(2021);

        exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Informe um usuario"));

        Usuario usuario = new Usuario();
        usuario.setId(1l);
        lancamento.setUsuario(usuario);

        exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Informe um valor valido"));

        lancamento.setValor(BigDecimal.valueOf(100));

        exception = Assertions.assertThrows(RegraNegocioException.class,
                () -> lancamentoService.validarLancamento(lancamento));

        Assertions.assertTrue(exception.getMessage().equals("Informe um tipo valido"));
    }
}

