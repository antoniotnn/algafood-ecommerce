package com.algaworks.ecommerce.criteria;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.*;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.List;

public class SubqueriesCriteriaTest extends EntityManagerTest {

    @Test
    public void pesquisarComAllExercicio() {
        //Todos os produtos que sempre foram vendidos pelo mesmo preco
//        String jpql = "select distinct p from ItemPedido ip " +
//                "join ip.produto p where " +
//                "ip.precoProduto = all(select precoProduto from ItemPedido where produto = p and id <> ip.id)";
        // vai retornar  o id 3 e 4

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<ItemPedido> root = criteriaQuery.from(ItemPedido.class);
        Join<ItemPedido, Produto> joinItemPedidoProduto = root.join(ItemPedido_.produto);

        criteriaQuery.select(joinItemPedidoProduto);
        criteriaQuery.distinct(true);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
        subquery.where(
            criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), joinItemPedidoProduto),
            criteriaBuilder.notEqual(subqueryRoot.get(ItemPedido_.id), root.get(ItemPedido_.id))
        );

        criteriaQuery.where(
            criteriaBuilder.equal(
                root.get(ItemPedido_.precoProduto),
                criteriaBuilder.all(subquery)
            )
        );

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));

        /*
            Professor resolveu a questão dessa forma:

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
            Root<ItemPedido> root = criteriaQuery.from(ItemPedido.class);

            criteriaQuery.select(root.get(ItemPedido_.produto));
            criteriaQuery.distinct(true);

            Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
            Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);
            subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
            subquery.where(
                    criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root.get(ItemPedido_.produto)),
                    criteriaBuilder.notEqual(subqueryRoot, root)
            );

            criteriaQuery.where(
                    criteriaBuilder.equal(
                            root.get(ItemPedido_.precoProduto), criteriaBuilder.all(subquery))
            );

            TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

            List<Produto> lista = typedQuery.getResultList();
            Assert.assertFalse(lista.isEmpty());

            lista.forEach(obj -> System.out.println("ID: " + obj.getId()));
         */
    }

    @Test
    public void pesquisarComAny02() {
        //Todos os produtos que já foram vendidos por um preço diferente do atual
//        String jpql = "select p from Produto p where " +
//                "p.preco <> any(select precoProduto from ItemPedido where produto = p)";
        //any pode ser substituido por some, no jpql

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root));

        criteriaQuery.where(
            criteriaBuilder.notEqual(
                root.get(Produto_.preco),
                criteriaBuilder.any(subquery)
            )
        );

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComAny01() {
        //Todos os produtos que já foram vendidos pelo menos uma vez pelo preço atual
//        String jpql = "select p from Produto p where " +
//                "p.preco = any(select precoProduto from ItemPedido where produto = p)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root));

        criteriaQuery.where(
            criteriaBuilder.equal(
                    root.get(Produto_.preco),
                    criteriaBuilder.any(subquery)
            )
        );

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComAll02() {
        //Todos os produtos que não foram vendidos mais depois que encareceram, e que foram vendidos pelo menos uma vez.
//        String jpql = "select p from Produto p where " +
//                "p.preco > all(select precoProduto from ItemPedido where produto = p) ";
//                "and exists (select 1 from ItemPedido where produto = p)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root));

        criteriaQuery.where(
            criteriaBuilder.greaterThan(
                root.get(Produto_.preco),
                criteriaBuilder.all(subquery)
            ),
            criteriaBuilder.exists(subquery)
        );

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComAll01() {
        //Todos os produtos que SEMPRE foram vendidos pelo preço atual.
//        String jpql = "select p from Produto p where " +
//                "p.preco = all(select precoProduto from ItemPedido where produto = p)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(subqueryRoot.get(ItemPedido_.precoProduto));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root));

        criteriaQuery.where(criteriaBuilder.equal(
                root.get(Produto_.preco), criteriaBuilder.all(subquery)));

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComExistsExercicio() {
        // Retornar todos os produtos que já foram vendidos por um preço diferente do preço atual
        // (produtos onde item pedido (preco) seja diferente do preco na tabela (produto)

        // OBS: Não consegui executar o exercício. Resolução abaixo.

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);
        subquery.select(criteriaBuilder.literal(1));

        subquery.where(
            criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root),
            criteriaBuilder.notEqual(subqueryRoot.get(ItemPedido_.precoProduto), root.get(Produto_.preco))
        );

        criteriaQuery.where(criteriaBuilder.exists(subquery));

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComINExercicio() {
        // Retornar todos os Pedidos que tem algum produto da Categoria de ID 2;

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        Join<Pedido, ItemPedido> joinItens = root.join(Pedido_.itens);
        Join<ItemPedido, Produto> joinItensProduto = joinItens.join(ItemPedido_.produto);

        criteriaQuery.select(root);

        Subquery<Produto> subquery = criteriaQuery.subquery(Produto.class);
        Root<Produto> subqueryRoot = subquery.from(Produto.class);
        Join<Produto, Categoria> subqueryJoinProdutoCategoria = subqueryRoot.join(Produto_.categorias);

        subquery.select(subqueryRoot);
        subquery.where(criteriaBuilder.equal(subqueryJoinProdutoCategoria.get(Categoria_.id), 2));

        criteriaQuery.where(joinItensProduto.get(Categoria_.id).in(subquery));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Nome:" + obj.getCliente().getNome()));

        /*
            Professor fez da forma abaixo:

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
            Root<Pedido> root = criteriaQuery.from(Pedido.class);

            criteriaQuery.select(root);

            Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
            Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);
            Join<ItemPedido, Produto> subqueryJoinProduto = subqueryRoot.join(ItemPedido_.produto);
            Join<Produto, Categoria> subqueryJoinProdutoCategoria = subqueryJoinProduto
                    .join(Produto_.categorias);
            subquery.select(subqueryRoot.get(ItemPedido_.id).get(ItemPedidoId_.pedidoId));
            subquery.where(criteriaBuilder.equal(subqueryJoinProdutoCategoria.get(Categoria_.id), 2));

            criteriaQuery.where(root.get(Pedido_.id).in(subquery));

            TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

            List<Pedido> lista = typedQuery.getResultList();
            Assert.assertFalse(lista.isEmpty());

            lista.forEach(obj -> System.out.println("ID: " + obj.getId()));
         */
    }

    @Test
    public void pesquisarComSubqueryExercicio() {
        //Pesquisar todos os clientes que já fizeram mais que 2 pedidos.

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteriaQuery = criteriaBuilder.createQuery(Cliente.class);
        Root<Cliente> root = criteriaQuery.from(Cliente.class);

        criteriaQuery.select(root);

        Subquery<Long> subquery = criteriaQuery.subquery(Long.class);
        Root<Pedido> subqueryRoot = subquery.from(Pedido.class);

        subquery.select(criteriaBuilder.count(criteriaBuilder.literal(1)));
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(Pedido_.cliente), root));

        criteriaQuery.where(criteriaBuilder.greaterThan(subquery, 2L));


        TypedQuery<Cliente> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Nome:" + obj.getNome()));

        /*
            Professor fez da forma abaixo:

            CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
            Root<Pedido> root = criteriaQuery.from(Pedido.class);

            criteriaQuery.select(root);

            Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
            Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);
            Join<ItemPedido, Produto> subqueryJoinProduto = subqueryRoot.join(ItemPedido_.produto);
            Join<Produto, Categoria> subqueryJoinProdutoCategoria = subqueryJoinProduto
                    .join(Produto_.categorias);
            subquery.select(subqueryRoot.get(ItemPedido_.id).get(ItemPedidoId_.pedidoId));
            subquery.where(criteriaBuilder.equal(subqueryJoinProdutoCategoria.get(Categoria_.id), 2));

            criteriaQuery.where(root.get(Pedido_.id).in(subquery));

            TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

            List<Pedido> lista = typedQuery.getResultList();
            Assert.assertFalse(lista.isEmpty());

            lista.forEach(obj -> System.out.println("ID: " + obj.getId()));
         */

    }

    @Test
    public void pesquisarComExists() {
//        Todos os produtos que já foram vendidos.
//        String jpql = "select p from Produto p where exists " +
//                "(select 1 from ItemPedido ip2 join ip2.produto p2 where p2 = p) ";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        subquery.select(criteriaBuilder.literal(1));
//        Join<ItemPedido, Produto> subqueryJoinItemPedidoProduto = subqueryRoot.join(ItemPedido_.produto);
        subquery.where(criteriaBuilder.equal(subqueryRoot.get(ItemPedido_.produto), root));

        criteriaQuery.where(criteriaBuilder.exists(subquery));

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarComIN() {
//        String jpql = "select p from Pedido p " +
//                "where p.id in " +
//                "(select p2.id from ItemPedido i2 join i2.pedido p2 join i2.produto pro2 where pro2.preco > 100)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        Subquery<Integer> subquery = criteriaQuery.subquery(Integer.class);
        Root<ItemPedido> subqueryRoot = subquery.from(ItemPedido.class);

        Join<ItemPedido, Pedido> subqueryJoinPedido = subqueryRoot.join(ItemPedido_.pedido);
        Join<ItemPedido, Produto> subqueryJoinProduto = subqueryRoot.join(ItemPedido_.produto);

        subquery.select(subqueryJoinPedido.get(Pedido_.id));

        subquery.where(criteriaBuilder.greaterThan(
                subqueryJoinProduto.get(Produto_.preco), new BigDecimal("100")));

        criteriaQuery.where(root.get(Pedido_.id).in(subquery));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId()));
    }

    @Test
    public void pesquisarSubQueries03() {
//        Bons clientes.
//        String jpql = "select c from Cliente c " +
//                "where 1300 < (select sum(p.total) from Pedido p where p.cliente = c)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Cliente> criteriaQuery = criteriaBuilder.createQuery(Cliente.class);
        Root<Cliente> root = criteriaQuery.from(Cliente.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<Pedido> subqueryRoot = subquery.from(Pedido.class);

        subquery.select(criteriaBuilder.sum(subqueryRoot.get(Pedido_.total)));

        subquery.where(criteriaBuilder.equal(root, subqueryRoot.get(Pedido_.cliente)));
//        subquery.where(criteriaBuilder.equal(
//                root.get(Cliente_.id), subqueryRoot.get(Pedido_.cliente).get(Cliente_.id)));

        criteriaQuery.where(criteriaBuilder.greaterThan(subquery, new BigDecimal("1300")));

        TypedQuery<Cliente> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Nome: " + obj.getNome()));
    }

    @Test
    public void pesquisarSubQueries02() {
//        Todos os pedidos acima da média de vendas.
//        String jpql = "select p from Pedido p " +
//                " where p.total > (select avg(total) from Pedido)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<Pedido> subqueryRoot = subquery.from(Pedido.class);

        subquery.select(criteriaBuilder.avg(subqueryRoot.get(Pedido_.total)).as(BigDecimal.class));

        criteriaQuery.where(criteriaBuilder.greaterThan(root.get(Pedido_.total), subquery));

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getId() + ", Total: " + obj.getTotal()));
    }

    @Test
    public void pesquisarSubQueries01() {

//        O produto ou os produtos mais caros da base.
//        String jpql = "select p from Produto p " +
//                " where p.preco = (select max(preco) from Produto)";

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

        Subquery<BigDecimal> subquery = criteriaQuery.subquery(BigDecimal.class);
        Root<Produto> subqueryRoot = subquery.from(Produto.class);

        subquery.select(criteriaBuilder.max(subqueryRoot.get(Produto_.preco)));

        criteriaQuery.where(criteriaBuilder.equal(root.get(Produto_.preco), subquery));

        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);

        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println("ID: " + obj.getNome() + ", Preço: " + obj.getPreco()));
    }

}
