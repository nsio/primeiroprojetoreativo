package br.com.holiexpress.api.verticles.controller;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public interface RouteHandler extends Handler<RoutingContext> {
	
	static RouteHandlerImpl create(Vertx vertx, Router router) {
		createUsuarioRouteHandler(vertx, router);
		createProdutoRouteHandler(vertx, router);
		createCarrinhoRouteHandler(vertx, router);
        return new RouteHandlerImpl(vertx, router);
    }
	
	static UsuariosController createUsuarioRouteHandler(Vertx vertx, Router router) {
        return new UsuariosController(vertx, router);
    }
	
	static ProdutosController createProdutoRouteHandler(Vertx vertx, Router router) {
        return new ProdutosController(vertx, router);
    }
	
	static CarrinhoController createCarrinhoRouteHandler(Vertx vertx, Router router) {
        return new CarrinhoController(vertx, router);
    }
}
