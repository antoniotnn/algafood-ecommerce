package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.NotaFiscal;
import com.algaworks.ecommerce.model.Pedido;
import com.algaworks.ecommerce.model.StatusPagamento;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

public class PassandoParametrosTest extends EntityManagerTest {

    @Test
    public void passarParametroDate() {
        String jpql = "select nf from NotaFiscal nf where nf.dataEmissao <= ?1 ";

        TypedQuery<NotaFiscal> typedQuery = entityManager.createQuery(jpql, NotaFiscal.class);
        typedQuery.setParameter(1, new Date(), TemporalType.TIMESTAMP);

        List<NotaFiscal> notaFiscalList = typedQuery.getResultList();
        Assert.assertTrue(notaFiscalList.size() == 1);
    }

    @Test
    public void passarParametro() {
//        String jpql = "select p from Pedido p  join p.pagamento pag " +
//                "   where p.id = ?1 and pag.status = ?2";

//        String jpql = "select p from Pedido p  join p.pagamento pag " +
//                "   where p.id = :pedidoId and pag.status = :pagamentoStatus";

        String jpql = "select p from Pedido p  join p.pagamento pag " +
                "   where p.id = :pedidoId and pag.status = ?1";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

//        typedQuery.setParameter(1, 2);
//        typedQuery.setParameter(2, StatusPagamento.PROCESSANDO);

        typedQuery.setParameter("pedidoId", 2);
//        typedQuery.setParameter("pagamentoStatus", StatusPagamento.PROCESSANDO);
        typedQuery.setParameter(1, StatusPagamento.PROCESSANDO);

        List<Pedido> pedidoList = typedQuery.getResultList();
        Assert.assertTrue(pedidoList.size() == 1);
    }

}
