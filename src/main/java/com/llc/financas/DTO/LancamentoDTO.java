package com.llc.financas.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {

    private Long id;
    private Integer mes;
    private Integer ano;
    private String descricao;
    private BigDecimal valor;
    private Long usuario;
    private String tipo;
    private String status;
}
