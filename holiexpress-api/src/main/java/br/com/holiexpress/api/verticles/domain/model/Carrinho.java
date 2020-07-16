package br.com.holiexpress.api.verticles.domain.model;

import java.util.List;

public class Carrinho {
	
	private List<Produto> produtos;

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}
	
}
