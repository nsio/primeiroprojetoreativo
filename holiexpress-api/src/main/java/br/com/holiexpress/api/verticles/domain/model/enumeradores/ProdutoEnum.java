package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum ProdutoEnum {
	
	ID("id"),
	DESCRICAO("descricao"),
	QUANTIDADE_ESTOQUE("qtd_estoque"),
	PRECO("preco"),
	ID_USER("id_user"),
	QUANTIDADE("quantidade"),
	MENSAGEM("Quantidade em estoque ultrapssada. Foi adicionado a esse produto, o máximo de quantidade em estoque."),
	MENSAGEM_CAMPO("mensagem"),
	FROM_REQUEST("fromRequest"),
	NENHUM_PRODUTO_ENCONTRADO("Nenhum Produto encontrado para o id informado.");
	
	private final String valor;
	
	ProdutoEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
