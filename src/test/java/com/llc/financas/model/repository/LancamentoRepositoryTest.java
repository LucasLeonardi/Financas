package com.llc.financas.model.repository;

import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.enums.StatusLancamento;
import com.llc.financas.model.enums.TipoLancamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository lancamentoRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void deveSalvarUmLancamento(){
        Lancamento lancamento = criarLancamento();

        lancamento = lancamentoRepository.save(lancamento);

        Assertions.assertNotNull(lancamento.getId());
    }

    @Test
    public void deveDeletarUmLancamento(){
        Lancamento lancamento = criarLancamento();
        testEntityManager.persist(lancamento);
        lancamento = testEntityManager.find(Lancamento.class, lancamento.getId());
        lancamentoRepository.delete(lancamento);
        Lancamento lancamentoDeletado = testEntityManager.find(Lancamento.class, lancamento.getId());
        Assertions.assertNull(lancamentoDeletado);

    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarLancamento();
        testEntityManager.persist(lancamento);
        lancamento.setAno(2100);
        lancamento.setMes(5);
        lancamentoRepository.save(lancamento);
        Lancamento lancamentoAtual = testEntityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertEquals(lancamentoAtual.getAno(), 2100);
        Assertions.assertEquals(lancamentoAtual.getMes(), 5);
    }

    @Test
    public void deveBuscarLancamentoPorId(){
        Lancamento lancamento = criarLancamento();
        Lancamento lancamentoSalvo = testEntityManager.persist(lancamento);

        Lancamento lancamentoBuscado = lancamentoRepository.findById(lancamentoSalvo.getId()).get();

        Assertions.assertTrue(lancamentoBuscado != null);
    }

    public static Lancamento criarLancamento(){
        return Lancamento.builder()
                .ano(2020)
                .mes(2)
                .descricao("Qualquer")
                .status(StatusLancamento.PENDENTE)
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.DESPESA)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
