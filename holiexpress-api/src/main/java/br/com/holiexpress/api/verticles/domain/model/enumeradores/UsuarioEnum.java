package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum UsuarioEnum {
	
	ID("id"),
	NOME("nome"),
	EMAIL("e-mail"),
	LOGIN("login"),
	PASSWORD("password"),
	LOGIN_INVALIDO("Login inválido"),
	NENHUM_USUARIO_ENCONTRADO("Nenhum Usuário encontrado para o id informado.");
	
	private final String valor;
	
	UsuarioEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
