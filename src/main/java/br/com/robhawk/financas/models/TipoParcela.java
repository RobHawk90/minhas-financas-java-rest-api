package br.com.robhawk.financas.models;

public enum TipoParcela {

	MENSAL, ANUAL;

	public static TipoParcela getFrom(String nome) {
		switch (nome) {
		case "MENSAL":
			return MENSAL;
		case "ANUAL":
			return ANUAL;

		default:
			throw new IllegalArgumentException(nome + " não existe em " + TipoParcela.class.getName());
		}
	}

}
