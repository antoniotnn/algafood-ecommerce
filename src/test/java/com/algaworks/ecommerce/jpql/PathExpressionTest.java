package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Pedido;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class PathExpressionTest extends EntityManagerTest {

    @Test
    public void buscarPedidosComProdutoEspecifico() {
//        String jpql = "select p from Pedido p join p.itens i join i.produto prod where prod.id = 1";
        String jpql = "select p from Pedido p join fetch p.itens i join fetch i.produto prod where prod.id = 1";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);
        List<Pedido> pedidoList = typedQuery.getResultList();

        Assert.assertFalse(pedidoList.isEmpty());
    }

    @Test
    public void usarPathExpressions() { // path expression: p.cliente.nome
//        String jpql = "select p from Pedido p where p.cliente.nome = 'NOME QUALQUER'"; // Dessa forma usa o join mais sensato (definido pelo jpa) que é o join normal
//        String jpql = "select p from Pedido p join p.cliente c where c.nome = 'NOME QUALQUER'"; // Para fazer a mesma consulta acima garantindo q seja com inner join
        String jpql = "select p.cliente.nome from Pedido p"; // Para fazer a mesma consulta acima garantindo q seja com inner join

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());
    }

}
