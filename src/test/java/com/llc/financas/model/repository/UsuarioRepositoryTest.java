package com.llc.financas.model.repository;


import com.llc.financas.model.entity.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    public void verificarSeExisteUmEmail(){
        Usuario usuario = criarUsuario();
        testEntityManager.persist(usuario);
        boolean result = usuarioRepository.existsByEmail("Usuario@email.com");
        Assertions.assertTrue(result);
    }

    @Test
    public void retornaFalsoQuandoNaoHouverUsuarioCadastradoComEmail(){
        boolean result = usuarioRepository.existsByEmail("Usuario@email.com");
        Assertions.assertFalse(result);
    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){
        Usuario usuario = criarUsuario();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        Assertions.assertNotNull(usuarioSalvo.getId());

    }

    @Test
    public void buscaUmUsuarioPorEmail(){
        Usuario usuario = criarUsuario();
        testEntityManager.persist(usuario);

        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    public void retornaVazioAoBuscarUsuarioPorEmailQueNaoEstaNaBaseDeDados(){
        Optional<Usuario> result = usuarioRepository.findByEmail("usuario@email.com");

        Assertions.assertFalse(result.isPresent());
    }

    public static Usuario criarUsuario(){
        return  Usuario.builder()
                .nome("user")
                .email("usuario@email.com")
                .senha("1234")
                .build();
    }
}
