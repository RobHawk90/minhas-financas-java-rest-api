package br.com.robhawk.financas.models;

public enum UnidadeTemporal {
	DIAS, MESES, ANOS;

	public static UnidadeTemporal getBy(String name) {
		name = name.toUpperCase();

		switch (name) {
		case "DIAS":
			return DIAS;

		case "MESES":
			return MESES;

		case "ANOS":
			return ANOS;
		default:
			throw new IllegalArgumentException(name + " não existe em " + UnidadeTemporal.class.getName());
		}
	}
}
