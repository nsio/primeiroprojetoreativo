package br.com.holiexpress.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.holiexpress.api.verticles.domain.model.Produto;
import br.com.holiexpress.api.verticles.domain.model.dto.ProdutoDTO;
import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

public class CarrinhoUtil {
	
	private static CarrinhoUtil INSTANCE;
    
    private CarrinhoUtil() {}
     
    public static CarrinhoUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new CarrinhoUtil();
        }
        return INSTANCE;
    }
    
    public JsonArray jsonArryToAdd(JsonArray fromRequest, JsonArray fromSession) {
		fromRequest.forEach(object -> {
			JsonObject item = (JsonObject) object;
			item.put("fromRequest", 1);
		});
		if(Optional.ofNullable(fromSession).isPresent()) {
			fromSession.forEach(object -> {
				JsonObject item = (JsonObject) object;
				item.put("fromRequest", 0);
				fromRequest.add(item);
			});
		}
		return fromRequest;
    }
    
    public List<ProdutoDTO> listaProdutoRequest(JsonArray jsonArray){
    	List<ProdutoDTO> produtos = new ArrayList<ProdutoDTO>();
    	jsonArray.forEach(object -> {
			JsonObject item = (JsonObject) object;
			if(item.getInteger("fromRequest").equals(1)) {
				produtos.add(Json.decodeValue(item.encodePrettily(), ProdutoDTO.class));
			}
		});
    	return produtos;
    }
	
    public List<ProdutoDTO> listaProdutoSession(JsonArray jsonArray){
    	List<ProdutoDTO> produtos = new ArrayList<ProdutoDTO>();
    	jsonArray.forEach(object -> {
			JsonObject item = (JsonObject) object;
			if(item.getInteger("fromRequest").equals(0)) {
				produtos.add(Json.decodeValue(item.encodePrettily(), ProdutoDTO.class));
			}
		});
    	return produtos;
    }
    
    public List<ProdutoDTO> listaProdutoRepositoty(JsonArray jsonArray){
    	List<ProdutoDTO> produtos = new ArrayList<ProdutoDTO>();
    	jsonArray.forEach(object -> {
			JsonObject item = (JsonObject) object;
			produtos.add(Json.decodeValue(item.encodePrettily(), ProdutoDTO.class));
		});
    	return produtos;
    }
    
    public JsonArray jsonArrayListProdutoFormatado(List<ProdutoDTO> produtos) {
    	JsonArray toSession = new JsonArray();
    	produtos.forEach(produto ->{
    		ProdutoDTO pDto = (ProdutoDTO)produto;
    		toSession.add(new JsonObject(Json.encode(pDto)));
    	});
    	toSession.stream().forEach(object -> {
    		JsonObject item = (JsonObject)object;
    		item.remove(ProdutoEnum.FROM_REQUEST.getValor());
    		item.remove(ProdutoEnum.QUANTIDADE_ESTOQUE.getValor());
    		if(!Optional.ofNullable(item.getString(ProdutoEnum.MENSAGEM_CAMPO.getValor())).isPresent()) {
    			item.remove(ProdutoEnum.MENSAGEM_CAMPO.getValor());
    		}
    	});
    	return toSession;
    }
    
    public JsonArray jsonArrayListProdutoSomenteId(List<ProdutoDTO> produtos){
    	JsonArray produtosArray = new JsonArray();
    	produtos.forEach(produto ->{
    		ProdutoDTO pDto = (ProdutoDTO)produto;
    		produtosArray.add(new JsonObject(Json.encode(pDto)));
    	});
    	produtosArray.stream().forEach(object -> {
    		JsonObject item = (JsonObject)object;
    		item.remove("fromRequest");
    		item.remove("id_user");
    		item.remove("preco");
    		item.remove("quantidade");
    		item.remove("descricao");
    		item.remove("qtd_estoque");
    	});
    	return produtosArray;
    }
	
    public JsonArray jsonArrayListProdutoDTO(List<ProdutoDTO> produtos){
    	JsonArray produtosArray = new JsonArray();
    	produtos.stream().forEach(produto ->{
    		produtosArray.add(Json.encode(produto));
    	});
    	return produtosArray;
    }
}
