package br.com.holiexpress.api.verticles.controller;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.RoutesEnum;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProdutosController extends RouteHandlerImpl {
	
private static final String STATUS = "status";
	
    private Vertx vertx;
    
    public ProdutosController(Vertx vertx, Router router) {
    	super(vertx, router);
        this.vertx = vertx;
    	router.post(RoutesEnum.PRODUTO_ADD.getValor())
    		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::inserirProduto);
    	router.put(RoutesEnum.PRODUTO_UPDATE.getValor())
    		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::atualizarProduto);
    	router.get(RoutesEnum.PRODUTO_LIST.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::listarProduto);
    	router.get(RoutesEnum.PRODUTO_BY_ID.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::getProdutotById);
    }
	
    public void inserirProduto(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.PRODUTO_ADD_SERVICE.getValor(), 
    							 ctx.getBodyAsJsonArray(), 
    							 reply -> {
    		JsonArray json = (JsonArray)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(((JsonObject)json.getValue(0)).getInteger(STATUS)).end(json.getValue(0).toString());
    		}
    	});
    }
    
    public void atualizarProduto(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.PRODUTO_UPDATE_SERVICE.getValor(), 
    							 ctx.getBodyAsJsonArray(), 
    							 reply -> {
    		JsonArray json = (JsonArray)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(((JsonObject)json.getValue(0)).getInteger(STATUS)).end(json.getValue(0).toString());
    		}
    	});
    }
    
    public void listarProduto(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.PRODUTO_LIST_SERVICE.getValor(), 
    							 "", 
    							 reply -> {
    		JsonArray json = (JsonArray)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(((JsonObject)json.getValue(0)).getInteger(STATUS)).end(json.getValue(0).toString());
    		}
    	});
    }
    
    public void getProdutotById(RoutingContext ctx) {
    	String id = ctx.pathParam(ProdutoEnum.ID.getValor());
    	vertx.eventBus().request(EventBusEnum.PRODUTO_BY_ID_SERVICE.getValor(), 
    							 id, 
    							 reply -> {
    		JsonObject json = new JsonObject(reply.result().body().toString());
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(json.getInteger(STATUS)).end(json.encodePrettily());
    		}
    	});
    }
}
