package br.com.robhawk.estoque.daos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import br.com.robhawk.estoque.database.DAO;
import br.com.robhawk.estoque.models.Categoria;
import br.com.robhawk.estoque.models.TipoCategoria;

public class CategoriaDAO extends DAO<Categoria> {

	@Override
	public Categoria extrai(ResultSet rs) {
		Categoria categoria = new Categoria();

		try {
			categoria.setDescricao(rs.getString("descricao"));
		} catch (SQLException e) {
		}

		try {
			categoria.setId(rs.getInt("id"));
		} catch (SQLException e) {
		}

		try {
			categoria.setTipo(TipoCategoria.getFrom(rs.getString("tipo")));
		} catch (SQLException e) {
		}

		return categoria;
	}

	public void insereOuAtualiza(Categoria categoria) {
		if (categoria.getId() > 0)
			atualiza(categoria);
		else
			insere(categoria);
	}

	public void atualiza(Categoria categoria) {
		String sql = "UPDATE categorias SET descricao = ?, tipo = ? WHERE id = ?";
		executa(sql, categoria.getDescricao(), categoria.getTipo().name(), categoria.getId());
	}

	public void insere(Categoria categoria) {
		String sql = "INSERT INTO categorias(descricao, tipo) VALUES(?, ?)";
		int idGerado = executa(sql, categoria.getDescricao(), categoria.getTipo().name());
		categoria.setId(idGerado);
	}

	public void delete(int id) {
		String sql = "DELETE FROM categorias WHERE id = ?";
		executa(sql, id);
	}

	public Categoria buscaPela(String descricao) {
		String sql = "SELECT * FROM categorias WHERE descricao = ?";
		return buscaResultado(sql, descricao);
	}

	public List<Categoria> listaPor(TipoCategoria tipo) {
		String sql = "SELECT * FROM categorias WHERE tipo = ?";
		return listaResultados(sql, tipo.name());
	}

	public Categoria buscaPor(long id) {
		String sql = "SELECT * FROM categorias WHERE id = ?";
		return buscaResultado(sql, id);
	}

	public boolean jaExiste(Categoria categoria) {
		String sql = "SELECT COUNT(id) > 0 AS jaExiste FROM categorias WHERE descricao = ?";
		ResultSet rs = query(sql, categoria.getDescricao());

		try {
			if (rs.next())
				return rs.getBoolean("jaExiste");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

}
