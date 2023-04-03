package com.algaworks.ecommerce.model;

import lombok.*;
import org.hibernate.validator.constraints.br.CPF;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

//o name não precisa ser o nome exatamente da procedure do banco, mas nesse caso, usei, ja em procedureName precisa.
@NamedStoredProcedureQuery(name = "compraram_acima_media", procedureName = "compraram_acima_media",
    parameters = {
        @StoredProcedureParameter(name = "ano", type = Integer.class, mode = ParameterMode.IN)
    },
    resultClasses = Cliente.class
)
@Getter
@Setter
@SecondaryTable(name = "cliente_detalhe" , pkJoinColumns = @PrimaryKeyJoinColumn(name = "cliente_id"),
        foreignKey = @ForeignKey(name = "fk_cliente_detalhe_cliente")) /* em SecondaryTable, o foreignKey só funciona se for usado dentro da anotação SecondaryTable e náo dentro do @PrimaryKeyJoinColumn */
@Entity
@Table(name = "cliente",
        uniqueConstraints = { @UniqueConstraint(name = "unq_cliente_cpf", columnNames = { "cpf" }) },
        indexes = { @Index(name = "idx_cliente_nome", columnList = "nome") })
public class Cliente extends EntidadeBaseInteger {


    @NotBlank
    @Column(length = 100, nullable = false)
    private String nome;

//    @Pattern(regexp = "(^\\d{3}\\x2E\\d{3}\\x2E\\d{3}\\x2D\\d{2}$)")  // professor fez assim, mas ja existe anotacao @CPF q faz um regex pra validar cpf
    @CPF
    @NotNull
    @Column(length = 14, nullable = false) // serve para ao criar o DDL gerar um script pra not null. Não serve para validar na hora da inserção no java. Pra isso usar a anotation @NotNull
    private String cpf;

    @ElementCollection
    @CollectionTable(name = "cliente_contato",
            joinColumns = @JoinColumn(name = "cliente_id",
                    foreignKey = @ForeignKey(name = "fk_cliente_contato_cliente")))
    @MapKeyColumn(name = "tipo")
    @Column(name = "descricao")
    private Map<String, String> contatos;

    @Transient
    private String primeiroNome;

    @NotNull
    @Column(table = "cliente_detalhe", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private SexoCliente sexo;

    @NotNull
    @Past
    @Column(name = "data_nascimento", table = "cliente_detalhe")
    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;

    @PostLoad
    public void configurarPrimeiroNome() {
        if(nome != null && !nome.isBlank()) {
            int index = nome.indexOf(" ");
            if(index > -1) {
                primeiroNome = nome.substring(0, index);
            }
        }
    }
}
