package br.com.holiexpress.api.verticles.domain.repository;

import java.util.HashMap;
import java.util.List;

import br.com.holiexpress.api.util.SqlUtil;
import br.com.holiexpress.api.util.Util;
import br.com.holiexpress.api.verticles.domain.model.Produto;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.DMLEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.EventBusEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.PgEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Tuple;

public class ProdutoRepositoryVerticle extends AbstractVerticle {

	private static final String ADD_PRODUCT = "INSERT INTO public.produto (descricao, qtd_estoque, preco, id_user) VALUES ($1,$2,$3,$4) RETURNING *";
	private static final String UPDATE_PRODUCT = "UPDATE public.produto set descricao = $1, qtd_estoque = $2, preco = $3, id_user = $4 WHERE id = ? RETURNING *";
	private static final String LIST_ALL = "SELECT * FROM public.produto ORDER BY descricao ASC";
	private static final String GET_BY_ID = "SELECT * FROM public.produto p WHERE id = $1";
	private static final String SELECT_N_ID = "SELECT * FROM public.produto WHERE id IN (:ids)";
	
	private PgPool client;
	
	@Override
	public void start(Promise<Void> startFuture) throws Exception {
		configureSqlClient();
		inserir();
		atualizar();
		getById();
		getByNIds();
		list();
		super.start(startFuture);
	}
	
	Promise<Void> configureSqlClient(){
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
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_UPDATE_REPOSITORY.getValor(), 
								  msg ->{
			HashMap<DMLEnum, List<Tuple>> mapaParaDML = Util.getInstance().mapParaOperacoesDML(msg.body().toString(), Produto.class);
			client.preparedQuery(UPDATE_PRODUCT)
			.executeBatch(mapaParaDML.get(DMLEnum.UPDATE), 
						  resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonArray(resultado.result().value(), Produto.class));
				}else {
					msg.reply(Util.getInstance().jsonArrayToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> inserir(){
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_ADD_REPOSITORY.getValor(), 
								  msg ->{
			HashMap<DMLEnum, List<Tuple>> mapaParaDML = Util.getInstance().mapParaOperacoesDML(msg.body().toString(), Produto.class);
			client.preparedQuery(ADD_PRODUCT).executeBatch(mapaParaDML.get(DMLEnum.INSERT), resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonArray(resultado.result().value(), Produto.class));
				}else {
					msg.reply(Util.getInstance().jsonArrayToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> list(){
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_LIST_REPOSITORY.getValor(), 
								  msg ->{
			client.preparedQuery(LIST_ALL).execute(resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonArray(resultado.result(), Produto.class));
				}else {
					msg.reply(Util.getInstance().jsonArrayToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> getById(){
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_BY_ID_REPOSITORY.getValor(), 
								  msg ->{
			JsonObject produto = new JsonObject(msg.body().toString());
			client.preparedQuery(GET_BY_ID).execute(Tuple.of(Integer.parseInt(produto.getString(ProdutoEnum.ID.getValor()))), resultado -> {
				if(resultado.succeeded()) {
					if(resultado.result().size() <= 0) {
						msg.reply(Util.getInstance().jsonToException(404, ProdutoEnum.NENHUM_PRODUTO_ENCONTRADO.getValor()));
					}else {
						msg.reply(Util.getInstance().asJsonObject(resultado.result(), Produto.class));
					}
				}else {
					msg.reply(Util.getInstance().jsonToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
	Promise<Void> getByNIds(){
		vertx.eventBus().consumer(EventBusEnum.PRODUTO_LIST_IDS_REPOSITORY.getValor(), 
								  msg ->{
			client.preparedQuery(SqlUtil.getInstance().queryToVariosIds((JsonArray)msg.body(), SELECT_N_ID))
				  .execute(resultado -> {
				if(resultado.succeeded()) {
					msg.reply(Util.getInstance().asJsonArray(resultado.result(), Produto.class));
				}else {
					msg.reply(Util.getInstance().jsonArrayToException(500, resultado.cause().getLocalizedMessage()));
				}
			});
		});
		return Promise.promise();
	}
	
}
