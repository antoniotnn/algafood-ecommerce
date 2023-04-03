package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Pedido;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class OperadoresLogicosTest extends EntityManagerTest {


    @Test
    public void usarOperadores() {

        String jpql = "select p from Pedido p " +
                "where (p.total > 500 and p.status = 'AGUARDANDO' or p.status = 'PAGO') " +
                "and p.total > 100";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

}
