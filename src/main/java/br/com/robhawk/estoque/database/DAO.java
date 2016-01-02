package br.com.robhawk.estoque.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public abstract class DAO<T> {

	private final Connection conexao;
	public static EscopoConexao escopo = EscopoConexao.PRODUCAO;
	public static boolean exibirSql = false;

	public DAO() {
		conexao = Conector.conecta(escopo);
	}

	public static final void setEscopo(EscopoConexao escopo) {
		DAO.escopo = escopo;
	}

	public static final void escopoTestes() {
		escopo = EscopoConexao.TESTES;
	}

	public static final boolean exibeSql() {
		return exibirSql;
	}

	public static final void exibeSql(boolean exibirSql) {
		DAO.exibirSql = exibirSql;
	}

	public static final boolean ocultaSql() {
		return !exibirSql;
	}

	public static final void ocultaSql(boolean ocultarSql) {
		exibirSql = ocultarSql;
	}

	public abstract T extrai(ResultSet rs);

	private PreparedStatement prepara(String sql, Object... params) throws SQLException {
		PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		for (int i = 0; i < params.length; i++)
			ps.setObject(i + 1, params[i]);

		if (exibirSql)
			System.out.println(ps);

		return ps;
	}

	public int executa(String sql, Object... params) {
		try {
			PreparedStatement ps = prepara(sql, params);
			ps.execute();
			ResultSet rs = ps.getGeneratedKeys();

			if (rs.next())
				return rs.getInt("GENERATED_KEY");

			return 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	public ResultSet query(String sql, Object... params) {
		try {
			PreparedStatement ps = prepara(sql, params);
			ResultSet rs = ps.executeQuery();
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<T> listaResultados(String sql, Object... params) {
		List<T> objetos = new LinkedList<T>();

		ResultSet rs = query(sql, params);

		try {
			while (rs.next())
				objetos.add(extrai(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return objetos;
	}

	public T buscaResultado(String sql, Object... params) {
		T objeto = null;

		ResultSet rs = query(sql, params);

		try {
			if (rs.next())
				objeto = extrai(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return objeto;
	}

}
