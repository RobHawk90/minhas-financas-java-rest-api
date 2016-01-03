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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.utils.DatabaseHelper;

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

		target = ClientBuilder.newClient().target(Servidor.URL + "/contas");
	}

	@AfterClass
	public static void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void adicionaContas() {
		Response contaCorrenteResponse = target.request(JSON).post(json(new Conta("Corrente")));
		Conta contaCorrente = contaCorrenteResponse.readEntity(Conta.class);

		assertEquals(201, contaCorrenteResponse.getStatus());
		assertTrue(contaCorrente.getId() > 0);
		assertTrue(contaCorrente.getDescricao().contains("Corrente"));
	}

	@Test
	public void naoAdicionaContasComMesmaDescricao() {
		// insere a conta
		target.request(JSON).post(json(new Conta("Poupanca")));

		// tenta inserir a conta com a mesma descrição
		Response response = target.request(JSON).post(json(new Conta("Poupanca")));

		assertEquals(304, response.getStatus());
	}

	@Test
	public void buscaContaPorId() {
		// insere a conta
		Response response = target.request(JSON).post(json(new Conta("Entretenimento")));
		Conta contaEntretenimento = response.readEntity(Conta.class);

		// busca a conta
		Conta conta = target.path("/" + contaEntretenimento.getId()).request(JSON).get(Conta.class);
		assertTrue(conta.getDescricao().equals("Entretenimento"));
	}

	@Test
	public void removeContas() {
		// insere a conta
		Response responseContaAdicionada = target.request(JSON).post(json(new Conta("Remover")));
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
		DatabaseHelper.deletaRegistrosDaTabela("contas");

		List<Conta> novasContas = new ArrayList<>();
		novasContas.add(new Conta("Corrente"));
		novasContas.add(new Conta("Poupança"));
		novasContas.add(new Conta("Entretenimento"));
		novasContas.forEach(novaConta -> target.request().post(json(novaConta)));

		List<Conta> contas = target.request(JSON).get(new GenericType<List<Conta>>() {
		});

		assertEquals(3, contas.size());
		assertTrue(contas.stream().anyMatch(conta -> conta.getDescricao().equals("Poupança"))); // verifica um dos resultados aproveitando para verificar o encoding
	}

}
