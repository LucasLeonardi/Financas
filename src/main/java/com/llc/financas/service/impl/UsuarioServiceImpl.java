package com.llc.financas.service.impl;

import com.llc.financas.exception.ErroAutenticacao;
import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Usuario;
import com.llc.financas.model.repository.UsuarioRepository;
import com.llc.financas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario autenticar(String email, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        if (!usuario.isPresent()){
            throw new ErroAutenticacao("Usuario não encontrado");
        }

        if(!usuario.get().getSenha().equals(senha)){
            throw new ErroAutenticacao("Senha Incorreta");
        }
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        validarEmail(usuario.getEmail());
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {
        boolean existe = usuarioRepository.existsByEmail(email);
        if(existe){
            throw new RegraNegocioException("Já existe um usuario com esse email cadastrado!");
        }
    }

    @Override
    public Optional<Usuario> obterUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario;
    }
}
