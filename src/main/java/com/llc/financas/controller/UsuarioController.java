package com.llc.financas.controller;

import com.llc.financas.DTO.UsuarioDTO;
import com.llc.financas.exception.ErroAutenticacao;
import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Usuario;
import com.llc.financas.service.LancamentoService;
import com.llc.financas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private LancamentoService lancamentoService;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO usuarioDTO){
        try {
            Usuario usuarioAutenticado = usuarioService.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
            return ResponseEntity.ok(usuarioAutenticado);
        } catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping
    public ResponseEntity salvar(@RequestBody UsuarioDTO usuarioDTO){
        Usuario usuario = Usuario.builder().
                email(usuarioDTO.getEmail()).
                senha(usuarioDTO.getSenha()).
                nome(usuarioDTO.getNome()).
                build();

        try {
            Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
            return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/saldo")
    @Transactional(readOnly = true)
    private ResponseEntity mostrarSaldo(@PathVariable("id") Long id){
        Optional<Usuario> usuario = usuarioService.obterUsuario(id);
        if(!usuario.isPresent()){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        BigDecimal saldo = lancamentoService.obterSaldoDoUsuario(id);
        return ResponseEntity.ok(saldo);
    }
}
