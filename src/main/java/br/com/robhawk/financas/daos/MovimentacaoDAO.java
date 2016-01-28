package br.com.robhawk.financas.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Movimentacao;
import br.com.robhawk.financas.models.Parcela;

public class MovimentacaoDAO extends DAO<Movimentacao> {

	private final CategoriaDAO categoriaDAO;
	private final ContaDAO contaDAO;
	private final ParcelaDAO parcelaDAO;

	public MovimentacaoDAO() {
		categoriaDAO = new CategoriaDAO();
		contaDAO = new ContaDAO();
		parcelaDAO = new ParcelaDAO();
	}

	@Override
	public Movimentacao extrai(ResultSet rs) {
		Movimentacao movimentacao = new Movimentacao();

		try {
			movimentacao.setDataBanco(rs.getString("dataMovimentacao"));
		} catch (SQLException e) {
		}

		try {
			movimentacao.setValor(rs.getDouble("valor"));
		} catch (SQLException e1) {
		}

		try {
			movimentacao.setCategoria(categoriaDAO.buscaPor(rs.getInt("categoriaId")));
		} catch (SQLException e) {
		}

		try {
			movimentacao.setConta(contaDAO.buscaPorId(rs.getInt("contaId")));
		} catch (SQLException e) {
		}

		try {
			movimentacao.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
		}

		try {
			movimentacao.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		try {
			boolean emParcelas = rs.getBoolean("emParcelas");

			if (emParcelas) {
				List<Parcela> parcelas = parcelaDAO.listaParcelas(movimentacao.getId());
				movimentacao.setParcelas(parcelas);
			}
		} catch (SQLException e) {
		}

		return movimentacao;
	}

	public boolean insere(Movimentacao movimentacao) {
		String sql = "INSERT INTO movimentacoes(descricao, dataMovimentacao, valor, emParcelas, contaId, categoriaId) "
				+ "VALUES(?, ?, ?, ?, ?, ?)";
		int idGerado = executaInsert(sql, movimentacao.getDescricao(), movimentacao.getDataBanco(),
				movimentacao.getValor(), movimentacao.isEmParcelas(), movimentacao.getConta().getId(),
				movimentacao.getCategoria().getId());
		movimentacao.setId(idGerado);

		return sucesso(idGerado) && setSaldoDaConta(movimentacao) && parcelaDAO.insereParcelas(movimentacao);
	}

	private boolean setSaldoDaConta(Movimentacao movimentacao) {
		String sql = "SELECT saldo FROM contas WHERE id = ?";
		ResultSet rs = query(sql, movimentacao.getConta().getId());

		try {
			if (rs.next()) {
				movimentacao.getConta().setSaldo(rs.getDouble("saldo"));
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public Movimentacao buscaPor(int id) {
		String sql = "SELECT * FROM movimentacoes WHERE id = ?";
		return buscaResultado(sql, id);
	}

	public Movimentacao buscaPorIdParcela(int id) {
		String sql = "SELECT movimentacoes.* FROM movimentacoes "
				+ "INNER JOIN parcelas ON movimentacoes.id = parcelas.movimentacaoId " + "WHERE parcelas.id = ?";
		return buscaResultado(sql, id);
	}

	public boolean deleta(int id) {
		String sql = "DELETE FROM movimentacoes WHERE id = ?";
		return executa(sql, id);
	}

}
