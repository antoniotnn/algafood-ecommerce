package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Cliente;
import com.algaworks.ecommerce.model.Pedido;
import com.algaworks.ecommerce.model.Produto;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class SubqueriesTest extends EntityManagerTest {

    @Test
    public void pesquisarComAllExercicio() {

        //distinct, from ItemPedido, join produto , retorno é produto
        //Todos os produtos que sempre foram vendidos pelo mesmo preco
        String jpql = "select distinct p from ItemPedido ip " +
                "join ip.produto p where " +
                "ip.precoProduto = all(select precoProduto from ItemPedido where produto = p and id <> ip.id)";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComAny() {

        //Todos os produtos que já foram vendidos por um preço diferente do atual
        String jpql = "select p from Produto p where " +
                "p.preco <> any(select precoProduto from ItemPedido where produto = p)";
                //any pode ser substituido por some

        //Todos os produtos que já foram vendidos pelo menos uma vez pelo preço atual
//        String jpql = "select p from Produto p where " +
//                "p.preco = any(select precoProduto from ItemPedido where produto = p)";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComAll() {

        //Todos os produtos que não foram vendidos mais depois que encareceram
        String jpql = "select p from Produto p where " +
                "p.preco > all(select precoProduto from ItemPedido where produto = p)";
                /* outra forma de fazer
                "p.preco > (select max(precoProduto) from ItemPedido where produto = p)"; */



        //Todos os produtos que sempre foram vendidos pelo preço atual.
//        String jpql = "select p from Produto p where " +
//                "p.preco = all(select precoProduto from ItemPedido where produto = p)";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    //Retornar todos os produtos que não foram vendidos ainda com o preço atual.
    @Test
    public void pesquisarComExistsExercicio() {

        //Como eu fiz
        String jpql = "select p from Produto p where exists " +
                "(select 1 from ItemPedido ip2 join ip2.produto p2 where p2 = p and p2.preco <> ip2.precoProduto) ";

        //Como o professor fez.
//        String jpql = "select p from Produto p " +
//                " where exists " +
//                " (select 1 from ItemPedido where produto = p and precoProduto <> p.preco)";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    //Trazer todos os Clientes que fizeram pelo menos 2 pedidos
    @Test
    public void pesquisarComSubQueryExercicio() {

        //Como eu fiz.
        String jpql = "select c from Cliente c " +
                "where 2 <= (select count(p.id) from c.pedidos p)";

        //Como o professor fez:
//        String jpql = "select c from Cliente c where " +
//                " (select count(cliente) from Pedido where cliente = c) >= 2";

        TypedQuery<Cliente> typedQuery = entityManager.createQuery(jpql, Cliente.class);

        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Preço: " + obj.getNome()));
    }

    //Trazer todos os pedidos que contenham algum produto da categoria de identificador 2
    @Test
    public void pesquisarComINExercicio() {

        String jpql = "select p from Pedido p " +
                "where p.id in " +
                "(select p2.id from ItemPedido i2 join i2.pedido p2 " +
                "join i2.produto pro2 " +
                "join pro2.categorias cat2 " +
                "where cat2.id = 2)";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComExists() {

        String jpql = "select p from Produto p where exists " +
                "(select 1 from ItemPedido ip2 join ip2.produto p2 where p2 = p) ";

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql, Produto.class);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComIN() {

        String jpql = "select p from Pedido p " +
                "where p.id in " +
                "(select p2.id from ItemPedido i2 join i2.pedido p2 join i2.produto pro2 where pro2.preco > 100)";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarSubQueries() {

//        Bons clientes. Versão 2.
        String jpql = "select c from Cliente c " +
                "where 500 < (select sum(p.total) from Pedido p where p.cliente = c)";

//        Bons clientes. Versão 1.
//        String jpql = "select c from Cliente c " +
//                "where 500 < (select sum(p.total) from c.pedidos p)";

//        Todos os pedidos acima da média de vendas.
//        String jpql = "select p from Pedido p " +
//                " where p.total > (select avg(total) from Pedido)";

//        O produto ou os produtos mais caros da base.
//        String jpql = "select p from Produto p " +
//                " where p.preco = (select max(preco) from Produto)";

        TypedQuery<Cliente> typedQuery = entityManager.createQuery(jpql, Cliente.class);

        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Preço: " + obj.getNome()));
    }

}
