package com.llc.financas.service;

import com.llc.financas.exception.ErroAutenticacao;
import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Usuario;
import com.llc.financas.model.repository.UsuarioRepository;
import com.llc.financas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl usuarioService;

    @MockBean
    UsuarioRepository usuarioRepository;

    @Test
    public void deveAutenticarUmUsuarioComSucesso(){
        Usuario usuario = criarUsuario();
        Mockito.when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.autenticar("usuario@email.com", "senha");

        Assertions.assertNotNull(result);
    }


    @Test
    public void deveLancarErroSeNaoEncontrarUsuarioComEmailInformado(){
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        ErroAutenticacao autenticacao = Assertions.assertThrows(ErroAutenticacao.class,
                () -> usuarioService.autenticar("usuario@email.com","senha"),
                "Se não encontrar o Email deve dar throw");

        Assertions.assertTrue(autenticacao.getMessage().contains("Usuario não encontrado"));
    }

    @Test
    public void deveLancarErroSeSenhaInformadaNaoBaterComEmailInformado(){
        Usuario usuario = criarUsuario();
        Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        ErroAutenticacao autenticacao = Assertions.assertThrows(ErroAutenticacao.class,
                () -> usuarioService.autenticar("usuario@email.com","1234"),
                "Se não encontrar o Email deve dar throw");

        Assertions.assertTrue(autenticacao.getMessage().contains("Senha Incorreta"));
    }

    @Test
    public void validarEmailComSucesso(){

        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

        usuarioService.validarEmail("usuario@email.com");
    }

    @Test
    public void deveLancarErroSeEmailJaForCadastrado(){
        Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

        RegraNegocioException regraNegocio = Assertions.assertThrows(RegraNegocioException.class,
                () -> usuarioService.validarEmail("usuario@email.com"),
                "Email já cadastrado deve dar throw");

        Assertions.assertTrue(regraNegocio.getMessage().contains("Já existe um usuario com esse email cadastrado!"));
    }

    @Test
    public void deveSalvarUmUsuario(){
        Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
        Usuario usuario = criarUsuario();
        Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = usuarioService.salvarUsuario(new Usuario());

        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(usuarioSalvo.getNome(),"user");
        Assertions.assertEquals(usuarioSalvo.getEmail(),"usuario@email.com");
        Assertions.assertEquals(usuarioSalvo.getSenha(),"senha");
    }


    public static Usuario criarUsuario(){
        Usuario usuario = Usuario.builder().
                nome("user").
                email("usuario@email.com").
                senha("senha").
                id(1l).
                build();
        return usuario;
    }
}
