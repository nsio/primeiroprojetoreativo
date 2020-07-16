package br.com.holiexpress.api.verticles;

import br.com.holiexpress.api.util.Util;
import br.com.holiexpress.api.verticles.controller.RouteHandler;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.RoutesEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

public class WebVerticle extends AbstractVerticle {

	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		configurarRouterAndStartServer();
		super.start(startFuture);
	}
	
	// ROUTE
    Promise<Router> configurarRouterAndStartServer(){
    	Router router = Router.router(vertx);
    	router.route().handler(BodyHandler.create());
    	router.route().handler(LoggerHandler.create(LoggerFormat.DEFAULT));
    	
    	JWTAuth authProvider = getAuthProvider();
    	AuthHandler redirectAuthHandler = JWTAuthHandler.create(authProvider);
    	
    	LocalSessionStore session = LocalSessionStore.create(vertx, "holiexpress.sessionmap");
    	
    	router.route().handler(SessionHandler.create(session).setAuthProvider(authProvider));
    	router.route(RoutesEnum.ROOT.getValor()).handler(RouteHandler.create(vertx, router));
    	router.route(RoutesEnum.ROOT_RESTRITO.getValor()).handler(redirectAuthHandler);
    	
    	
    	// Sessions
    	//SessionStore store = ClusteredSessionStore.create(vertx); // Para todos os menbros da session
    	//SessionStore store = LocalSessionStore.create(vertx); // Para um único nó.
    	//router.route().handler(SessionHandler.create(store));
    	// CORS (Futura conficuração para WebClient, na comunicação para efetuar o pagamento(checkout))
    	//router.route().handler(CorsHandler.create("http://www.paypal.com"));
    	// EXCEPTION
    	router.errorHandler(500, rc -> {
								Throwable failure = rc.failure();
								if (failure != null) {
									JsonObject exception = Util.getInstance().jsonToException(500, failure.getLocalizedMessage());
									rc.response().setStatusCode(exception.getInteger("status")).end(exception.encodePrettily());
								}
								
							});
    	
    	JsonObject http = config().getJsonObject("http");
		//Integer port = http.getInteger("port");
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(request -> router.accept(request));
		httpServer.listen(8081);
		
    	return Promise.promise();
    }
        
    JWTAuth getAuthProvider() {
    	JWTAuthOptions config = new JWTAuthOptions()
  			  .addPubSecKey(new PubSecKeyOptions()
  				    .setAlgorithm("HS256")
  				    .setPublicKey("secret")
  				    .setSymmetric(true));
    	JWTAuth provider = JWTAuth.create(vertx, config);
		return provider;
    }
    
}
