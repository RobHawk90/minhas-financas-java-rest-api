package br.com.robhawk.estoque.models;

public enum TipoCategoria {

	RECEITA, DESPESA;

	public static TipoCategoria getFrom(String name) {
		switch (name.toUpperCase()) {
		case "RECEITA":
			return RECEITA;

		case "DESPESA":
			return DESPESA;

		default:
			throw new IllegalArgumentException(name + " não existe em " + TipoCategoria.class.getName());
		}
	}

}
