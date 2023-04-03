package com.algaworks.ecommerce.jpql;

import com.algaworks.ecommerce.EntityManagerTest;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class FuncoesTest extends EntityManagerTest {

    @Test
    public void aplicarFuncaoAgregacao() {
        // avg, count, min, max, sum

//        String jpql = "select avg(p.total) from Pedido p";
//        String jpql = "select avg(p.total) from Pedido p where p.dataCriacao >= current_date ";
//        String jpql = "select count(p.dataCriacao) from Pedido p";
//        String jpql = "select min(p.total) from Pedido p";
//        String jpql = "select max(p.total) from Pedido p";
        String jpql = "select sum(p.total) from Pedido p";

        TypedQuery<Number> typedQuery = entityManager.createQuery(jpql, Number.class);

        List<Number> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println(obj));
    }

    @Test
    public void aplicarFuncaoNativas() {

        // chamar functions nativas criadas no banco de dados, ou que ja existem disponibilizladas pelo proprio sgbd
        // parametros: nome da funcao, parametros de entrada separados por virgula
//        String jpql = "select p from Pedido p where function('acima_media_faturamento', p.total) = 1";
        String jpql = "select function('dayname', p.dataCriacao) from Pedido p " +
                "where function('acima_media_faturamento', p.total) = 1";

        TypedQuery<String> typedQuery = entityManager.createQuery(jpql, String.class);

        List<String> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(obj -> System.out.println(obj));
    }

    @Test
    public void aplicarFuncaoColecao() {

        //size() retorna a qtd de elementos da coleção

//        String jpql = "select size(p.itens) from Pedido p";
        String jpql = "select size(p.itens) from Pedido p where size(p.itens) > 1 ";

        TypedQuery<Integer> typedQuery = entityManager.createQuery(jpql, Integer.class);

        List<Integer> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(size -> System.out.println(size));
    }

    @Test
    public void aplicarFuncaoNumero() {

        //abs() valor absoluto, mod() resto da divisao, sqrt() raiz quadrada

//        String jpql = "select abs(-10), mod(3, 2), sqrt(9) from Pedido ";

        String jpql = "select abs(p.total), mod(p.id, 2), sqrt(p.total) from Pedido p " +
                "where abs(p.total) > 1000 ";

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + " | " + arr[1] + " | " + arr[2]));
    }

    @Test
    public void aplicarFuncaoData() {
        //TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

//        String jpql = "select year(p.dataCriacao), month(p.dataCriacao), day(p.dataCriacao) from Pedido p";

        String jpql = "select hour(p.dataCriacao), minute(p.dataCriacao), second(p.dataCriacao) from Pedido p";

//        String jpql = "select hour(p.dataCriacao), minute(p.dataCriacao), second(p.dataCriacao) " +
//                "from Pedido p where hour(p.dataCriacao) > 18";

//        String jpql = "select current_date, current_time, current_timestamp from Pedido p";
        //retorna data, hora , instante . (De acordo com a configuração do banco que pode ser
        // encontrada em persistence.xml na propriedade javax.persistence.jdbc.url
        // para ajustar isso pode ser usado a função TimeZone.setDefault definida acima

        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + " | " + arr[1] + " | " + arr[2]));
    }



    @Test
    public void aplicarFuncaoString() {

        // concat, length, locate, substring, lower, upper, trim

//        String jpql = "select c.nome, concat('Categoria: ' , c.nome) from Categoria c";

//        String jpql = "select c.nome, length(c.nome) from Categoria c";

//        String jpql = "select c.nome, locate('a', c.nome) from Categoria c"; //locate é como o indexOf do Java, retorna a posição real (não a de array) do elemento indicado no primeiro parametro, dentro do elemento do segundo parametro

//        String jpql = "select c.nome, substring(c.nome, 1, 2) from Categoria c"; // string primeiro parametro, indice onde vai comecar a busca no segundo parametro. Aceita um terceiro parametro que diz quantos caracteres a gente quer buscar apos o segundo parametro

//        String jpql = "select c.nome, lower(c.nome) from Categoria c";

//        String jpql = "select c.nome, upper(c.nome) from Categoria c";

//        String jpql = "select c.nome, trim(c.nome) from Categoria c"; //remover espaços d inicio e do fim

//        String jpql = "select c.nome, length(c.nome), length(c.nome) from Categoria c where length(c.nome) > 10 ";

        String jpql = "select c.nome, length(c.nome) from Categoria c " +
                " where substring(c.nome, 1, 1) = 'N' ";


        TypedQuery<Object[]> typedQuery = entityManager.createQuery(jpql, Object[].class);

        List<Object[]> list = typedQuery.getResultList();
        Assert.assertFalse(list.isEmpty());

        list.forEach(arr -> System.out.println(arr[0] + " - " + arr[1]));
    }
}
