package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Cliente;
import com.algaworks.ecommerce.model.Pedido;
import com.algaworks.ecommerce.model.Produto;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ExpressoesCondicionaisTest extends EntityManagerTest {

    @Test
    public void usarExpressaoIN() {
        Cliente cliente1 = entityManager.find(Cliente.class, 1);
        Cliente cliente2 = entityManager.find(Cliente.class, 2);

        List<Cliente> clientes = Arrays.asList(cliente1, cliente2);

        String jpql = "select p from Pedido p where p.cliente in (:clientes) ";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);
        typedQuery.setParameter("clientes", clientes);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

    @Test
    public void usarExpressoesCase() {
//        String jpql = "select p.id, " +
//                "case p.status " +
//                "   when 'PAGO' then 'Está pago' " +
//                "   when 'CANCELADO' then 'Foi cancelado' " +
//                "   else 'Está aguardando' " +
//                "end " +
//                "from Pedido p ";

        //OBS: Tanto no JPQL quanto criteria se passar parametro no WHEN, por exemplo :parametro , dá erro e nao passa.
        String jpql = "select p.id, " +
                "case type (p.pagamento) " +
                "   when PagamentoBoleto then 'Pago com Boleto' " +
                "   when PagamentoCartao then 'Pago com Cartão' " +
                "   else 'Não pago ainda' " +
                "end " +
                "from Pedido p ";


        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));
    }

    @Test
    public void usarExpressaoDiferente() {

        String jpql = "select p from Produto p where p.preco <> 100";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }

    //@Test
    public void usarBetween() {

        String jpql = "select p from Pedido p " +
                " where p.dataCriacao between :dataInicial and :dataFinal";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);
        typedQuery.setParameter("dataInicial", LocalDateTime.now().minusDays(2));
        typedQuery.setParameter("dataFinal", LocalDateTime.now());

        List<Pedido> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }

    //@Test
    public void usarMaiorMenorComDatas() { //todos os pedidos dos dois ultimos dias / tem que ajeitar o insert pra funcionar.

        String jpql = "select p from Pedido p " +
                "where p.dataCriacao > :data";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);
        typedQuery.setParameter("data", LocalDateTime.now().minusDays(2));

        List<Pedido> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }


    @Test
    public void usarMaiorMenor() {

        String jpql = "select p from Produto p " +
                " where p.preco >= :precoInicial and p.preco <= :precoFinal";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);
        typedQuery.setParameter("precoInicial", new BigDecimal("400"));
        typedQuery.setParameter("precoFinal", new BigDecimal("1500"));

        List<Produto> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }

    @Test
    public void usarIsNull() {

        String jpql = "select p from Produto p where p.foto is null";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }

    @Test
    public void usarIsEmpty() {

        String jpql = "select p from Produto p where p.categorias is empty"; //serve para coleções

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> objectList = typedQuery.getResultList();
        Assert.assertFalse(objectList.isEmpty());
    }

    @Test
    public void usarExpressaoCondicionalLike() {
//        String jpql = "select c from Cliente c where c.nome like :nome";
//        String jpql = "select c from Cliente c where c.nome like concat(:nome, '%')";
//        String jpql = "select c from Cliente c where c.nome like concat('%', :nome)";
        String jpql = "select c from Cliente c where c.nome like concat('%', :nome, '%')";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);
//        typedQuery.setParameter("nome", "Fernando%");
//        typedQuery.setParameter("nome", "Medeiros");
        typedQuery.setParameter("nome", "a");

        List<Object[]> resultList = typedQuery.getResultList();
        Assert.assertFalse(resultList.isEmpty());
    }



}
