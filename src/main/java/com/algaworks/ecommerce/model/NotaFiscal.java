package com.algaworks.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "nota_fiscal")
public class NotaFiscal extends EntidadeBaseInteger { //para funcionar no EclipseLink, nao extender entidade base por conta da forma da PK

    /* Id e versao na classe só necessarios se usar EclipseLink
    @Id
    private Integer id;
    @Version
    private Integer versao;
    */

    /*@JoinTable(name = "pedido_nota_fiscal", joinColumns = { @JoinColumn(name = "nota_fiscal_id", unique = true) },
            inverseJoinColumns = { @JoinColumn(name = "pedido_id", unique = true) })*/
    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_nota_fiscal_pedido")/*, @MapsId ja faz isso aqui: insertable = false, updatable = false*/)
    @MapsId
    @NotNull
    private Pedido pedido;

    @NotEmpty //serve pra lista, maps e arrays
    @Lob
    @Column(nullable = false)
    @Type(type = "org.hibernate.type.BinaryType") // necessário para PostGres
    private byte[] xml;

    @PastOrPresent
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_emissao", nullable = false)  // padrao é datetime(6) not null , pode ser preenchido com columnDefinition = "datetime(6) not null"
    private Date dataEmissao;
}
