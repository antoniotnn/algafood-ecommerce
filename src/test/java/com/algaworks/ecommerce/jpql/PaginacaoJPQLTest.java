package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Categoria;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class PaginacaoJPQLTest extends EntityManagerTest {

    @Test
    public void paginarResultados() {

        String jpql = "select c from Categoria c order by c.nome"; //desc

        TypedQuery<Categoria> typedQuery = entityManager.createQuery(jpql, Categoria.class);

        // Fórmula para a página que o Usuário quiser acessar:
        // FIRST_RESULT = MAX_RESULTS * (paginaQueUsuarioQuerEntrar - 1)
        typedQuery.setFirstResult(6); // setar página onde virá o primeiro resultado
        typedQuery.setMaxResults(2); // no máximo X resultados // pode ser usado sozinho apenas pra limitar o resultado.

        List<Categoria> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(c -> System.out.println(c.getId() + ", " + c.getNome()));
    }

}
