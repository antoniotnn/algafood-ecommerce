package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class GroupByTest extends EntityManagerTest {

    @Test
    public void condicionarAgrupamentoComHaving() {

        // Total de vendas dentre as categorias que mais vendem
        String jpql = "select cat.nome, sum(ip.precoProduto) from ItemPedido ip " +
                " join ip.produto pro join pro.categorias cat " +
                " group by cat.id " +
                "having sum(ip.precoProduto) > 100";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));
    }


    @Test
    public void agruparEFiltrarResultado() {
//         Total de vendas por mês.
//        String jpql = "select concat(year(p.dataCriacao), '/', function('monthname', p.dataCriacao)), sum(p.total) " +
//                " from Pedido p " +
//                "where year(p.dataCriacao) = year(current_date) " +
//                " group by year(p.dataCriacao), month(p.dataCriacao)";

        // Total de vendas por categoria.
//        String jpql = "select c.nome, sum(ip.precoProduto) from ItemPedido ip " +
//                " join ip.produto pro join pro.categorias c join ip.pedido p " +
//                "where year(p.dataCriacao) = year(current_date) and month(p.dataCriacao) = month(current_date) " +
//                " group by c.id";

        // Total de vendas por cliente.
        String jpql = "select c.nome, sum(i.precoProduto) from ItemPedido i " +
                "join i.pedido ped " +
                "join ped.cliente c " +
                "where year(ped.dataCriacao) = year(current_date) " +
                "and month(ped.dataCriacao) >= (month(current_date - 3)) " +
                "group by c.id ";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
//        Assert.assertTrue(Boolean.TRUE);

        list.forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));
    }

    @Test
    public void agruparResultado() {
        // Quantidade de  produtos por categoria.
        //String jpql = "select c.nome, count(p.id) from Categoria c join c.produtos p group by c.id";

        // Total de vendas por mês.
//        String jpql = "select concat(year(p.dataCriacao), '/', function('monthname', p.dataCriacao)), sum(p.total) " +
//                " from Pedido p " +
//                " group by year(p.dataCriacao), month(p.dataCriacao)";



        // Total de vendas por categoria.
//        String jpql = "select c.nome, sum(ip.precoProduto) from ItemPedido ip " +
//                " join ip.produto pro join pro.categorias c " +
//                " group by c.id";

        // Total de vendas por cliente.
//        String jpql = "select c.nome, sum(i.precoProduto) from ItemPedido i " +
//                "join i.pedido ped " +
//                "join ped.cliente c " +
//                "group by c.id ";

        // Total de vendas por dia e por categoria.
        String jpql = "select " +
                " concat(year(ped.dataCriacao), '/', month(ped.dataCriacao), '/', day(ped.dataCriacao)), " +
                " concat(cat.nome, ': ', sum(i.precoProduto) ) " +
                " from ItemPedido i " +
                "join i.pedido ped " +
                "join i.produto prod " +
                "join prod.categorias cat " +
                "group by year(ped.dataCriacao), month(ped.dataCriacao), day(ped.dataCriacao), cat.id " +
                "order by ped.dataCriacao, cat.nome";


        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + ", " + arr[1]));
    }
}
