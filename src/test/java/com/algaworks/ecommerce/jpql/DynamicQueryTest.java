package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Produto;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Objects;

public class DynamicQueryTest extends EntityManagerTest {

    @Test
    public void executarConsultaDinamica() {

        Produto produto = new Produto();
        produto.setNome("Câmera GoPro");

        List<Produto> list = pesquisar(produto);

        Assert.assertFalse(Objects.requireNonNull(list).isEmpty());
        Assert.assertEquals("Câmera GoPro Hero 7", list.get(0).getNome());
    }

    private List<Produto> pesquisar(Produto produto) {

        StringBuilder jpql = new StringBuilder("select p from Produto p where 1 = 1 ");

        if(produto.getNome() != null) {
            jpql.append(" and p.nome like concat('%', :nome, '%')");
        }

        if(produto.getDescricao() != null) {
            jpql.append(" and p.descricao like concat('%', :descricao, '%')");
        }

        TypedQuery<Produto> typedQuery = entityManager.createQuery(jpql.toString(), Produto.class);

        if(produto.getNome() != null) {
            typedQuery.setParameter("nome", produto.getNome());
        }

        if(produto.getDescricao() != null) {
            typedQuery.setParameter("descricao", produto.getDescricao());
        }

        return typedQuery.getResultList();
    }


}
