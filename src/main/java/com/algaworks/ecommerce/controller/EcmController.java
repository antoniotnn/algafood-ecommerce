/*
    Para multitenacy SEM SER POR COLUNA, Descomentar os atributos tenacy e remover este atributo em todas as chamadas e SQLS.
 */

package com.algaworks.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EcmController {

    @GetMapping
    public String index() {
        return "index";
    }
}