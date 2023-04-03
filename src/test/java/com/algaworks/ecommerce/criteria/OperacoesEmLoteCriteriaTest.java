package com.algaworks.ecommerce.criteria;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.jpql.OperacoesEmLoteTest;
import com.algaworks.ecommerce.model.Categoria;
import com.algaworks.ecommerce.model.Categoria_;
import com.algaworks.ecommerce.model.Produto;
import com.algaworks.ecommerce.model.Produto_;
import org.junit.Test;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

public class OperacoesEmLoteCriteriaTest extends EntityManagerTest {

    /*
        Evitando deixar muitos registros na memória para não ocorrer problema de memória do Java, em operações em Lote
        Uma das técnicas pra isso, abaixo, no JPA.
     */

    private final int LIMITE_INSERCOES = 4; // colocar o máximo de registros que queremos deixar em memória.

    @Test
    public void removerEmLoteExercicio() {

//        String jpql = "delete from Produto p where p.id between 5 and 12";

        entityManager.getTransaction().begin();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<Produto> criteriaDelete = criteriaBuilder.createCriteriaDelete(Produto.class);
        Root<Produto> root = criteriaDelete.from(Produto.class);

        criteriaDelete.where(criteriaBuilder.between(root.get(Produto_.id), 5, 12));

        Query query = entityManager.createQuery(criteriaDelete);
        query.executeUpdate();

        entityManager.getTransaction().commit();
    }

    @Test
    public void atualizarEmLote() {
        // Atualizar o preco do produto com o fator 110% somente em produtos da categoria do id informado.
//        String jpql = "update Produto p set p.preco = p.preco + (p.preco * 1.1) " +
//                "where exists (select 1 from p.categorias c2 where c2.id = :categoria)";

        entityManager.getTransaction().begin();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Produto> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Produto.class);
        Root<Produto> root = criteriaUpdate.from(Produto.class);

        criteriaUpdate.set(
            root.get(Produto_.preco),
            criteriaBuilder.prod(root.get(Produto_.preco), new BigDecimal("1.1"))
        );

        Subquery<Integer> subquery = criteriaUpdate.subquery(Integer.class);
        Root<Produto> subqueryRoot = subquery.correlate(root);
        Join<Produto, Categoria> subqueryJoinCategoria = subqueryRoot.join(Produto_.categorias);

        subquery.select(criteriaBuilder.literal(1));

        subquery.where(criteriaBuilder.equal(subqueryJoinCategoria.get(Categoria_.id), 2));

        criteriaUpdate.where(criteriaBuilder.exists(subquery));

        Query query = entityManager.createQuery(criteriaUpdate);
        query.executeUpdate();

        entityManager.getTransaction().commit();
    }

    /*
        Não Existe forma de inserção em lote com JPQL ou Criteria API. Então essa é uma das formas que pode ser feito.
     */
    @Test
    public void inserirEmLote() throws IOException {
        InputStream inputStream = OperacoesEmLoteTest.class.getClassLoader()
                .getResourceAsStream("produtos/importar.txt");

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(inputStream)));

        entityManager.getTransaction().begin();

        int contadorInsercoes = 0;


        for (String linha: bufferedReader.lines().collect(Collectors.toList())) {
            if (linha.isBlank()) {
                continue;
            }

            String[] produtoColuna = linha.split(";");
            Produto produto = new Produto();
            produto.setNome(produtoColuna[0]);
            produto.setDescricao(produtoColuna[1]);
            produto.setPreco(new BigDecimal(produtoColuna[2]));
            produto.setDataCriacao(LocalDateTime.now());

            entityManager.persist(produto);

            if(++contadorInsercoes == LIMITE_INSERCOES) {
                entityManager.flush();
                entityManager.clear();

                contadorInsercoes = 0;

                System.out.println("--------------------------------------------------------");
            }
        }

        entityManager.getTransaction().commit();
    }

}
