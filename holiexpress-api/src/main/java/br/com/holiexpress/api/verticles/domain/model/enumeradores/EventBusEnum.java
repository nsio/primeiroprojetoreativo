package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum EventBusEnum {
	
	USUARIO_ADD_SERVICE("user.inserir.service"),
	USUARIO_UPDATE_SERVICE("user.atualizar.service"),
	USUARIO_LIST_SERVICE("user.list.service"),
	USUARIO_BY_ID_SERVICE("user.getbyid.service"),
	USUARIO_LOGIN_SERVICE("user.login.service"),
	USUARIO_ADD_REPOSITORY("user.inserir.repository"),
	USUARIO_UPDATE_REPOSITORY("user.atualizar.repository"),
	USUARIO_LIST_REPOSITORY("user.list.repository"),
	USUARIO_BY_ID_REPOSITORY("user.getbyid.repository"),
	USUARIO_LOGIN_REPOSITORY("user.login.repository"),
	PRODUTO_ADD_SERVICE("product.inserir.service"),
	PRODUTO_UPDATE_SERVICE("product.atualizar.service"),
	PRODUTO_LIST_SERVICE("product.list.service"),
	PRODUTO_BY_ID_SERVICE("product.getbyid.service"),
	PRODUTO_ADD_REPOSITORY("product.inserir.repository"),
	PRODUTO_UPDATE_REPOSITORY("product.atualizar.repository"),
	PRODUTO_LIST_REPOSITORY("product.list.repository"),
	PRODUTO_BY_ID_REPOSITORY("product.getbyid.repository"),
	PRODUTO_LIST_IDS_REPOSITORY("product.list.nids.repository"),
	CARRINHO_ADD_SERVICE("carrinho.add.produto.service"),
	CARRINHO_EXCLUIR_SERVICE("carrinho.excluir.produto.service");
	
	private final String valor;
	
	EventBusEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
}
