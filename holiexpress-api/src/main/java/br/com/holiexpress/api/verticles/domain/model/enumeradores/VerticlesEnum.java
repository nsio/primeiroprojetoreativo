package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum VerticlesEnum {
	
	CONFIG_TYPE("file"),
	CONFIG_FORMAT("json"),
	DEPLOY_PATH_WEB_VERTICLE("br.com.holiexpress.api.verticles.WebVerticle"),
	DEPLOY_PATH_USUARIO_SERVICE("br.com.holiexpress.api.verticles.domain.service.UsuarioSerivceVerticle"),
	DEPLOY_PATH_USUARIO_REPOSITORY("br.com.holiexpress.api.verticles.domain.repository.UsuarioRepositoryVerticle"),
	DEPLOY_PATH_PRODUTO_SERVICE("br.com.holiexpress.api.verticles.domain.service.ProdutoServiceVerticle"),
	DEPLOY_PATH_PRODUTO_REPOSITORY("br.com.holiexpress.api.verticles.domain.repository.ProdutoRepositoryVerticle"),
	DEPLOY_PATH_CARRINHO_SERVICE("br.com.holiexpress.api.verticles.domain.service.CarrinhoServiceVerticle"),
	DEPLOY_PATH_DATA_BASE("br.com.holiexpress.api.verticles.DataBaseVerticle");
	
	private final String valor;
	
	VerticlesEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
