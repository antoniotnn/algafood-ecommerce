package com.algaworks.ecommerce.conhecendoentitymanager;

import com.algaworks.ecommerce.EntityManagerTest;
import com.algaworks.ecommerce.model.Categoria;
import org.junit.Test;

public class EstadosCiclosDeVidaTest extends EntityManagerTest {

    @Test
    public void analisarEstados() {

        Categoria categoriaNovo = new Categoria(); //nao gerenciado (estado novo, transient)
        categoriaNovo.setNome("Eletrônicos");

        Categoria categoriaGerenciadaMerge = entityManager.merge(categoriaNovo); //passou a ser gerenciado apos o merge

        Categoria categoriaGerenciada = entityManager.find(Categoria.class, 1); //gerenciada apos o find.

        entityManager.remove(categoriaGerenciada); //estado removed, não mais gerenciada
        entityManager.persist(categoriaGerenciada); // estado gerenciado novamente

        entityManager.detach(categoriaGerenciada); //desanexado, não gerenciado

    }
}
