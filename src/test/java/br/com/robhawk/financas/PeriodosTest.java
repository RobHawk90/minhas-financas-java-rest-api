package br.com.robhawk.financas;

import static br.com.robhawk.financas.models.UnidadeTemporal.ANOS;
import static br.com.robhawk.financas.models.UnidadeTemporal.DIAS;
import static br.com.robhawk.financas.models.UnidadeTemporal.MESES;
import static br.com.robhawk.financas.utils.Entidade.JSON;
import static br.com.robhawk.financas.utils.Entidade.json;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Periodo;
import br.com.robhawk.financas.utils.DatabaseHelper;
import br.com.robhawk.financas.utils.ResponseValidator;

public class PeriodosTest {

	private static Server server;
	private static WebTarget client;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseHelper.constroiBancoDeTestes();
		DAO.escopoTestes();
		DAO.exibeSql(true);

		server = Servidor.constroi();
		server.start();

		client = ClientBuilder.newClient().target(Servidor.URL + "/periodos");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		server.stop();
	}

	@After
	public void tearDown() {
		DatabaseHelper.deletaRegistrosDaTabela("periodos");
	}

	@Test
	public void adicionaPeriodo() {
		Periodo novoPeriodo = new Periodo("Bimestral", MESES, 3);
		Response response = client.request(JSON).post(json(novoPeriodo));
		Periodo periodo = response.readEntity(Periodo.class);

		assertEquals(201, response.getStatus());
		assertTrue(periodo.getId() > 0);
		assertTrue(periodo.getDescricao().equals("Bimestral"));
	}

	@Test
	public void editaPeriodo() {
		client.request().post(json(new Periodo("Anual", ANOS, 1)));

		Response responseAdicionado = client.request(JSON).post(json(new Periodo("Mes", DIAS, 30)));
		Periodo periodo = responseAdicionado.readEntity(Periodo.class);

		// tenta editar a descrição do periodo adicionado para uma que ja existe
		periodo.setDescricao("Anual");
		Response responseAtualizaComDescricaoExistente = client.request().post(json(periodo));
		assertEquals(304, responseAtualizaComDescricaoExistente.getStatus());

		// tenta editar o periodo com informações corretas
		periodo.setDescricao("Mensal");
		periodo.setUnidadeTemporal(MESES);
		periodo.setQuantidade(1);
		Response responseEditado = client.request().post(json(periodo));
		assertEquals(200, responseEditado.getStatus());

		// verifica todas as informações editadas
		Periodo periodoAtualizado = client.path("/" + periodo.getId()).request(JSON).get(Periodo.class);
		assertTrue(periodo.equals(periodoAtualizado));
	}

	@Test
	public void naoSalvaPeriodoComMesmaDescricao() {
		// adiciona o periodo
		Periodo novoPeriodo = new Periodo("Mensal", MESES, 1);
		client.request(JSON).post(json(novoPeriodo));

		// tenta adicionar o mesmo periodo
		Response response = client.request(JSON).post(json(novoPeriodo));

		assertEquals(304, response.getStatus());
	}

	@Test
	public void listaTodosOsPeriodos() {
		List<Periodo> novosPeriodos = new LinkedList<>();
		novosPeriodos.add(new Periodo("Semanal", DIAS, 7));
		novosPeriodos.add(new Periodo("Mensal", MESES, 1));
		novosPeriodos.add(new Periodo("Semestral", MESES, 6));
		novosPeriodos.add(new Periodo("Anual", ANOS, 1));
		novosPeriodos.forEach(periodo -> client.request().post(json(periodo)));

		List<Periodo> periodos = client.request(JSON).get(new GenericType<List<Periodo>>() {
		});

		assertEquals(4, periodos.size());
		assertTrue(periodos.stream().allMatch(periodo -> periodo.getId() > 0));
	}

	@Test
	public void buscaPeriodosPeloId() {
		Response response = client.request(JSON).post(json(new Periodo("Quinzenal", DIAS, 15)));
		Periodo periodoCriado = response.readEntity(Periodo.class);

		int idPeriodoBusca = client.path("/" + periodoCriado.getId()).request(JSON).get(Periodo.class).getId();

		assertEquals(periodoCriado.getId(), idPeriodoBusca);
	}

	@Test
	public void buscaPeriodosPelaDescricao() {
		client.request().post(json(new Periodo("A cada 2 mêses", MESES, 2)));

		List<Periodo> periodos = client.path("/descricao/a cada").request(JSON).get(new GenericType<List<Periodo>>() {
		});

		assertTrue(periodos.get(0).getDescricao().equals("A cada 2 mêses"));
	}

	@Test
	public void removePeriodo() {
		// adiciona o periodo
		Response responsePeriodoAdicionado = client.request(JSON).post(json(new Periodo("Diario", DIAS, 1)));
		int idPeriodo = responsePeriodoAdicionado.readEntity(Periodo.class).getId();

		// remove o periodo
		Response responsePeriodoRemovido = client.path("/" + idPeriodo).request().delete();
		assertEquals(204, responsePeriodoRemovido.getStatus());

		// tenta buscar o periodo removido
		Response responseBuscaPeriodo = client.path("/" + idPeriodo).request().get();
		assertEquals(404, responseBuscaPeriodo.getStatus());
	}

	@Test
	public void naoSalvaPeriodoInvalido() {
		Response response = client.request(JSON).post(json(new Periodo()));
		ResponseValidator validador = new ResponseValidator(response);

		validador.assertBadRequest();
		validador.assertMensagemIgual("O período deve conter uma descrição");
		validador.assertMensagemIgual("O período deve conter uma unidade temporal: MES, DIA ou ANO");
		validador.assertMensagemIgual("A quantidade do período deve ser maior que zero");
	}
}
