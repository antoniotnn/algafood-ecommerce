package com.algaworks.ecommerce.multitenant;

import com.algaworks.ecommerce.EntityManagerFactoryTest;
import com.algaworks.ecommerce.hibernate.EcmCurrentTenantIdentifierResolver;
import com.algaworks.ecommerce.model.Produto;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;

public class MultiTenantTest extends EntityManagerFactoryTest {

    /*

        Configurar Multitenant por Schema. ir no persistence.xml e adicionar as configs:

        <!-- Multitenacy  -->
        <!-- por SCHEMA -->
        <property name="hibernate.multiTenancy" value="SCHEMA"/>

        <property name="hibernate.multi_tenant_connection_provider"
                  value="com.algaworks.ecommerce.hibernate.EcmSchemaMultiTenantConnectionProvider"/>

        <property name="hibernate.tenant_identifier_resolver"
                  value="com.algaworks.ecommerce.hibernate.EcmCurrentTenantIdentifierResolver"/>

     */

    @Test
    public void usarAbordagemPorSchema() {

        EcmCurrentTenantIdentifierResolver.setTenantIdentifier("algaworks_ecommerce");
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        Produto produto1 = entityManager1.find(Produto.class, 1);
        Assert.assertEquals("Kindle", produto1.getNome());
        entityManager1.close();

        EcmCurrentTenantIdentifierResolver.setTenantIdentifier("loja_ecommerce");
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();
        Produto produto2 = entityManager2.find(Produto.class, 1);
        Assert.assertEquals("Kindle Paperwhite", produto2.getNome());
        entityManager2.close();
    }

    @Test
    public void usarAbordagemPorMaquina() {

        EcmCurrentTenantIdentifierResolver.setTenantIdentifier("algaworks_ecommerce");
        EntityManager entityManager1 = entityManagerFactory.createEntityManager();
        Produto produto1 = entityManager1.find(Produto.class, 1);
        Assert.assertEquals("Kindle", produto1.getNome());
        entityManager1.close();

        EcmCurrentTenantIdentifierResolver.setTenantIdentifier("loja_ecommerce");
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();
        Produto produto2 = entityManager2.find(Produto.class, 1);
        Assert.assertEquals("Kindle Paperwhite", produto2.getNome());
        entityManager2.close();
    }


}
