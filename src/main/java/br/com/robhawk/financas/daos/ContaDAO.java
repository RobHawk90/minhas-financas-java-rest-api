package br.com.robhawk.financas.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.core.Response;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Conta;

public class ContaDAO extends DAO<Conta> {

	@Override
	public Conta extrai(ResultSet rs) {
		Conta conta = new Conta();

		try {
			conta.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
		}

		try {
			conta.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		return conta;
	}

	public Response insereOuAtualiza(Conta conta) {
		if (conta.getId() > 0)
			return atualiza(conta);
		else
			return insere(conta);
	}

	public Response atualiza(Conta conta) {
		String sql = "UPDATE contas SET descricao = ? WHERE id = ?";
		executa(sql, conta.getDescricao(), conta.getId());

		return Response.ok(conta).build();
	}

	public Response insere(Conta conta) {
		String sql = "INSERT INTO contas(descricao) VALUES(?)";
		int idGerado = executa(sql, conta.getDescricao());
		conta.setId(idGerado);

		return Response.ok(conta).status(201).build();
	}

	public boolean jaExiste(Conta conta) {
		String sql = "SELECT COUNT(id) > 0 AND id <> ? AS jaExiste FROM contas WHERE descricao = ?";
		ResultSet rs = query(sql, conta.getId(), conta.getDescricao());

		try {
			if (rs.next())
				return rs.getBoolean("jaExiste");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public void deleta(int id) {
		String sql = "DELETE FROM contas WHERE id = ?";
		executa(sql, id);
	}

	public Conta buscaPorId(int id) {
		String sql = "SELECT * FROM contas WHERE id = ?";
		return buscaResultado(sql, id);
	}

	public List<Conta> listaTodas() {
		String sql = "SELECT * FROM contas ORDER BY descricao";
		return listaResultados(sql);
	}
}
