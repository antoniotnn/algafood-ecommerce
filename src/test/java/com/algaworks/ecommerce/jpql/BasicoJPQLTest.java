package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.dto.ProdutoDTO;
import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Cliente;
import com.algaworks.ecommerce.model.Pedido;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class BasicoJPQLTest extends EntityManagerTest {
    // Java Persistence Query Language - JPQL

    //  JPQL - select p from Pedido p where p.id = 1

    //  JPQL - select p from Pedido p join p.itens where i.precoProduto > 10

    // SQL - select p.* from pedido where p.id = 1;

    // SQL - select p.* from pedido join item_pedido i on i.pedido_id = p.id where i.preco_produto > 10;

    // entityManager.find(Pedido.class, 1);

    @Test
    public void usarDistinct() {

        String jpql = "select distinct p from Pedido p " +
                " join p.itens i join i.produto pro" +
                " where pro.id in (1, 2, 3, 4) ";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        System.out.println(list.size());
    }

    @Test
    public void ordenarResultados() {

        String jpql = "select c from Cliente c order by c.nome asc"; //desc

        TypedQuery<Cliente> typedQuery = entityManager.createQuery(jpql, Cliente.class);

        List<Cliente> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(c -> System.out.println(c.getId() + ", " + c.getNome()));
    }

    @Test
    public void projetarNoDTO() {
        String jpql = "select new com.algaworks.ecommerce.dto.ProdutoDTO(id, nome) from Produto";

        TypedQuery<ProdutoDTO> typedQuery = entityManager.createQuery(jpql, ProdutoDTO.class);

        List<ProdutoDTO> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());

        lista.forEach(produtoDTO -> System.out.println(produtoDTO.getId() + ", " + produtoDTO.getNome()));
    }

    @Test
    public void projetarOResultado() {
        String jpql = "select id, nome from Produto";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> lista = typedQuery.getResultList();

        Assert.assertTrue(lista.get(0).length == 2);

        lista.forEach(array -> System.out.println(array[0] + ", " + array[1]));

    }

    @Test
    public void selecionarUmAtributoParaRetorno() {
        String jpql = "select p.nome from Produto p";

        TypedQuery<String> typedQuery = entityManager.createQuery(jpql, String.class);
        List<String> produtoList = typedQuery.getResultList();
        Assert.assertTrue(String.class.equals(produtoList.get(0).getClass()));

        String jpqlCliente = "select p.cliente from Pedido p";
        TypedQuery<Cliente> clienteTypedQuery = entityManager.createQuery(jpqlCliente, Cliente.class);
        List<Cliente> clienteList = clienteTypedQuery.getResultList();
        Assert.assertTrue(Cliente.class.equals(clienteList.get(0).getClass()));
    }

    @Test
    public void buscarPorIdentificador() {
        TypedQuery<Pedido> typedQuery = entityManager
                .createQuery("select p from Pedido p where p.id = 1", Pedido.class); // traz pedido e seus agregados

        // select p.* from pedido p where p.id = 1;  // tras so os atributos de pedido

        Pedido pedido = typedQuery.getSingleResult();
        Assert.assertNotNull(pedido);


//        List<Pedido> lista = typedQuery.getResultList();
//        Assert.assertFalse(lista.isEmpty());
    }

    @Test
    public void mostrarDiferencaQueries() {
        String jpql = "select p from Pedido p where p.id = 1";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);
        Pedido pedido1 = typedQuery.getSingleResult();
        Assert.assertNotNull(pedido1);

        Query query = entityManager.createQuery(jpql);
        Pedido pedido2 = (Pedido) query.getSingleResult();
        Assert.assertNotNull(pedido2);

//        List<Pedido> pedidoList = query.getResultList();
//        Assert.assertFalse(pedidoList.isEmpty());
    }





}
