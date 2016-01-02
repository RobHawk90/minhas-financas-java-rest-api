package br.com.robhawk.estoque;

import static br.com.robhawk.estoque.models.TipoCategoria.DESPESA;
import static br.com.robhawk.estoque.models.TipoCategoria.RECEITA;
import static br.com.robhawk.estoque.utils.Entidade.JSON;
import static br.com.robhawk.estoque.utils.Entidade.json;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.robhawk.estoque.database.DAO;
import br.com.robhawk.estoque.models.Categoria;
import br.com.robhawk.estoque.utils.DatabaseHelper;

public class CategoriasTest {

	private static Server servidor;
	private static WebTarget target;
	private static int idGerado = 1;

	@BeforeClass
	public static void setUp() throws Exception {
		DAO.escopoTestes();
		DAO.exibeSql(true);
		DatabaseHelper.constroiBancoDeTestes();

		target = ClientBuilder.newClient().target(Servidor.URL);

		servidor = Servidor.constroi();
		servidor.start();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		servidor.stop();
		idGerado = 0;
	}

	@Test
	public void adicionaCategorias() {
		List<Categoria> categorias = constroiCategorias();

		categorias.forEach(categoria -> {
			Response response = target.path("/categorias").request(JSON).post(json(categoria));
			Categoria categoriaGerada = response.readEntity(Categoria.class);
			assertEquals(201, response.getStatus());
			assertEquals(idGerado++, categoriaGerada.getId());
		});

	}

	@Test
	public void buscaCategoriasPelaDescricao() {
		Categoria categoria = target.path("/categorias/descricao/Entretenimento").request().get(Categoria.class);

		assertEquals(2, categoria.getId());
		assertEquals("Entretenimento", categoria.getDescricao());
	}

	@Test
	public void buscaCategoriaPorId() {
		Categoria categoria = target.path("/categorias/1").request(JSON).get(Categoria.class);
		assertEquals("Alimentação", categoria.getDescricao());
		assertEquals(1, categoria.getId());
	}

	@Test
	public void listaCategoriasPorTipoDespesaReceita() {
		List<Categoria> categoriasDespesa = target.path("/categorias/tipo/despesa").request(JSON)
				.get(new GenericType<List<Categoria>>() {
				});

		List<Categoria> categoriasReceita = target.path("/categorias/tipo/receita").request(JSON)
				.get(new GenericType<List<Categoria>>() {
				});

		assertEquals(3, categoriasDespesa.size());
		assertEquals(2, categoriasReceita.size());
	}

	@Test
	public void jaExisteCategoria() {
		Response response = target.path("/categorias").request().post(json(new Categoria("Alimentação", DESPESA)));

		assertEquals(304, response.getStatus());
	}

	public static final List<Categoria> constroiCategorias() {
		List<Categoria> categorias = new ArrayList<>();
		categorias.add(new Categoria("Alimentação", DESPESA));
		categorias.add(new Categoria("Entretenimento", DESPESA));
		categorias.add(new Categoria("Transporte", DESPESA));
		categorias.add(new Categoria("Freelance", RECEITA));
		categorias.add(new Categoria("Salario", RECEITA));
		return categorias;
	}
}
