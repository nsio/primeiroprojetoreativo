package br.com.holiexpress.api.verticles.domain.service;

import java.util.UUID;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.UsuarioEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class UsuarioSerivceVerticle extends AbstractVerticle {

	String userServiceVerticleId = UUID.randomUUID().toString();
	
	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		inserir();
		atualizar();
		list();
		getById();
		login();
		super.start(startFuture);
	}	
	
	Promise<Void> inserir() {
		vertx.eventBus().consumer(EventBusEnum.USUARIO_ADD_SERVICE.getValor(), 
								  msg ->{	
			vertx.eventBus().request(EventBusEnum.USUARIO_ADD_REPOSITORY.getValor(), 
									 msg.body(), 
									 handlerRepository -> {
				msg.reply((JsonObject)handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> atualizar() {
		vertx.eventBus().consumer(EventBusEnum.USUARIO_UPDATE_SERVICE.getValor(), 
								  msg ->{	
			vertx.eventBus().request(EventBusEnum.USUARIO_UPDATE_REPOSITORY.getValor(), 
									 msg.body(), 
									 handlerRepository -> {
				msg.reply((JsonObject)handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> list() {
		vertx.eventBus().consumer(EventBusEnum.USUARIO_LIST_SERVICE.getValor(), 
								  msg ->{
			vertx.eventBus().request(EventBusEnum.USUARIO_LIST_REPOSITORY.getValor(), 
									 "", 
									 handlerRepository -> {
				msg.reply(handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> getById() {
		vertx.eventBus().consumer(EventBusEnum.USUARIO_BY_ID_SERVICE.getValor(), 
								  msg ->{
			JsonObject user = new JsonObject();
			user.put(UsuarioEnum.ID.getValor(), msg.body().toString());
			vertx.eventBus().request(EventBusEnum.USUARIO_BY_ID_REPOSITORY.getValor(), 
									 user, 
									 handlerRepository -> {
				msg.reply(handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> login() {
		vertx.eventBus().consumer(EventBusEnum.USUARIO_LOGIN_SERVICE.getValor(), 
								 msg ->{
			vertx.eventBus().request(EventBusEnum.USUARIO_LOGIN_REPOSITORY.getValor(), 
									 msg.body(), 
									 handlerRepository -> {
				msg.reply(handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
}
