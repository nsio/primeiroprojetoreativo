package br.com.holiexpress.api.util;

import java.util.stream.Collectors;

import br.com.holiexpress.api.verticles.domain.model.enumeradores.ProdutoEnum;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class SqlUtil {
	
	private static SqlUtil INSTANCE;
    
    private SqlUtil() {}
     
    public static SqlUtil getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SqlUtil();
        }
        return INSTANCE;
    }
	
    public String queryToVariosIds(JsonArray jsonArray, String query){
		String retorno = "";
		String ids = jsonArray.stream()
				 			  .map(id -> ((JsonObject)id).getInteger(ProdutoEnum.ID.getValor()).toString())
				 			  .collect(Collectors.joining(", "));
		retorno = query.replace(":ids", ids);
		return retorno;
	}
    
}
