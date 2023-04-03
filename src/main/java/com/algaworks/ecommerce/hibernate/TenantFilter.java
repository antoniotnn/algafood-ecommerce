/*
    Para multitenacy SEM SER POR COLUNA, Descomentar os atributos tenacy e remover este atributo em todas as chamadas e SQLS.
 */

//package com.algaworks.ecommerce.hibernate;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//
//public class TenantFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException { }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//        String serverName = request.getServerName();
//        String subdomain = serverName.substring(0, serverName.indexOf("."));
//
////        request.setAttribute("tenant", subdomain);
//
//        EcmCurrentTenantIdentifierResolver.setTenantIdentifier(subdomain + "_ecommerce");
//
//        filterChain.doFilter(servletRequest, servletResponse);
//    }
//
//    @Override
//    public void destroy() {}
//}