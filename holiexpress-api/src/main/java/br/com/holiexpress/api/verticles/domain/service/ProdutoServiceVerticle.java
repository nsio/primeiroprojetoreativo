package br.com.holiexpress.api.verticles.domain.service;

import java.util.UUID;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ProdutoServiceVerticle extends AbstractVerticle{
	
	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		inserir();
		atualizar();
		getById();
		list();
		super.start(startFuture);
	}	
	
	Promise<Void> inserir() {
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_ADD_SERVICE.getValor(), 
								  msg ->{	
			vertx.eventBus().request(EventBusEnum.PRODUTO_ADD_REPOSITORY.getValor(), 
									msg.body(), 
									handlerRepository -> {
				msg.reply((JsonArray)handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> atualizar() {
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_UPDATE_SERVICE.getValor(), 
								  msg ->{	
			vertx.eventBus().request(EventBusEnum.PRODUTO_UPDATE_REPOSITORY.getValor(), 
									msg.body(), 
									handlerRepository -> {
				msg.reply((JsonArray)handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> list() {
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_LIST_SERVICE.getValor(), 
								  msg ->{
			vertx.eventBus().request(EventBusEnum.PRODUTO_LIST_REPOSITORY.getValor(), 
									 "", 
									 handlerRepository -> {
				msg.reply(handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> getById() {
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_BY_ID_SERVICE.getValor(), 
								  msg ->{
			vertx.eventBus().request(EventBusEnum.PRODUTO_BY_ID_REPOSITORY.getValor(), 
								     new JsonObject().put(ProdutoEnum.ID.getValor(), msg.body().toString()), 
								     handlerRepository -> {
				msg.reply(handlerRepository.result().body());
			});
		});
		return Promise.promise();
	}
	
}
