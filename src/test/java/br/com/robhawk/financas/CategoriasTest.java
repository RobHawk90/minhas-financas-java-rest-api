package br.com.robhawk.financas;

import static br.com.robhawk.financas.models.TipoCategoria.DESPESA;
import static br.com.robhawk.financas.models.TipoCategoria.RECEITA;
import static br.com.robhawk.financas.utils.Entidade.JSON;
import static br.com.robhawk.financas.utils.Entidade.json;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.NO_CONTENT_204;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Categoria;
import br.com.robhawk.financas.models.TipoCategoria;
import br.com.robhawk.financas.utils.DatabaseHelper;
import br.com.robhawk.financas.utils.ResponseValidator;

public class CategoriasTest {

	private static Server servidor;
	private static WebTarget client;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseHelper.constroiBancoDeTestes();
		DAO.escopoTestes();
		DAO.exibeSql(true);

		client = ClientBuilder.newClient().target(Servidor.URL + "/categorias").register(new LoggingFilter());

		servidor = Servidor.constroi();
		servidor.start();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		servidor.stop();
	}

	@After
	public void tearDown() {
		DatabaseHelper.deletaRegistrosDaTabela("categorias");
	}

	@Test
	public void adicionaCategorias() {
		Response response = client.request(JSON).post(json(new Categoria("Material Escolar", DESPESA)));
		Categoria categoriaGerada = response.readEntity(Categoria.class);

		assertEquals(201, response.getStatus());
		assertTrue(categoriaGerada.getId() > 0);
	}

	@Test
	public void editaCategorias() {
		client.request().post(json(new Categoria("Aulas", RECEITA)));

		Response responseAdicionada = client.request().post(json(new Categoria("Teste", RECEITA)));
		Categoria categoria = responseAdicionada.readEntity(Categoria.class);

		categoria.setDescricao("Aulas");
		Response responseEditaComDescricaoExistente = client.request().post(json(categoria));
		assertEquals(304, responseEditaComDescricaoExistente.getStatus());

		categoria.setDescricao("Faculdade");
		categoria.setTipo(TipoCategoria.DESPESA);
		Response responseEditada = client.request().post(json(categoria));
		assertEquals(200, responseEditada.getStatus());

		Categoria categoriaEditada = client.path("/" + categoria.getId()).request(JSON).get(Categoria.class);
		assertTrue(categoria.equals(categoriaEditada));
	}

	@Test
	public void removeCategorias() {
		Categoria transporte = client.request(JSON).post(json(new Categoria("Transporte", DESPESA)))
				.readEntity(Categoria.class);

		Response responseDelete = client.path("/" + transporte.getId()).request().delete();
		assertEquals(NO_CONTENT_204, responseDelete.getStatus());

		Response responseGet = client.path("/" + transporte.getId()).request().get();
		assertEquals(NOT_FOUND_404, responseGet.getStatus());
	}

	@Test
	public void buscaCategoriasPelaDescricao() {
		client.request().post(json(new Categoria("Entretenimento", DESPESA)));

		Categoria categoria = client.path("/descricao/Entretenimento").request().get(Categoria.class);

		assertTrue(categoria.getId() > 0);
		assertEquals("Entretenimento", categoria.getDescricao());
	}

	@Test
	public void buscaCategoriaPorId() {
		Response responseAdicionada = client.request().post(json(new Categoria("Alimentação", DESPESA)));
		long idCategoria = responseAdicionada.readEntity(Categoria.class).getId();

		Categoria categoria = client.path("/" + idCategoria).request(JSON).get(Categoria.class);
		assertEquals("Alimentação", categoria.getDescricao());
		assertTrue(categoria.getId() > 0);
	}

	@Test
	public void listaCategoriasPorTipoDespesaReceita() {
		List<Categoria> categorias = new ArrayList<>();
		categorias.add(new Categoria("Alimentação", DESPESA));
		categorias.add(new Categoria("Entretenimento", DESPESA));
		categorias.add(new Categoria("Transporte", DESPESA));
		categorias.add(new Categoria("Freelance", RECEITA));
		categorias.add(new Categoria("Salario", RECEITA));
		categorias.forEach(categoria -> client.request().post(json(categoria)));

		List<Categoria> categoriasDespesa = client.path("/tipo/despesa").request(JSON)
				.get(new GenericType<List<Categoria>>() {
				});

		List<Categoria> categoriasReceita = client.path("/tipo/receita").request(JSON)
				.get(new GenericType<List<Categoria>>() {
				});

		assertEquals(3, categoriasDespesa.size());
		assertEquals(2, categoriasReceita.size());
	}

	@Test
	public void listaTodasAsCategorias() throws Exception {
		client.request(JSON).post(json(new Categoria("Alimentação", DESPESA)));
		client.request(JSON).post(json(new Categoria("Salário", RECEITA)));

		List<Categoria> categorias = client.request(JSON).get(new GenericType<List<Categoria>>() {
		});

		assertEquals(2, categorias.size());
	}

	@Test
	public void naoSalvaCategoriaComMesmaDescricao() {
		Categoria categoria = new Categoria("Alimentação", DESPESA);
		client.request().post(json(categoria));

		Response response = client.request().post(json(categoria));

		assertEquals(304, response.getStatus());
	}

	@Test
	public void naoAdicionaCategoriaInvalida() {
		Response response = client.request(JSON).post(json(new Categoria()));
		ResponseValidator validador = new ResponseValidator(response);

		validador.assertBadRequest();
		validador.assertMensagemIgual("A categoria deve conter uma descrição");
		validador.assertMensagemIgual("A categoria deve conter um tipo");
	}
}
