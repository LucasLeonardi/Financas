package com.llc.financas.controller;

import com.llc.financas.DTO.LancamentoDTO;
import com.llc.financas.DTO.LancamentoStatusDTO;
import com.llc.financas.exception.RegraNegocioException;
import com.llc.financas.model.entity.Lancamento;
import com.llc.financas.model.entity.Usuario;
import com.llc.financas.model.enums.StatusLancamento;
import com.llc.financas.model.enums.TipoLancamento;
import com.llc.financas.service.LancamentoService;
import com.llc.financas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity salvarLancamento(@RequestBody LancamentoDTO lancamentoDTO){
        try {
            Lancamento lancamento = converter(lancamentoDTO);
            Lancamento lancamentoSalvo = lancamentoService.salvarLancamento(lancamento);
            return new  ResponseEntity(lancamentoSalvo, HttpStatus.CREATED);
        }catch (RegraNegocioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("{id}")
    public ResponseEntity buscarLancamentoPorId(@PathVariable("id") Long id){
        return lancamentoService.obterPorId(id)
                .map(lancamento -> new ResponseEntity(converterParaDTO(lancamento), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
    }

    @PutMapping("{id}")
    public  ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO){
        return lancamentoService.obterPorId(id).map(entity -> {
            try {
                Lancamento lancamento = converter(lancamentoDTO);
                lancamento.setId(entity.getId());
                lancamentoService.salvarLancamento(lancamento);
                return ResponseEntity.ok(lancamento);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }).orElseGet(() ->
                 new ResponseEntity("Lancamento nao encontrado", HttpStatus.BAD_REQUEST));
    }


    @PutMapping("{id}/statusAtualizado")
    public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody LancamentoStatusDTO statusLancamento){
        Optional<Lancamento> lancamento = lancamentoService.obterPorId(id);
        StatusLancamento statusLancamento1 = StatusLancamento.valueOf(statusLancamento.getStatus());
        if (statusLancamento1 == null){
            return ResponseEntity.badRequest().body("Não foi possivel atualizar o Status");
        }
        if(lancamento.isPresent()){
            try {
                Lancamento lancamentoAtualizado = lancamento.get();
                lancamentoAtualizado.setStatus(statusLancamento1);
                Lancamento lancamentoSalvo = lancamentoService.atualizarLancamento(lancamentoAtualizado);
                return ResponseEntity.ok(lancamentoSalvo);
            }catch (RegraNegocioException e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }else {
            return new ResponseEntity("Lancamento nao encontrado", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity buscarLancamento(
            @RequestParam(value = "descricao", required = false) String descricao,
            @RequestParam(value = "mes", required = false) Integer mes,
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "usuario") Long idUsuario
            ){
        Lancamento lancamentoFiltro = new Lancamento();
        lancamentoFiltro.setMes(mes);
        lancamentoFiltro.setAno(ano);
        lancamentoFiltro.setDescricao(descricao);
        Optional<Usuario> usuario = usuarioService.obterUsuario(idUsuario);
        if(!usuario.isPresent()){
            return ResponseEntity.badRequest().body("Não foi possivel realizar a consulta");
        }else {
            lancamentoFiltro.setUsuario(usuario.get());
        }
        List<Lancamento> lancamentos = lancamentoService.buscarLancamento(lancamentoFiltro);
        return ResponseEntity.ok(lancamentos);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deletar(@PathVariable("id") Long id){
        return lancamentoService.obterPorId(id).map(entity ->{
            lancamentoService.deletarLancamento(entity);
            return new  ResponseEntity(HttpStatus.NO_CONTENT);
        }).orElseGet(() -> new ResponseEntity("Lancamento nao encontrado", HttpStatus.BAD_REQUEST));
    }

    public LancamentoDTO converterParaDTO(Lancamento lancamento){
        return LancamentoDTO.builder()
                .ano(lancamento.getAno())
                .id(lancamento.getId())
                .descricao(lancamento.getDescricao())
                .mes(lancamento.getMes())
                .tipo(lancamento.getTipo().name())
                .status(lancamento.getStatus().name())
                .usuario(lancamento.getUsuario().getId())
                .build();
    }

    public Lancamento converter(LancamentoDTO lancamentoDTO){

        Lancamento lancamento = new Lancamento();
        lancamento.setId(lancamentoDTO.getId());
        lancamento.setAno(lancamentoDTO.getAno());
        lancamento.setDescricao(lancamentoDTO.getDescricao());
        lancamento.setMes(lancamentoDTO.getMes());
        lancamento.setValor(lancamentoDTO.getValor());

        Usuario usuario = usuarioService.obterUsuario(lancamentoDTO.getUsuario()).orElseThrow(() -> new RegraNegocioException("Usuario não encontrado"));

        lancamento.setUsuario(usuario);

        if(lancamentoDTO.getTipo() != null){
            lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo()));
        }

        if(lancamentoDTO.getStatus() != null){
            lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatus()));
        }

        return lancamento;
    }
}
