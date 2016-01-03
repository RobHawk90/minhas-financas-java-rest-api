package br.com.robhawk.financas.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conector {

	private static Connection PRODUCAO;
	private static Connection TESTES;

	public static Connection conectaEmProducao(EscopoConexao escopo) {
		if (PRODUCAO != null)
			return PRODUCAO;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			PRODUCAO = DriverManager.getConnection(escopo.getUrl(), escopo.getUsuario(), escopo.getSenha());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return PRODUCAO;
	}

	public static Connection conectaEmTestes(EscopoConexao escopo) {
		if (TESTES != null)
			return TESTES;

		try {
			Class.forName("com.mysql.jdbc.Driver");
			TESTES = DriverManager.getConnection(escopo.getUrl(), escopo.getUsuario(), escopo.getSenha());
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		return TESTES;
	}

	public static Connection conecta(EscopoConexao escopo) {
		if (escopo == EscopoConexao.PRODUCAO)
			return conectaEmProducao(escopo);

		if (escopo == EscopoConexao.TESTES)
			return conectaEmTestes(escopo);

		return null;
	}
}
