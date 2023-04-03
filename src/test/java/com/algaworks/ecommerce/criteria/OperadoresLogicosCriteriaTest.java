package com.algaworks.ecommerce.criteria;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Pedido;
import com.algaworks.ecommerce.model.Pedido_;
import com.algaworks.ecommerce.model.StatusPedido;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.List;

public class OperadoresLogicosCriteriaTest extends EntityManagerTest {

    @Test
    public void usarOperadores() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Pedido> criteriaQuery = criteriaBuilder.createQuery(Pedido.class);
        Root<Pedido> root = criteriaQuery.from(Pedido.class);

        criteriaQuery.select(root);

        /*
        //select p from Pedido p where (total > 499 and status = 'PAGO') and dataCriacao > ?
        criteriaQuery.where(
            criteriaBuilder.and( //utilizado pra agrupar as condicoes num parêntese)
                criteriaBuilder.greaterThan(
                    root.get(Pedido_.total), new BigDecimal("499")),
                criteriaBuilder.equal(
                    root.get(Pedido_.status), StatusPedido.PAGO)
            ),
            criteriaBuilder.greaterThan(
                root.get(Pedido_.dataCriacao), LocalDateTime.now().minusDays(5))
        );
        */


        //select p from Pedido p where (status = 'AGUARDANDO' or status = 'PAGO') and total > ?
        criteriaQuery.where(
            criteriaBuilder.or( //utilizado pra agrupar as condicoes num parêntese)
                criteriaBuilder.equal(
                    root.get(Pedido_.status), StatusPedido.AGUARDANDO),
                criteriaBuilder.equal(
                    root.get(Pedido_.status), StatusPedido.PAGO)
            ),
            criteriaBuilder.greaterThan(
                root.get(Pedido_.total), new BigDecimal("499"))
        );



        TypedQuery<Pedido> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Pedido> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(pedido -> System.out.println(
                "ID: " + pedido.getId() + ", Total: " + pedido.getTotal()));
    }

}
