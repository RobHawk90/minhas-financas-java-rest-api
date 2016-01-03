package br.com.robhawk.financas.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.ibatis.jdbc.ScriptRunner;

import br.com.robhawk.financas.database.Conector;
import br.com.robhawk.financas.database.EscopoConexao;

public class DatabaseHelper {

	private static final Connection conexao = Conector.conecta(EscopoConexao.TESTES);

	/**
	 * Executa o script sql do diretorio src/test/resources. Tal script dropa o
	 * banco e o cria novamente.
	 */
	public static final void constroiBancoDeTestes() {
		try {
			Reader reader = new BufferedReader(new FileReader("src/test/resources/banco-testes.sql"));
			ScriptRunner scriptRunner = new ScriptRunner(conexao);

			// Desabilita a exibição do conteúdo do arquivo (sql) 
			scriptRunner.setLogWriter(null);

			scriptRunner.runScript(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void deletaRegistrosDaTabela(String tabela) {
		try {
			String sql = "DELETE FROM %s WHERE id > 0";
			PreparedStatement ps = conexao.prepareStatement(String.format(sql, tabela));
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
