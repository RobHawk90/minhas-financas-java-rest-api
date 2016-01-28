package br.com.robhawk.financas.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Movimentacao;
import br.com.robhawk.financas.models.Parcela;
import br.com.robhawk.financas.models.TipoParcela;

public class ParcelaDAO extends DAO<Parcela> {

	@Override
	public Parcela extrai(ResultSet rs) {
		Parcela parcela = new Parcela();

		try {
			parcela.setDataPagamentoBanco(rs.getString("dataPagamento"));
		} catch (SQLException e) {
		}

		try {
			parcela.setDataVencimentoBanco(rs.getString("dataVencimento"));
		} catch (SQLException e) {
		}

		try {
			parcela.setFoiPaga(rs.getBoolean("foiPaga"));
		} catch (SQLException e) {
		}

		try {
			parcela.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		try {
			parcela.setMovimentacaoId(rs.getInt("movimentacaoId"));
		} catch (SQLException e) {
		}

		try {
			parcela.setTipo(TipoParcela.getFrom(rs.getString("tipo")));
		} catch (SQLException e) {
		}

		try {
			parcela.setValor(rs.getDouble("valor"));
		} catch (SQLException e) {
		}

		return parcela;
	}

	public boolean insere(Parcela parcela) {
		String sql = "INSERT INTO parcelas(dataVencimento, dataPagamento, valor, tipo, foiPaga, movimentacaoId) "
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		int idGerado = executaInsert(sql, parcela.getDataVencimento(), parcela.getDataPagamento(), parcela.getValor(),
				parcela.getTipo().name(), parcela.isFoiPaga(), parcela.getMovimentacaoId());
		parcela.setId(idGerado);
		return sucesso(idGerado);
	}

	public List<Parcela> listaParcelas(int movimentacaoId) {
		String sql = "SELECT * FROM parcelas WHERE movimentacaoId = ?";
		return listaResultados(sql, movimentacaoId);
	}

	public boolean insereParcelas(Movimentacao movimentacao) {
		boolean sucesso = true;

		for (Parcela parcela : movimentacao.getParcelas()) {
			parcela.setMovimentacaoId(movimentacao.getId());
			sucesso = sucesso && insere(parcela);
		}

		return sucesso;
	}

	public boolean atualiza(Parcela parcela) {
		String sql = "UPDATE parcelas SET foiPaga = ?, dataPagamento = ?, valor = ? WHERE id = ?";
		return executa(sql, parcela.isFoiPaga(), parcela.getDataPagamento(), parcela.getValor(), parcela.getId());
	}

	public Parcela buscaPor(int id) {
		String sql = "SELECT * FROM parcelas WHERE id = ?";
		return buscaResultado(sql, id);
	}

}
