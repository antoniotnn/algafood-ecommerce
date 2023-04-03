package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Produto;
import org.junit.Test;

import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

public class OperacoesEmLoteTest extends EntityManagerTest {
    /*
        Evitando deixar muitos registros na memória para não ocorrer problema de memória do Java, em operações em Lote
        Uma das técnicas pra isso, abaixo, no JPA.
     */

    private final int LIMITE_INSERCOES = 4; // colocar o máximo de registros que queremos deixar em memória.

    @Test
    public void removerEmLote() {
        entityManager.getTransaction().begin();

        String jpql = "delete from Produto p where p.id between 8 and 12";

        Query query = entityManager.createQuery(jpql);
        query.executeUpdate();

        entityManager.getTransaction().commit();
    }

    @Test
    public void atualizarEmLote() {
        entityManager.getTransaction().begin();

        String jpql = "update Produto p set p.preco = p.preco + (p.preco * 0.1) " +
                "where exists (select 1 from p.categorias c2 where c2.id = :categoria)";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("categoria", 2);
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
