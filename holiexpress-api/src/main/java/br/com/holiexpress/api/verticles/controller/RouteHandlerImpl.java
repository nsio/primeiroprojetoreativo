package br.com.holiexpress.api.verticles.controller;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class RouteHandlerImpl implements RouteHandler {
	
	private Router router;
    
    public RouteHandlerImpl(Vertx vertx, Router router) {
    	this.router = router;
    }

    @Override
    public void handle(final RoutingContext ctx) {
         router.handleContext(ctx);
    }
    
}
