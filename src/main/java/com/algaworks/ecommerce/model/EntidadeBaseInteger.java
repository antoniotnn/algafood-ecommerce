package com.algaworks.ecommerce.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@MappedSuperclass
public class EntidadeBaseInteger {

//    @GeneratedValue(strategy = GenerationType.SEQUENCE) // para PostGres
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY) // para MYSQL
    private Integer id;

    /*
        serve para dar o lock otimista na entidade, evitar atualizações em concorrência.
        Tipos que pode assumir: Integer, Long, Date (porém não recomendado o Date ou qualquer tipo de data,
         pois ocupa mais espaço e pra controlar Versao, a data nao faz muito sentido)
     */
    @Version
    private Integer versao;

    /* para Multitenacy por COLUNA */
//    @NotBlank
//    private String tenant;
}
