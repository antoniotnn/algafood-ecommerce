package com.algaworks.ecommerce.jpql;


import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Pedido;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class JoinTest extends EntityManagerTest {

    @Test
    public void usarJoinFetch() { // pra carregar tudo num select so e nao em vários. Serve pra resolver o N+1

        // join fetch : para trazer os itens (coleção , que geralmente por padrão é lazy) juntos em um select só.
//        String jpql = "select p from Pedido p join fetch p.itens where p.id = 1";

        /*
            trás todos os pedidos com pagamento relacionado (join) (e também sem pagamento (left join) (fetch pra carregar os dados desse pagamento junto)
            e também com cliente relacionado (join), carregando os seus dados (fetch), com nota fiscal relacionada (join), porém sem nota também ( left join),
            carregando os seus dados da nota fiscal (fetch)
         */
        String jpql = "select p from Pedido p left join fetch p.pagamento join fetch p.cliente left join fetch p.notaFiscal";

        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

//        Pedido pedido = typedQuery.getSingleResult();

//        Pedido pedido = typedQuery.getSingleResult();

        List<Pedido> pedidoList = typedQuery.getResultList();

//        Assert.assertFalse(pedido.getItens().isEmpty());
        Assert.assertFalse(pedidoList.isEmpty());
    }

    @Test
    public void fazerLeftJoin() {
        // left join ou left outer join ( no jpql)
//        String jpql = "select p from Pedido p left join p.pagamento pag"; // trás todos os Pedidos que tenham pagamento associado, e em seguida trás todos os pedidos mesmo se não tiver pagamento

        //String jpql = "select pag from Pagamento pag left join pag.pedido p"; // trás os pagamentos que tenham pedidos associados, depois trás todos os pagamentos.

        //Usar o where (ao invés de on) em left join no jpql (exemplo abaixo):
        /*
            trás todos os pedidos que tenham pagamentos associados, e o status seja PROCESSANDO, (vai limitar a esse filtro de processando APENAS).
            Neste caso pelo left join normal, traria também os pedidos sem pagamento associado (na sequencia, apos o primeiro criterio), porém,
            o where LIMITA e não trás.
            Porém utilizando a clausula ON, ele trás primeiro os pedidos com pagamento associado e com status processando, e depois todos os pedidos
            SEM pagamento associado, ignorando até a clausula do ON.
         */
        String jpql = "select p from Pedido p left join p.pagamento pag on pag.status = 'PROCESSANDO'";


        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> pedidoList = typedQuery.getResultList();

//        Assert.assertTrue(pedidoList.size() == 2);
        Assert.assertFalse(pedidoList.isEmpty());
    }


    @Test
    public void fazerJoin() {

        // esse comando faz um inner join
        String jpql = "select p from Pedido p join p.pagamento pag"; // traz pedidos que tenham pagamento. (Size = 1) , sem join o teste falha
//        String jpql = "select p from Pedido p"; // traz todos os pedidos, com pagamento ou não. (Size = 2 ou ??) , sem join o teste falha

//        String jpql = "select p, pag from Pedido p join p.pagamento pag where pag.status = 'PROCESSANDO'"; // projeção, retorna Object[]
//        String jpql = "select p, i from Pedido p join p.itens i"; // projeção, retorna Object[]

//        String jpql = "select p from Pedido p join p.itens i join i.produto prod where prod.id = 1"; // projeção, retorna Object[]

//        TypedQuery<Pedido> typedQuery = entityManager.createQuery(jpql, Pedido.class);

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

//        List<Pedido> pedidoList = typedQuery.getResultList();

        List<Object[]> pedidoList = typedQuery.getResultList();

        Assert.assertFalse(pedidoList.isEmpty());
//        Assert.assertTrue(pedidoList.size() == 3); // projeção trazendo os itens, linha 20
    }


}
