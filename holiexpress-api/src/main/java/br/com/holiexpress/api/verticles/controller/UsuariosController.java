package br.com.holiexpress.api.verticles.controller;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.RoutesEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.UsuarioEnum;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class UsuariosController extends RouteHandlerImpl {
	
	private static final String STATUS = "status";
	
    private Vertx vertx;
    
    public UsuariosController(Vertx vertx, Router router) {
    	super(vertx, router);
    	this.vertx = vertx;
     	router.post(RoutesEnum.USUARIO_ADD.getValor())
     		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
     		.produces(RoutesEnum.CONTENT_TYPE.getValor())
     		.handler(this::inserirUsuario);
     	router.put(RoutesEnum.USUARIO_UPDATE.getValor())
     		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
     		.produces(RoutesEnum.CONTENT_TYPE.getValor())
     		.handler(this::atualizarUsuario);
     	router.get(RoutesEnum.USUARIO_LIST.getValor())
     		.produces(RoutesEnum.CONTENT_TYPE.getValor())
     		.handler(this::listarUsuario);
     	router.get(RoutesEnum.USUARIO_BY_ID.getValor())
     		.produces(RoutesEnum.CONTENT_TYPE.getValor())
     		.handler(this::getUsuarioById);
     	router.post(RoutesEnum.USUARIO_LOGIN.getValor())
     		.consumes(RoutesEnum.CONTENT_TYPE.getValor())
     		.produces(RoutesEnum.CONTENT_TYPE.getValor())
     		.handler(this::login);
    }
	
    // Usuarios ------------------------------------------------------------
    public void inserirUsuario(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.USUARIO_ADD_SERVICE.getValor(), 
    							 ctx.getBodyAsJson(), 
    							 reply -> {
    		JsonObject json = (JsonObject)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(json.getInteger(STATUS)).end(json.encodePrettily());
    		}
    	});
    }
    
    public void atualizarUsuario(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.USUARIO_UPDATE_SERVICE.getValor(), 
    							 ctx.getBodyAsJson(), 
    							 reply -> {
    		JsonObject json = (JsonObject)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.CREATED.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(json.getInteger(STATUS)).end(json.encodePrettily());
    		}
    	});
    }
    
    public void listarUsuario(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.USUARIO_LIST_SERVICE.getValor(), 
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
    
    public void getUsuarioById(RoutingContext ctx) {
    	String id = ctx.pathParam(UsuarioEnum.ID.getValor());
    	vertx.eventBus().request(EventBusEnum.USUARIO_BY_ID_SERVICE.getValor(), 
    							 id, 
    							 reply -> {
    		JsonObject json = (JsonObject)reply.result().body();
    		if(reply.succeeded()) {
        		ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(json.getInteger(STATUS)).end(json.encodePrettily());
    		}
    	});
    }
    
    public void login(RoutingContext ctx) {
    	vertx.eventBus().request(EventBusEnum.USUARIO_LOGIN_SERVICE.getValor(), 
    							 ctx.getBodyAsJson(), 
    							 reply -> {
    		JsonObject json = (JsonObject)reply.result().body();
    		if(reply.succeeded()) {
    			if(json.containsKey(UsuarioEnum.ID.getValor())) {
    				json.put("token", getAuthProvider().generateToken(new JsonObject()));
    			}
    			ctx.response().setStatusCode(HttpResponseStatus.OK.code()).end(json.encodePrettily());
    		}else {
    			ctx.response().setStatusCode(json.getInteger(STATUS)).end(json.encodePrettily());
    		}
    	});
    }
    
    JWTAuth getAuthProvider() {
//    	JWTAuthOptions config = new JWTAuthOptions()
//    			  .addPubSecKey(new PubSecKeyOptions()
//    					  .setAlgorithm("RS512")
//    			    .setPublicKey(getKeyStoreKey()));
    	JWTAuthOptions config = new JWTAuthOptions()
  			  .addPubSecKey(new PubSecKeyOptions()
  				    .setAlgorithm("HS256")
  				    .setPublicKey("secret")
  				    .setSymmetric(true));
    	JWTAuth provider = JWTAuth.create(vertx, config);
		return provider;
    }
}
