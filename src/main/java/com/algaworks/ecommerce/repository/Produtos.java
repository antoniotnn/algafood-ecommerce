/*
    Para multitenacy SEM SER POR COLUNA, Descomentar os atributos tenacy e remover este atributo em todas as chamadas e SQLS.
 */

package com.algaworks.ecommerce.repository;

import com.algaworks.ecommerce.model.Produto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class Produtos {

    @PersistenceContext
    private EntityManager entityManager;

    public Produto buscar(Integer id/*, String tenant*/) {
        return entityManager
                .createQuery("select p from Produto p where p.id =:id", Produto.class) //and p.tenant = :tenant
                .setParameter("id", id)
//                .setParameter("tenant", tenant)
                .getSingleResult();
    }

    public Produto salvar(Produto produto) {
        return entityManager.merge(produto);
    }

    public List<Produto> listar(/*String tenant*/) {
        return entityManager
                .createQuery("select p from Produto p", Produto.class) //where p.tenant = :tenant
//                .setParameter("tenant", tenant)
                .getResultList();
    }
}