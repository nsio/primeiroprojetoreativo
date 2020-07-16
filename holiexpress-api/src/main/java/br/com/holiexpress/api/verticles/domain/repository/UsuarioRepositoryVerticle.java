package br.com.holiexpress.api.verticles.domain.repository;

import br.com.holiexpress.api.util.Util;
import br.com.holiexpress.api.verticles.domain.model.Usuario;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.DMLEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.PgEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.UsuarioEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

public class UsuarioRepositoryVerticle extends AbstractVerticle {
	
	private static final String ADD_USER = "INSERT INTO public.usuario (nome, email, login, password) VALUES ($1, $2, $3, $4) RETURNING *";
	private static final String UPDATE_USER = "UPDATE public.usuario set nome = $1, email = $2, login = $3, password = $4 WHERE id = $5 RETURNING *";
	private static final String LIST_ALL = "SELECT * FROM public.usuario ORDER BY nome ASC";
	private static final String GET_BY_ID = "SELECT * FROM public.usuario WHERE id = $1";
	private static final String LOGIN = "SELECT * FROM public.usuario WHERE login = $1 and password = $2";
	
	private PgPool client;
	
	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		configureSqlClient();
		inserir();
		atualizar();
		list();
		getById();
		login();
		super.start(startFuture);
	}
	
	Promise<Void> configureSqlClient(){
		//client = JDBCClient.createShared(vertx, config().getJsonObject("db"), "PostgreSQLPool1");
		PgConnectOptions connectOptions = new PgConnectOptions()
				  .setPort(5432)
				  .setHost(PgEnum.HOST.getValor())
				  .setDatabase(PgEnum.DATA_BASE.getValor())
				  .setUser(PgEnum.USER.getValor())
				  .setPassword(PgEnum.PASSWORD.getValor());
		PoolOptions poolOptions = new PoolOptions()
				  .setMaxSize(5);
		client = PgPool.pool(vertx, connectOptions, poolOptions);
		return Promise.promise();
	}
	
	Promise<Void> atualizar(){
		vertx.eventBus().consumer(EventBusEnum.USUARIO_UPDATE_REPOSITORY.getValor(), 
								  msg ->{
			client.preparedQuery(UPDATE_USER).execute(Util.getInstance().toParamsAsTuple(msg.body().toString(), Usuario.class, DMLEnum.UPDATE), 
													  resultado -> {
				if(resultado.failed()) {
					resultado.cause().printStackTrace();
				}
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonObject(resultado.result().value(), Usuario.class));
				}else {
					msg.fail(500, resultado.cause().getLocalizedMessage());
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> inserir(){
		vertx.eventBus().consumer(EventBusEnum.USUARIO_ADD_REPOSITORY.getValor(), 
								  msg ->{
			client.preparedQuery(ADD_USER).execute(Util.getInstance().toParamsAsTuple(msg.body().toString(), Usuario.class, DMLEnum.INSERT), 
														   resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonObject(resultado.result().value(), Usuario.class));
				}else {
					msg.reply(Util.getInstance().jsonToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> list(){
		vertx.eventBus().consumer(EventBusEnum.USUARIO_LIST_REPOSITORY.getValor(), 
								  msg ->{
			client.preparedQuery(LIST_ALL).execute(resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonArray(resultado.result(), Usuario.class));
				}else {
					msg.reply(Util.getInstance().jsonArrayToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> getById(){
		vertx.eventBus().consumer(EventBusEnum.USUARIO_BY_ID_REPOSITORY.getValor(), 
								  msg ->{
			JsonObject user = new JsonObject(msg.body().toString());
			client.preparedQuery(GET_BY_ID).execute(Tuple.of(Integer.parseInt(user.getString(ProdutoEnum.ID.getValor()))), resultado -> {
				if(resultado.succeeded()) {
					if(resultado.result().size() <= 0) {
						msg.reply(Util.getInstance().jsonToException(404, UsuarioEnum.NENHUM_USUARIO_ENCONTRADO.getValor()));
					}else {
						msg.reply(Util.getInstance().asJsonObject(resultado.result(), Usuario.class));
					}
				}else {
					msg.reply(Util.getInstance().jsonToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> login(){
		vertx.eventBus().consumer(EventBusEnum.USUARIO_LOGIN_REPOSITORY.getValor(), 
								  msg ->{
			JsonObject user = new JsonObject(msg.body().toString());
			client.preparedQuery(LOGIN).execute(Tuple.of(user.getString(UsuarioEnum.LOGIN.getValor()), user.getString(UsuarioEnum.PASSWORD.getValor())),
												resultado -> {
				if(resultado.succeeded()) {
					if(resultado.result().size() <= 0) {
						msg.reply(Util.getInstance().jsonToException(404, UsuarioEnum.LOGIN_INVALIDO.getValor()));
					}else {
						msg.reply(Util.getInstance().asJsonObject(resultado.result(), Usuario.class));
					}
				}else {
					msg.reply(Util.getInstance().jsonToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
}
