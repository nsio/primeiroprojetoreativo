package br.com.holiexpress.api.verticles.controller;

import java.util.Optional;

import br.com.holiexpress.api.util.CarrinhoUtil;
import br.com.holiexpress.api.util.Util;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.CarrinhoEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.RoutesEnum;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class CarrinhoController extends RouteHandlerImpl {

	private static final String STATUS = "status";
	
    private Vertx vertx;
    
    public CarrinhoController(Vertx vertx, Router router) {
        super(vertx, router);
    	this.vertx = vertx;
    	router.post(RoutesEnum.CARRINHO_ADD.getValor())
    		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::adicionarAoCarrinho);
    	router.delete(RoutesEnum.CARRINHO_DELETE.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::excluirItemCarrinho);
    	router.get(RoutesEnum.CARRINHO_VISUALIZAR.getValor())
    		.produces(RoutesEnum.CONTENT_TYPE.getValor())
    		.handler(this::getCarrinho);
    } 
    
    public void adicionarAoCarrinho(RoutingContext ctx) {
		vertx.eventBus().request(EventBusEnum.CARRINHO_ADD_SERVICE.getValor(), 
								 CarrinhoUtil.getInstance().jsonArryToAdd(ctx.getBodyAsJsonArray(), ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor())), 
								 reply -> {
    		JsonArray json = (JsonArray)reply.result().body();
    		if(reply.succeeded()) {
    			ctx.session().put(CarrinhoEnum.SESSION_NAME.getValor(), json);
    			JsonArray r = (JsonArray)ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor());
        		ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(r.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(((JsonObject)json.getValue(0)).getInteger(STATUS)).end(json.getValue(0).toString());
    		}
    	});
    }
    
    public void excluirItemCarrinho(RoutingContext ctx) {
		vertx.eventBus().request(EventBusEnum.CARRINHO_EXCLUIR_SERVICE.getValor(),
								 CarrinhoUtil.getInstance().jsonArryToAdd(ctx.getBodyAsJsonArray(), ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor())), 
								 reply -> {
    		JsonArray json = (JsonArray)reply.result().body();
    		if(reply.succeeded()) {
    			ctx.session().put(CarrinhoEnum.SESSION_NAME.getValor(), json);
    			JsonArray r = (JsonArray)ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor());
        		ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(r.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(((JsonObject)json.getValue(0)).getInteger(STATUS)).end(json.getValue(0).toString());
    		}
    	});
    		
    }
    
    public void getCarrinho(RoutingContext ctx) {
		if(Optional.ofNullable(ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor())).isPresent()) {
			JsonArray carrinhoAtual = (JsonArray)ctx.session().get(CarrinhoEnum.SESSION_NAME.getValor());
			if(carrinhoAtual.isEmpty()) {
				ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
											.end(Util.getInstance().jsonToException(404, CarrinhoEnum.MENSAGEM_VAZIO.getValor()).encodePrettily());
			}
    		ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(carrinhoAtual.encodePrettily());
		}else {
			ctx.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
										.end(Util.getInstance().jsonToException(404, CarrinhoEnum.MENSAGEM_VAZIO.getValor()).encodePrettily());
		}
    }
    
}
