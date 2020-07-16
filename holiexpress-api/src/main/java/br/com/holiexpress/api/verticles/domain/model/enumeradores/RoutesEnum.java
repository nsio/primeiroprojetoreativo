package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum RoutesEnum {
	
	CONTENT_TYPE("*/json"),
	ROOT("/holiexpress-api/*"),
	ROOT_RESTRITO("/holiexpress-api/restrito/*"),
	USUARIO_ADD("/usuarios"),
	USUARIO_UPDATE("/restrito/usuarios"),
	USUARIO_LIST("/restrito/usuarios"),
	USUARIO_BY_ID("/restrito/usuarios/:id"),
	USUARIO_LOGIN("/usuarios/login"),
	PRODUTO_ADD("/restrito/produtos"),
	PRODUTO_UPDATE("/restrito/produtos"),
	PRODUTO_LIST("/restrito/produtos"),
	PRODUTO_BY_ID("/restrito/produtos/:id"),
	CARRINHO_ADD("/restrito/carrinho"),
	CARRINHO_DELETE("/restrito/carrinho"),
	CARRINHO_VISUALIZAR("/restrito/carrinho");
	
	private final String valor;
	
	RoutesEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
