package com.algaworks.ecommerce.cache;

import com.algaworks.ecommerce.model.Pedido;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

public class CacheTest {

    /*
        para configurar a cache de segundo nivel, ir no pom.xml e add essa dependencia:

         // essa dependencia tem recursos de cache para uso em testes, e não profissional
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-testing</artifactId>
            <version>5.4.10.Final</version>
            <scope>test</scope>
        </dependency>

        após isso configurar no persistence.xml :

        // propriedade de cache do hibernate-testing, pode ser configurada em outro local, como a tag shared-cache-mode
            <property name="javax.persistence.sharedCache.mode" value="ALL"/>
     */

    /*

        Ehcache doc: ehcache.org/documentation

        ehcache: Cache profissional. Para configurar, adicionar as dependencias no pom.xml: (nessa ocasiao removemos a dependencia acima do
        hibernate-testing

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-jcache</artifactId>
            <version>5.4.10.Final</version>
        </dependency>

        <dependency>
            <groupId>org.ehcache</groupId>
            <artifactId>ehcache</artifactId>
            <version>3.8.1</version>
        </dependency>

     */

    protected static EntityManagerFactory entityManagerFactory;

    @BeforeClass
    public static void setUpBeforeClass() {
        entityManagerFactory = Persistence.createEntityManagerFactory("Ecommerce-PU");
    }

    @AfterClass
    public static void tearDownAfterClass() {
        entityManagerFactory.close();
    }

    @Test
    public void ehcache() {

        Cache cache = entityManagerFactory.getCache();

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();

        log("Buscando e includindo no cache...");

        entityManager1.createQuery("select p from Pedido p", Pedido.class)
                .getResultList();

        log("---");

        esperar(1);
        Assert.assertTrue(cache.contains(Pedido.class, 2));
        entityManager2.find(Pedido.class, 2);

        esperar(2);
        Assert.assertFalse(cache.contains(Pedido.class, 2));

        entityManager1.close();
        entityManager2.close();
    }

    private static void esperar(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            System.out.println("Erro ocorreu: " + e.getMessage());
        }
    }

    private static void log(Object obj) {
        System.out.println("[LOG " + System.currentTimeMillis() + "] " + obj);
    }

    @Test
    public void controlarCacheDinamicamente() {

        // javax.persistence.cache.retrieveMode CacheRetrieveMode    (ajuda a controlar se quer ou não usar o cache para recueprar os dados na hora de uma pesquisa)
        // javax.persistence.cache.storeMode CacheStoreMode          (quando for fazer pesquisa, se quer ADICIONAR o resultado da consulta no cache ou nao)

        Cache cache = entityManagerFactory.getCache();

        /*
            Modos do storeMode:
            .USE: Colocar o resultado da consulta no cache, e se numa proxima consulta já existir em cache ele nao vai no banco de novo.
            .BYPASS: ignora o retorno e nao vai colocar no cache
            .REFRESH: Toda consulta q for feita ele atualiza e põe no cache
         */

        System.out.println("Buscando todos os pedidos..........................");
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();

        entityManager1.setProperty("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS); //assim todas as consultas dessa instancia do entity manager vao usar essa propridade.
        entityManager1.createQuery("select p from Pedido p", Pedido.class)

                .setHint("javax.persistence.cache.storeMode", CacheStoreMode.USE)
                .getResultList();

        System.out.println("Buscando o pedido de ID igual a 2..................");
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();
        Map<String, Object> propriedades = new HashMap<>();
//        propriedades.put("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS); //modo de armazenar no cache, ignorando e não armazenando nada
//        propriedades.put("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS); //modo de recuperar as informacoes, do cache, ignorando o cache existente
        entityManager2.find(Pedido.class, 2, propriedades);

        System.out.println("Buscando todos os pedidos (denovo) ..........................");
        EntityManager entityManager3 = entityManagerFactory.createEntityManager();
        entityManager3.createQuery("select p from Pedido p", Pedido.class)
//                .setHint("javax.persistence.cache.retrieveMode", CacheStoreMode.BYPASS)
                .getResultList();

        entityManager1.close();
        entityManager2.close();
        entityManager3.close();
    }

    @Test
    public void analisarOpcoesCache() {

        Cache cache = entityManagerFactory.getCache();

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();

        System.out.println("Buscando a partir da instância 1:");
        entityManager1.createQuery("select p from Pedido p", Pedido.class)
                .getResultList();

        Assert.assertTrue(cache.contains(Pedido.class, 1));

        entityManager1.close();
    }

    @Test
    public void verificarSeEstaNoCache() {

        Cache cache = entityManagerFactory.getCache();

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();

        System.out.println("Buscando a partir da instância 1:");
        entityManager1.createQuery("select p from Pedido p", Pedido.class)
                .getResultList();

        Assert.assertTrue(cache.contains(Pedido.class, 1));
        Assert.assertTrue(cache.contains(Pedido.class, 2));

        entityManager1.close();
    }

    @Test
    public void removerDoCache() {

        Cache cache = entityManagerFactory.getCache();

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();

        System.out.println("Buscando a partir da instância 1:");
        entityManager1.createQuery("select p from Pedido p", Pedido.class)
                .getResultList();

        System.out.println("Removendo o pedido 1 do cache: ");
        cache.evict(Pedido.class, 1);
//        cache.evict(Pedido.class); //esse remove todas as entidades Pedido do cache
//        cache.evictAll(); //remove todas as entidades do cache.


        System.out.println("Buscando a partir da instância 2:");
        entityManager2.find(Pedido.class, 1);
        entityManager2.find(Pedido.class, 2);

        entityManager1.close();
        entityManager2.close();
    }

    @Test
    public void adicionarPedidosNoCache() {

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();

        System.out.println("Buscando a partir da instância 1:");
        entityManager1.createQuery("select p from Pedido p", Pedido.class)
                .getResultList(); // o jpa nao faz cache da consulta sql em si, e sim dos objetos retornados ( as entidades )

        System.out.println("Buscando a partir da instância 2:");
        entityManager2.find(Pedido.class, 1);

        entityManager1.close();
        entityManager2.close();
    }

    @Test
    public void buscarDoCache() {

        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();

        System.out.println("Buscando a partir da instância 1:");
        entityManager1.find(Pedido.class, 1);

//        System.out.println("Buscando a partir da instância 1:");
//        entityManager1.find(Pedido.class, 1);

        System.out.println("Buscando a partir da instância 2:");
        entityManager2.find(Pedido.class, 1);

        entityManager1.close();
        entityManager2.close();
    }
}
