package com.algaworks.ecommerce.detalhesimportantes;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Pedido;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/*
    O Problema do @OneToOne com o lazy no Hibernate
 */


public class OneOneLazyTest extends EntityManagerTest {

    /*
        Resolver problema do N+1 no OneToOne com Lazy em non-owner.

        para resolver o problema mostrado no método de teste abaixo ( mostrarProblema()), devemos fazer a entidade implementar
        a interface PersistentAttributeInterceptable e sobrescrever 2 métodos. (ver exemplo realizado na entidade Pedido, onde alem de implementar os métodos,
        tem um atributo d mesmo tipo na classe e anotacoes @LazyToOne nos atributos que queremos que o lazy seja respeitado.
     */
    @Test
    public void mostrarProblema() {

        //essa consulta individual de PEDIDO, dá OK. Ele é EAGER, tras uma consulta só.
        System.out.println("BUSCANDO UM PEDIDO: ");
        Pedido pedido = entityManager.find(Pedido.class, 1);
        Assert.assertNotNull(pedido);

        System.out.println("------------------------------------------------------------");

        /*
            Já nessa consulta, ocorre o problema, pois não é LAZY, ele faz VÁRIAS CONSULTAS ocorrendo o N+1.
            No @OneToOne ocorre um problema no hibernate ao utilizar o LAZY. Em atributos da classe (Pedido), que não
            são o OWNER, como por exemplo, notaFiscal, e pagamento (o owner é o pedido), se colocarmos o lazy lá nos atributos
            usando o fetch = FetchType.LAZY , não irá funcionar (incrivelmente o IntelliJ mostra isso).
         */
        System.out.println("BUSCANDO UMA LISTA DE PEDIDOS:");
        List<Pedido> lista = entityManager
                .createQuery("select p from Pedido p", Pedido.class)
//                .createQuery("select p from Pedido p join fetch p.notaFiscal", Pedido.class)
                .getResultList();
        Assert.assertFalse(lista.isEmpty());
    }
}
