package br.com.robhawk.financas;

import static br.com.robhawk.financas.utils.Entidade.JSON;
import static br.com.robhawk.financas.utils.Entidade.json;
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
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.utils.DatabaseHelper;
import br.com.robhawk.financas.utils.ResponseValidator;

public class ContasTest {

	private static Server server;
	private static WebTarget target;

	@BeforeClass
	public static void setUp() throws Exception {
		DatabaseHelper.constroiBancoDeTestes();
		DAO.escopoTestes();
		DAO.exibeSql(true);

		server = Servidor.constroi();
		server.start();

		target = ClientBuilder.newClient().target(Servidor.URL + "/contas").register(LoggingFilter.class);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		server.stop();
	}

	@After
	public void tearDown() {
		DatabaseHelper.deletaRegistrosDaTabela("contas");
	}

	@Test
	public void adicionaContas() {
		Response contaCorrenteResponse = target.request(JSON).post(json(new Conta("Corrente", 2000)));
		Conta contaCorrente = contaCorrenteResponse.readEntity(Conta.class);

		assertEquals(201, contaCorrenteResponse.getStatus());
		assertTrue(contaCorrente.getId() > 0);
		assertTrue(contaCorrente.getDescricao().contains("Corrente"));
	}

	@Test
	public void editaContas() {
		target.request().post(json(new Conta("Alimentação", 300)));

		Response responseAdicionada = target.request(JSON).post(json(new Conta("Teste", 0)));
		Conta conta = responseAdicionada.readEntity(Conta.class);

		conta.setDescricao("Alimentação");
		Response responseEditaComDescricaoExistente = target.request().post(json(conta));
		assertEquals(304, responseEditaComDescricaoExistente.getStatus());

		conta.setDescricao("Corrente");
		conta.somaSaldo(1500);
		Response responseEditado = target.request(JSON).post(json(conta));
		assertEquals(200, responseEditado.getStatus());

		Conta contaEditada = target.path("/" + conta.getId()).request(JSON).get(Conta.class);
		assertTrue(conta.equals(contaEditada));
	}

	@Test
	public void naoSalvaContasComMesmaDescricao() {
		// insere a conta
		target.request(JSON).post(json(new Conta("Poupanca", 10000)));

		// tenta inserir a conta com a mesma descrição
		Response response = target.request(JSON).post(json(new Conta("Poupanca", 10000)));

		assertEquals(304, response.getStatus());
	}

	@Test
	public void buscaContaPorId() {
		// insere a conta
		Response response = target.request(JSON).post(json(new Conta("Entretenimento", 200)));
		Conta contaEntretenimento = response.readEntity(Conta.class);

		// busca a conta
		Conta conta = target.path("/" + contaEntretenimento.getId()).request(JSON).get(Conta.class);
		assertTrue(conta.getDescricao().equals("Entretenimento"));
	}

	@Test
	public void removeContas() {
		// insere a conta
		Response responseContaAdicionada = target.request(JSON).post(json(new Conta("Remover", 0)));
		int idContaRemover = responseContaAdicionada.readEntity(Conta.class).getId();

		// remove a conta
		Response responseContaRemovida = target.path("/" + idContaRemover).request().delete();
		assertEquals(204, responseContaRemovida.getStatus());

		// busca a conta removida
		Response responseBuscaContaRemovida = target.path("/" + idContaRemover).request(JSON).get();
		assertEquals(404, responseBuscaContaRemovida.getStatus());
	}

	@Test
	public void listaTodasAsContas() {
		List<Conta> novasContas = new ArrayList<>();
		novasContas.add(new Conta("Corrente", 2000));
		novasContas.add(new Conta("Poupança", 10000));
		novasContas.add(new Conta("Entretenimento", 500));
		novasContas.forEach(novaConta -> target.request().post(json(novaConta)));

		List<Conta> contas = target.request(JSON).get(new GenericType<List<Conta>>() {
		});

		assertEquals(3, contas.size());
		assertTrue(contas.stream().anyMatch(conta -> conta.getDescricao().equals("Poupança"))); // verifica um dos resultados aproveitando para verificar o encoding
	}

	@Test
	public void naoSalvaContaInvalida() {
		Response response = target.request(JSON).post(json(new Conta())); // envia uma conta inválida
		ResponseValidator validator = new ResponseValidator(response);

		validator.assertBadRequest();
		validator.assertMensagemIgual("A conta deve conter uma descrição");
	}
}
