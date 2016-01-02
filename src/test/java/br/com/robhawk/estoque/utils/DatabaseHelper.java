package br.com.robhawk.estoque.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;

import org.apache.ibatis.jdbc.ScriptRunner;

import br.com.robhawk.estoque.database.Conector;
import br.com.robhawk.estoque.database.DAO;
import br.com.robhawk.estoque.database.EscopoConexao;

public class DatabaseHelper {

	/**
	 * Executa o script sql do diretorio src/test/resources. Tal script dropa o
	 * banco e o cria novamente.
	 */
	public static final void constroiBancoDeTestes() {
		try {
			Reader reader = new BufferedReader(new FileReader("src/test/resources/banco-testes.sql"));
			Connection conexao = Conector.conecta(EscopoConexao.TESTES);
			ScriptRunner scriptRunner = new ScriptRunner(conexao);

			if (DAO.ocultaSql())
				scriptRunner.setLogWriter(null);

			scriptRunner.runScript(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
