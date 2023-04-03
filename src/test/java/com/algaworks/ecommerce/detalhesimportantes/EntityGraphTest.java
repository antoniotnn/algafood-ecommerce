package com.algaworks.ecommerce.detalhesimportantes;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Cliente;
import com.algaworks.ecommerce.model.Cliente_;
import com.algaworks.ecommerce.model.Pedido;
import com.algaworks.ecommerce.model.Pedido_;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityGraph;
import javax.persistence.Subgraph;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* para funcionar os exemplos dessa aula tem que ajustar a classe pedido com a implementacao de uma interface comentada la*/
public class EntityGraphTest extends EntityManagerTest {

    @Test
    public void buscarAtributosEssenciaisDePedido01() {

        EntityGraph<Pedido> entityGraph = entityManager.createEntityGraph(Pedido.class);

        //obs (*inconsistencia 1): removi a propriedade "cliente" abaixo, mesmo assim há uma inconsistencia do hibernate e ele traz o cliente, pois ele é owner da relacao,
        //e tbm principalmente pq o atributo tem um JoinColumn indicando a coluna da tabela pedido q tem o cliente, entao ele tras de qualquer maneira na consulta,
        // na verdade em outra consulta separada. Pra resolver isso tem que colocar o fetch Lazy no atributo da classe que nao quer trazer.
        entityGraph.addAttributeNodes("dataCriacao", "status", "total", "notaFiscal"); // "cliente"

        Map<String, Object> properties = new HashMap<>();

        /*
            fetchgraph: traz todos os atributos especificados no entityGraph, IGNORANDO o restante mesmo que eles estejam explicitamente
            configurados como atributos eager. Neste em especifico ocorre um problema. Se tiver um atributo owner da relacao, que tenha um joinColumn,
            ou seja uma referencia praquele objeto de outra tabela, na nossa tabela, e ele nao estiver como LAZY, a consulta acima mesmo NAO especificando a propridade que quer
            buscar , onde no caso ela so buscaria s se for listada, ela vai acabar sendo retornada por essa inconstistencia. Ver acima *inconsistencia numero 1

            loadgraph: traz todos os atributos que foram especificados no entityGraph, e os outros atributos é pra trazer de acordo com as configurações,
            que eles já tem especificados na entidade. Ou seja, vai forçar o EAGER em todos os declarados no entityGraph , e os não especificados, vai respeitar
            o que estiver definido na entidade com as anotações.

            OBS: Para isso funcionar. Tem que usar a propriedade @LazyToONe(LazyToOneOption.NO_PROXY) no atributo
            da classe que for Lazy. Também tem q usar toda a implementação feita na classe OneOneLazyTest (ver compentário lá). É uma implementação
            de interface específica e mais outros detalhes.

         */

        properties.put("javax.persistence.fetchgraph", entityGraph);
//        properties.put("javax.persistence.loadgraph", entityGraph);

        Pedido pedido = entityManager.find(Pedido.class, 1, properties);
        Assert.assertNotNull(pedido);
    }

    @Test
    public void buscarAtributosEssenciaisDePedido02() {

        EntityGraph<Pedido> entityGraph = entityManager.createEntityGraph(Pedido.class);

        entityGraph.addAttributeNodes("dataCriacao", "status", "total", "notaFiscal");

        TypedQuery<Pedido> typedQuery = entityManager
                .createQuery("select p from Pedido p", Pedido.class);

        typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Pedido> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());
    }

    @Test
    public void buscarAtributosEssenciaisDePedido03() {

        EntityGraph<Pedido> entityGraph = entityManager.createEntityGraph(Pedido.class);

        entityGraph.addAttributeNodes("dataCriacao", "status", "total");

        Subgraph<Cliente> subGraphCliente = entityGraph.addSubgraph("cliente", Cliente.class);

        subGraphCliente.addAttributeNodes("nome", "cpf");

        TypedQuery<Pedido> typedQuery = entityManager
                .createQuery("select p from Pedido p", Pedido.class);

        typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Pedido> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());
    }

    @Test
    public void buscarAtributosEssenciaisDePedido04() {

        EntityGraph<Pedido> entityGraph = entityManager.createEntityGraph(Pedido.class);

        entityGraph.addAttributeNodes(Pedido_.dataCriacao, Pedido_.status, Pedido_.total);

        Subgraph<Cliente> subGraphCliente = entityGraph.addSubgraph(Pedido_.cliente);

        subGraphCliente.addAttributeNodes(Cliente_.nome, Cliente_.cpf);

        TypedQuery<Pedido> typedQuery = entityManager
                .createQuery("select p from Pedido p", Pedido.class);

        typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Pedido> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());
    }

    @Test
    public void buscarAtributosEssenciaisComNamedEntityGraph() {

        EntityGraph<?> entityGraph = entityManager.createEntityGraph("Pedido.dadosEssenciais");

        entityGraph.addAttributeNodes("pagamento");


        TypedQuery<Pedido> typedQuery = entityManager
                .createQuery("select p from Pedido p", Pedido.class);

        typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);

        List<Pedido> lista = typedQuery.getResultList();

        Assert.assertFalse(lista.isEmpty());
    }

}
