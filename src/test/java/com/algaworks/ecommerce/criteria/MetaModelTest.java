package com.algaworks.ecommerce.criteria;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Produto;
import com.algaworks.ecommerce.model.Produto_;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class MetaModelTest extends EntityManagerTest {

    /*
        Para gerar Classes de Meta Model no IntelliJ:
        Clicar em Edid Configurations, adicionar configuração do maven, pode dar o nome
        de: metamodel [clean package]
        na command line colocar; clean package -Dmaven.test.skip=true
        que significa pra dar um clean no pacote e skipar os testes na hora de gerar as classes.

        além disso adicionar no pom.xml o pacote:

        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-jpamodelgen</artifactId>
            <version>5.4.10.Final</version>
            <scope>provided</scope>
        </dependency>
     */

    @Test
    public void utilizarMetaModel() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Produto> criteriaQuery = criteriaBuilder.createQuery(Produto.class);
        Root<Produto> root = criteriaQuery.from(Produto.class);

        criteriaQuery.select(root);

//        criteriaQuery.where(criteriaBuilder.or(
//                criteriaBuilder.like(root.get("nome"), "%C%"),
//                criteriaBuilder.like(root.get("descricao"), "%C")
//        ));

        criteriaQuery.where(criteriaBuilder.or(
                criteriaBuilder.like(root.get(Produto_.nome), "%C%"),
                criteriaBuilder.like(root.get(Produto_.descricao), "%C")
        ));


        TypedQuery<Produto> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Produto> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());
    }

}
