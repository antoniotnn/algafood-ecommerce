package com.algaworks.ecommerce.model;


import com.algaworks.ecommerce.listener.GenericoListener;
import com.algaworks.ecommerce.listener.GerarNotaFiscalListener;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



//@Cacheable(value = false) //desabilita cache de Segundo Nivel.
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Pedido.dadosEssenciais", attributeNodes = {
        @NamedAttributeNode("dataCriacao"),
        @NamedAttributeNode("status"),
        @NamedAttributeNode("total"),
        @NamedAttributeNode(value = "cliente", subgraph = "cli"),
    }, subgraphs = {
        @NamedSubgraph(name = "cli", attributeNodes = {
                @NamedAttributeNode("nome"),
                @NamedAttributeNode("cpf"),
        })
    })
})
@Entity
@Getter
@Setter
@Table(name = "pedido")
@EntityListeners({ GerarNotaFiscalListener.class, GenericoListener.class })
public class Pedido extends EntidadeBaseInteger  /*implements PersistentAttributeInterceptable*/ { //serve para resolver o problema do OneToOne quando usa Lazy em atriuto non-owner


    //optional = false , colocar em atributos onde é Obrigatorio que esse atributo esteja preenchido, na hora de salvar o Objeto da Classe(Pedido por exemplo)
    //@ManyToOne(optional = false/*, cascade = CascadeType.PERSIST*/) //padrao é true. Com esse atributo, ele faz inner join ao inves de left outer join (que é menos performático)
    @ManyToOne(optional = false/*, fetch = FetchType.LAZY*/) //config LAZY foi utilizada para o estudo da interface impplementada acima porem comentada
    @JoinColumn(name = "cliente_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_pedido_cliente"))
    @NotNull
    private Cliente cliente;

    // orphanRemoval (só funciona com o cascade type persist junto)
    @OneToMany(mappedBy = "pedido" /*,orphanRemoval = true*/ /*,cascade = CascadeType.PERSIST*/ /*,cascade = CascadeType.MERGE*/ /*,cascade = CascadeType.REMOVE*/)
    @NotEmpty
    private List<ItemPedido> itens;

    @PastOrPresent
    @NotNull
    @Column(name = "data_criacao", updatable = false, nullable = false)
    private LocalDateTime dataCriacao; //datetime(6) notnull

    @PastOrPresent
    @Column(name = "data_ultima_atualizacao", insertable = false)
    private LocalDateTime dataUltimaAtualizacao;

    @PastOrPresent
    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    //@LazyToOne(LazyToOneOption.NO_PROXY) //necessário para remover o problema do n+1 com OneToOne em lazys no non-owner e tb ao usar o entitygraph
    @OneToOne(mappedBy = "pedido"/*, fetch = FetchType.LAZY*/) // aula na classe OneOneLazyTest, explicado problema do Lazy no OneToOne, colocar lazy em non-owner nao funciona
    private NotaFiscal notaFiscal;

    @Positive
    @NotNull
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal total;

    @NotNull
    @Column(length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    //@LazyToOne(LazyToOneOption.NO_PROXY) //necessário para remover o problema do n+1 com OneToOne em lazys no non-owner e tb ao usar o entitygraph
    @OneToOne(mappedBy = "pedido"/*, fetch = FetchType.LAZY*/) // aula na classe OneOneLazyTest, explicado problema do Lazy no OneToOne, colocar lazy em non-owner nao funciona
    private Pagamento pagamento;

    @NotNull
    @Embedded
    private EnderecoEntregaPedido enderecoEntrega;

    /*
        // aula na classe OneOneLazyTest, explicado problema do Lazy no OneToOne, colocar lazy em non-owner nao funciona
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Transient
    private PersistentAttributeInterceptor persistentAttributeInterceptor;

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return this.persistentAttributeInterceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(PersistentAttributeInterceptor persistentAttributeInterceptor) {
        this.persistentAttributeInterceptor = persistentAttributeInterceptor;
    }

    public NotaFiscal getNotaFiscal() {
        if (this.persistentAttributeInterceptor != null) {
            return (NotaFiscal) persistentAttributeInterceptor
                    .readObject(this, "notaFiscal", this.notaFiscal);
        }
        return this.notaFiscal;
    }

    public void setNotaFiscal(NotaFiscal notaFiscal) {
        if (this.persistentAttributeInterceptor != null) {
            this.notaFiscal = (NotaFiscal) persistentAttributeInterceptor
                    .writeObject(this, "notaFiscal", this.notaFiscal, notaFiscal);
        } else {
            this.notaFiscal = notaFiscal;
        }
    }

    public Pagamento getPagamento() {
        if (this.persistentAttributeInterceptor != null) {
            return (Pagamento) persistentAttributeInterceptor
                    .readObject(this, "pagamento", this.pagamento);
        }
        return this.pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        if (this.persistentAttributeInterceptor != null) {
            this.pagamento = (Pagamento) persistentAttributeInterceptor
                    .writeObject(this, "pagamento", this.pagamento, pagamento);
        } else {
            this.pagamento = pagamento;
        }
    }

     */


    public boolean isPago() {
        return StatusPedido.PAGO.equals(status);
    }

//    @PrePersist
//    @PreUpdate
    public void calcularTotal(){
        if (itens != null) {
            total = itens.stream().map(
                    item -> new BigDecimal(item.getQuantidade()).multiply(item.getPrecoProduto()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            total = BigDecimal.ZERO;
        }
    }

    @PrePersist
    public void aoPersistir() {
        dataCriacao = LocalDateTime.now();
        calcularTotal();
    }

    @PreUpdate
    public void aoAtualizar() {
        dataUltimaAtualizacao = LocalDateTime.now();
        calcularTotal();
    }

    @PostPersist
    public void aposPersistir(){
        System.out.println("Após persistir Pedido");
    }

    @PostUpdate
    public void aposAtualizar(){
        System.out.println("Após atualizar Pedido");
    }

    @PreRemove
    public void antesDeRemover(){
        System.out.println("Antes de remover Pedido");
    }

    @PostRemove
    public void aposRemover(){
        System.out.println("Após remover Pedido");
    }

    @PostLoad
    public void aoCarregar(){
        System.out.println("Após carregar o Pedido");
    }

}
