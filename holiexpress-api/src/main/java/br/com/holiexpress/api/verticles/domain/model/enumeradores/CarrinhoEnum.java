package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum CarrinhoEnum {
	
	SESSION_NAME("carrinho"),
	MENSAGEM_VAZIO("Seu carrinho está vazio. Adicione produtos."),;
	
	private final String valor;
	
	CarrinhoEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
