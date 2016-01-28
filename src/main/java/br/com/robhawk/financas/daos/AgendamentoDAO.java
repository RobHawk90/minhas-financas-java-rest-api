package br.com.robhawk.financas.daos;

import java.sql.ResultSet;
import java.sql.SQLException;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Agendamento;

public class AgendamentoDAO extends DAO<Agendamento> {

	private final CategoriaDAO categoriaDAO = new CategoriaDAO();
	private final PeriodoDAO periodoDAO = new PeriodoDAO();

	@Override
	public Agendamento extrai(ResultSet rs) {
		Agendamento agendamento = new Agendamento();

		try {
			agendamento.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		try {
			agendamento.setDataBanco(rs.getString("dataAgendamento"));
		} catch (SQLException e) {
		}

		try {
			agendamento.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
		}

		try {
			agendamento.setValor(rs.getDouble("valor"));
		} catch (SQLException e) {
		}

		try {
			agendamento.setCategoria(categoriaDAO.buscaPor(rs.getInt("categoriaId")));
		} catch (SQLException e) {
		}

		try {
			agendamento.setPeriodo(periodoDAO.buscaPor(rs.getInt("periodoId")));
		} catch (SQLException e) {
		}

		return agendamento;
	}

	public boolean insere(Agendamento agendamento) {
		String sql = "INSERT INTO agendamentos(dataAgendamento, descricao, valor, categoriaId, periodoId) VALUES(?, ?, ?, ?, ?)";
		int idGerado = executaInsert(sql, agendamento.getDataBanco(), agendamento.getDescricao(),
				agendamento.getValor(), agendamento.getCategoria().getId(), agendamento.getPeriodo().getId());
		agendamento.setId(idGerado);
		return sucesso(idGerado);
	}

	public boolean atualiza(Agendamento agendamento) {
		return false;
	}

	public boolean deleta(int id) {
		return false;
	}

}
