package br.com.robhawk.financas.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.core.Response;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Periodo;
import br.com.robhawk.financas.models.UnidadeTemporal;

public class PeriodoDAO extends DAO<Periodo> {

	@Override
	public Periodo extrai(ResultSet rs) {
		Periodo periodo = new Periodo();

		try {
			periodo.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		try {
			periodo.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
		}

		try {
			periodo.setQuantidade(rs.getInt("quantidade"));
		} catch (SQLException e) {
		}

		try {
			periodo.setUnidadeTemporal(UnidadeTemporal.getBy(rs.getString("unidadeTemporal")));
		} catch (SQLException e) {
		}

		return periodo;
	}

	public Response insereOuAtualiza(Periodo periodo) {
		if (periodo.getId() > 0)
			return atualiza(periodo);

		return insere(periodo);
	}

	public Response insere(Periodo periodo) {
		String sql = "INSERT INTO periodos(descricao, unidadeTemporal, quantidade) VALUES(?, ?, ?)";
		int idGerado = executa(sql, periodo.getDescricao(), periodo.getUnidadeTemporal().name(),
				periodo.getQuantidade());
		periodo.setId(idGerado);

		return Response.ok(periodo).status(201).build();
	}

	public Response atualiza(Periodo periodo) {
		String sql = "UPDATE periodos SET descricao = ?, unidadeTemporal = ?, quantidade = ? WHERE id = ?";
		executa(sql, periodo.getDescricao(), periodo.getUnidadeTemporal().name(), periodo.getQuantidade(),
				periodo.getId());

		return Response.ok(periodo).build();
	}

	public boolean jaExiste(Periodo periodo) {
		String sql = "SELECT COUNT(id) > 0 AND id <> ? AS jaExiste FROM periodos WHERE descricao = ?";
		ResultSet rs = query(sql, periodo.getId(), periodo.getDescricao());

		try {
			if (rs.next())
				return rs.getBoolean("jaExiste");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public List<Periodo> listaTodos() {
		String sql = "SELECT * FROM periodos ORDER BY descricao";
		return listaResultados(sql);
	}

	public Periodo buscaPor(int id) {
		String sql = "SELECT * FROM periodos WHERE id = ?";
		return buscaResultado(sql, id);
	}

	public List<Periodo> listaPor(String descricao) {
		String sql = "SELECT * FROM periodos WHERE descricao LIKE ? ORDER BY descricao";
		return listaResultados(sql, "%" + descricao + "%");
	}

	public void deleta(int id) {
		String sql = "DELETE FROM periodos WHERE id = ?";
		executa(sql, id);
	}

}
