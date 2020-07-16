package br.com.holiexpress.api.verticles.domain.model.enumeradores;

public enum PgEnum {
	
	HOST("172.25.0.1"),
	URL("jdbc:postgresql://172.25.0.1:5432/holiexpress"),
	DATA_BASE("holiexpress"),
	USER("postgres"),
	PASSWORD("somepassword");
	
	private final String valor;
	
	PgEnum(String valor) {
        this.valor = valor;
    }

	public String getValor() {
		return valor;
	}
	
}
