package com.algaworks.ecommerce.criteria;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.*;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ExpressoesCondicionaisCriteriaTest extends EntityManagerTest {

    @Test
    public void usarExpressaoIN02() {
        Cliente cliente01 = entityManager.find(Cliente.class, 1);

        Cliente cliente02 = new Cliente();
        cliente02.setId(2);

        List<Cliente> clientes = Arrays.asList(cliente01, cliente02);
//        List<Integer> clientes = Arrays.asList(cliente01.getId(), cliente02.getId());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        criteriaQuery.where(root.get(Pedido_.cliente).in(clientes));
//        criteriaQuery.where(root.get(Pedido_.cliente).get(Cliente_.id).in(clientes));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void usarExpressaoIN01() {
        List<Integer> ids = Arrays.asList(1, 3, 4, 6);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        criteriaQuery.where(root.get(Pedido_.id).in(ids));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void usarExpressoesCase() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> criteriaQuery = criteriaBuilder.createQuery(Object[].class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        /*
            OBS: Tanto no JPQL quanto no Criteria, quando se usa o WHEN e usa-se parametro, não funciona o bind corretamente.
            Para funcionar, ao invés de passar no case o atributo do metamodel, no caso Pedido_.status, deve se passar Pedido_.STATUS,
            de letra maíuscula mesmo, que aí ele pega uma String que é representa o nome do status, fora isso dentro dos when,
            deve se colocar um .toString() como no exemplo a abaixo
         */

        /*
            criteriaQuery.multiselect(
                root.get(Pedido_.id),
        //                criteriaBuilder.selectCase(root.get(Pedido_.status))
                    criteriaBuilder.selectCase(root.get(Pedido_.STATUS))
        //                    .when(StatusPedido.PAGO, "Foi pago.")
                        .when(StatusPedido.PAGO.toString(), "Foi pago.")
        //                    .when(StatusPedido.AGUARDANDO, "Está aguardando.")
                        .when(StatusPedido.AGUARDANDO.toString(), "Está Aguardando.")
                        .otherwise(root.get(Pedido_.status))
            );
         */

        /*
            Da forma abaixo também dá o mesmo problema mencionado acima (o Jpa tenta passar parâmetro mas que não é reconhecido no SQL),
            então deve se colocar o .as(String.class) para passar como String e fazer o bind corretamente),
         */
        criteriaQuery.multiselect(
            root.get(Pedido_.id),
//            criteriaBuilder.selectCase(root.get(Pedido_.pagamento).type())
            criteriaBuilder.selectCase(root.get(Pedido_.pagamento).type().as(String.class))
//                .when(PagamentoBoleto.class, "Foi pago com Boleto.")
                .when("boleto", "Foi pago com Boleto.")
//                .when(PagamentoCartao.class, "Foi pago com Cartão")
                .when("cartao", "Foi pago com Cartão")
                .otherwise("Não Identificado")
        );

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));
    }

    @Test
    public void usarExpressaoDiferente() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        criteriaQuery.where(criteriaBuilder.notEqual(root.get(Pedido_.total), new BigDecimal("499")));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(pedido -> System.out.println(
                "ID: " + pedido.getId() + ", Total: " + pedido.getTotal()));
    }

    @Test
    public void usarBetween() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

//        criteriaQuery.where(criteriaBuilder.between(
//                root.get(Pedido_.total), new BigDecimal("499"), new BigDecimal("2398")));

        criteriaQuery.where(criteriaBuilder.between(
                root.get(Pedido_.dataCriacao),
                LocalDateTime.now().minusDays(5).withSecond(0).withMinute(0).withHour(0),
                LocalDateTime.now()));



        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(pedido -> System.out.println(
                "ID: " + pedido.getId() + ", Total: " + pedido.getTotal()));
    }

    @Test
    public void usarMaiorMenorComDatas() {
//        //Exercicio: Trazer todos os pedidos que foram cadastrados na base nos últimos 3 dias

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(
                root.get(Pedido_.dataCriacao), LocalDateTime.now().minusDays(3)));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(pedido -> System.out.println("ID: " + pedido.getId()));
    }

    @Test
    public void usarMaiorMenor() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        //assim (SEM METAMODEL) funciona normal o teste , porém usando Metamodel, valida até o TIPO.
//        criteriaQuery.where(criteriaBuilder.greaterThan(root.get("preco"), 799));

        /* assim (COM METAMODEL) valida até mesmo o TIPO, por exemplo os parametros iguais ao acima (exemplo abaixo)
            como:  root.get(Produto_.preco), 799  não passam pois o 799 é string e não BigDecimal.
            Ex: criteriaQuery.where(criteriaBuilder.greaterThan(root.get(Produto_.preco), 799));  assim não passa
            Ex abaixo com metamodel mostra como funciona. Temos q passar os mesmos tipos nos 2 parâmetros.
         */
//        criteriaQuery.where(criteriaBuilder.greaterThan(root.get(Produto_.preco), new BigDecimal("799")));

//        criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get(Produto_.preco), new BigDecimal("799")));

//        criteriaQuery.where(criteriaBuilder.lessThan(root.get(Produto_.preco), new BigDecimal("3500")));

        //aceita dois parametros
//        criteriaQuery.where(
//            criteriaBuilder.greaterThan(root.get(Produto_.preco), new BigDecimal("799")),
//            criteriaBuilder.lessThan(root.get(Produto_.preco), new BigDecimal("3500"))
//        );

        //aceita dois parametros
        criteriaQuery.where(
                criteriaBuilder.greaterThanOrEqualTo(root.get(Produto_.preco), new BigDecimal("799")),
                criteriaBuilder.lessThanOrEqualTo(root.get(Produto_.preco), new BigDecimal("3500"))
        );

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(produto -> System.out.println(
                "ID: " + produto.getId() + ", Nome: " + produto.getNome() + ", Preço: " + produto.getPreco()));
    }

    @Test
    public void usarIsEmpty() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        criteriaQuery.where(criteriaBuilder.isNotEmpty(root.get(Produto_.categorias)));

        /*
            ATENÇÃO ESSE ABAIXO NAO FUNCIONA
            para coleções isso é invalido, Gera um SQL COM ERRO DE SINTAXE MAS NAO FUNCIONA (IS NULL COM COLEÇAO)
            criteriaQuery.where(root.get(Produto_.categorias).isNull()); //
         */

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void usarIsNull() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

//        criteriaQuery.where(root.get(Produto_.fotoProduto).isNull());
        criteriaQuery.where(criteriaBuilder.isNull(root.get(Produto_.foto))); //isNotNull

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void usarExpressaoCondicionalLike() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteriaQuery = criteriaBuilder.createQuery(Cliente.class);
        Root<Cliente> root = criteriaQuery.from(Cliente.class);

        criteriaQuery.select(root);

        criteriaQuery.where(criteriaBuilder.like(root.get(Cliente_.nome), "%a%"));

        TypedQuery<Cliente> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

}
