package br.com.holiexpress.api.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.holiexpress.api.verticles.domain.model.Produto;
import br.com.holiexpress.api.verticles.domain.model.Usuario;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.DMLEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.UsuarioEnum;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class Util {
	
	private static Util INSTANCE;
     
    private Util() {}
     
    public static Util getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Util();
        }
        return INSTANCE;
    }
	
	public JsonArray asJsonArray(RowSet<Row> rows, Object tipo) {
		JsonArray retorno = new JsonArray();
		if(tipo.equals(Produto.class)) {
			rows.forEach(row -> {
				retorno.add(asJsonObject(row, tipo));
			});
		}
		if(tipo.equals(Usuario.class)) {
			rows.forEach(row -> {
				retorno.add(asJsonObject(row, tipo));
			});
		}
		return retorno;
	}
	
	public JsonObject asJsonObject(RowSet<Row> rows, Object tipo) {
		JsonArray retorno = new JsonArray();
		if(tipo.equals(Produto.class)) {
			rows.forEach(row -> {
				retorno.add(asJsonObject(row, tipo));
			});
		}
		if(tipo.equals(Usuario.class)) {
			rows.forEach(row -> {
				retorno.add(asJsonObject(row, tipo));
			});
		}
		return retorno.getJsonObject(0);
	}
	
	public JsonObject asJsonObject(Row row, Object tipo) {
		JsonObject retorno = new JsonObject();
		if(tipo.equals(Produto.class)) {
			retorno.put(ProdutoEnum.ID.getValor(), row.getInteger(ProdutoEnum.ID.getValor()));
			retorno.put(ProdutoEnum.DESCRICAO.getValor(), row.getString(ProdutoEnum.DESCRICAO.getValor()));
			retorno.put(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor(), row.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()));
			retorno.put(ProdutoEnum.PRECO.getValor(), row.getDouble(ProdutoEnum.PRECO.getValor()));
			retorno.put(ProdutoEnum.ID_USER.getValor(), row.getInteger(ProdutoEnum.ID_USER.getValor()));
		}
		if(tipo.equals(Usuario.class)) {
			retorno.put(UsuarioEnum.ID.getValor(), row.getInteger(UsuarioEnum.ID.getValor()));
			retorno.put(UsuarioEnum.NOME.getValor(), row.getString(UsuarioEnum.NOME.getValor()));
			retorno.put(UsuarioEnum.EMAIL.getValor(), row.getString(UsuarioEnum.EMAIL.getValor()));
			retorno.put(UsuarioEnum.LOGIN.getValor(), row.getString(UsuarioEnum.LOGIN.getValor()));
			retorno.put(UsuarioEnum.PASSWORD.getValor(), row.getString(UsuarioEnum.PASSWORD.getValor()));
		}
		return retorno;
	}
	
	public JsonArray toParams(String json, Object tipo, DMLEnum dmlEnum) {
		JsonObject jsonObject = new JsonObject(json);
		JsonArray retorno = new JsonArray();
		if(tipo.equals(Produto.class)) {
			if (dmlEnum.equals(DMLEnum.UPDATE)) {
				retorno.add(jsonObject.getString(ProdutoEnum.ID.getValor()));
			}
			retorno.add(jsonObject.getString(ProdutoEnum.DESCRICAO.getValor()));
			retorno.add(jsonObject.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()));
			retorno.add(jsonObject.getDouble(ProdutoEnum.PRECO.getValor()));
			retorno.add(jsonObject.getInteger(ProdutoEnum.ID_USER.getValor()));
		}
		if(tipo.equals(Usuario.class)) {
			if (dmlEnum.equals(DMLEnum.UPDATE)) {
				retorno.add(jsonObject.getString(UsuarioEnum.ID.getValor()));
			}
			retorno.add(jsonObject.getString(UsuarioEnum.NOME.getValor()));
			retorno.add(jsonObject.getString(UsuarioEnum.EMAIL.getValor()));
			retorno.add(jsonObject.getString(UsuarioEnum.LOGIN.getValor()));
			retorno.add(jsonObject.getString(UsuarioEnum.PASSWORD.getValor()));
		}
		return retorno;
	}
	
	public Tuple toParamsAsTuple(String json, Object tipo, DMLEnum dmlEnum) {
		JsonObject jsonObject = new JsonObject(json);
		if(tipo.equals(Produto.class)) {
			if (dmlEnum.equals(DMLEnum.UPDATE)) {
				return Tuple.of(jsonObject.getString(ProdutoEnum.ID.getValor()),
								jsonObject.getString(ProdutoEnum.DESCRICAO.getValor()),
								jsonObject.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()),
								jsonObject.getDouble(ProdutoEnum.PRECO.getValor()),
								jsonObject.getInteger(ProdutoEnum.ID_USER.getValor()));
			}
			return Tuple.of(jsonObject.getString(ProdutoEnum.DESCRICAO.getValor()),
							jsonObject.getInteger(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor()),
							jsonObject.getDouble(ProdutoEnum.PRECO.getValor()),
							jsonObject.getInteger(ProdutoEnum.ID_USER.getValor()));
		}
		if(tipo.equals(Usuario.class)) {
			if (dmlEnum.equals(DMLEnum.UPDATE)) {
				return Tuple.of(jsonObject.getString(UsuarioEnum.ID.getValor()),
								jsonObject.getString(UsuarioEnum.NOME.getValor()),
								jsonObject.getString(UsuarioEnum.EMAIL.getValor()),
								jsonObject.getString(UsuarioEnum.LOGIN.getValor()),
								jsonObject.getString(UsuarioEnum.PASSWORD.getValor()));
			}
		}
		return Tuple.of(jsonObject.getString(UsuarioEnum.NOME.getValor()),
						jsonObject.getString(UsuarioEnum.EMAIL.getValor()),
						jsonObject.getString(UsuarioEnum.LOGIN.getValor()),
						jsonObject.getString(UsuarioEnum.PASSWORD.getValor()));
	}
	
	public JsonObject jsonToException(Integer code, String mensagem) {
		JsonObject retorno = new JsonObject();
		retorno.put("status", code);
		retorno.put("mensagem", mensagem);
		return retorno;
	}
	
	public JsonArray jsonArrayToException(Integer code, String mensagem) {
		JsonObject retorno = new JsonObject();
		retorno.put("status", code);
		retorno.put("mensagem", mensagem);
		return new JsonArray().add(retorno);
	}
	
	public HashMap<DMLEnum, List<Tuple>> mapParaOperacoesDML(String json, Object tipo){
		List<Tuple> paraInsertList = new ArrayList<Tuple>();
		List<Tuple> paraUpdateList = new ArrayList<Tuple>();
		HashMap<DMLEnum, List<Tuple>> retorno = new HashMap<DMLEnum, List<Tuple>>();
		JsonArray params = new JsonArray(json);
		params.forEach(object -> {
			JsonObject item = (JsonObject) object;
			if(item.getString("id") == null || item.getString("id").equalsIgnoreCase("")) {
				paraInsertList.add(toParamsAsTuple(item.toString(), tipo, DMLEnum.INSERT));
			}else {
				paraUpdateList.add(toParamsAsTuple(item.toString(), tipo, DMLEnum.UPDATE));
			}
		});
		retorno.put(DMLEnum.INSERT, paraInsertList);
		retorno.put(DMLEnum.UPDATE, paraUpdateList);
		return retorno;
	} 
	
}
