package br.com.robhawk.financas;

import static br.com.robhawk.financas.utils.Entidade.JSON;
import static br.com.robhawk.financas.utils.Entidade.json;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.NO_CONTENT_204;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.filter.LoggingFilter;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.robhawk.financas.builders.MovimentacaoBuilder;
import br.com.robhawk.financas.daos.ContaDAO;
import br.com.robhawk.financas.daos.MovimentacaoDAO;
import br.com.robhawk.financas.database.DAO;
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.models.Movimentacao;
import br.com.robhawk.financas.models.Parcela;
import br.com.robhawk.financas.utils.DatabaseHelper;

public class MovimentacoesTest {

	private static Server server;
	private static WebTarget client;
	private static MovimentacaoDAO dao;

	@BeforeClass
	public static void setUpClass() throws Exception {
		DatabaseHelper.constroiBancoDeTestes();
		DAO.escopoTestes();
		DAO.exibeSql(true);

		dao = new MovimentacaoDAO();

		server = Servidor.constroi();
		server.start();

		client = ClientBuilder.newClient().target(Servidor.URL + "/movimentacoes").register(LoggingFilter.class);
	}

	@Before
	public void setUp() {
		DatabaseHelper.deletaRegistrosDaTabela("movimentacoes");
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		server.stop();
	}

	@Test
	public void salvaMovimentacoesSimplesAtualizandoSaldoDaConta() throws Exception {
		Movimentacao compraCestaBasica = new MovimentacaoBuilder("Cesta básica").comDespesa("Alimentação")
				.comValor(499.99).naData("10/01/2016").paraContaCorrente().constroi();

		Movimentacao recebimentoSalario = new MovimentacaoBuilder("Salário").comReceita("Salário").comValor(3000)
				.naData("15/01/2016").paraContaCorrente().constroi();

		Response despesaResponse = client.request(JSON).post(json(compraCestaBasica));
		Movimentacao despesa = despesaResponse.readEntity(Movimentacao.class);

		Response receitaResponse = client.request(JSON).post(json(recebimentoSalario));
		Movimentacao receita = receitaResponse.readEntity(Movimentacao.class);

		assertEquals(CREATED_201, despesaResponse.getStatus());
		assertTrue(despesa.getId() > 0);
		assertEquals(-499.99, despesa.getConta().getSaldo(), 2);
		assertEquals(CREATED_201, receitaResponse.getStatus());
		assertTrue(receita.getId() > 0);
		assertEquals(3000 - 499.99, receita.getConta().getSaldo(), 2);
	}

	@Test
	public void salvaMovimentacoesParceladasAtualizandoSaldoDaContaQuandoPagarParcelas() {
		Movimentacao compraCelular = new MovimentacaoBuilder("Celular").comDespesa("Eletrônicos").comValor(900)
				.naData("15/01/2016").paraContaCorrente().emParcelas(3).constroi();

		Response despesaResponse = client.request(JSON).post(json(compraCelular));
		Movimentacao despesa = despesaResponse.readEntity(Movimentacao.class);

		assertTrue(despesa.isEmParcelas());
		assertEquals(0, despesa.getConta().getSaldo(), 0);
		assertEquals(3, despesa.getParcelas().size());

		Parcela parcelaDespesa = despesa.getParcelas().get(0);
		parcelaDespesa.setFoiPaga(true);
		Response parcelaPagaResponse = client.path("/parcela").request(JSON).put(json(parcelaDespesa));
		Parcela parcelaPaga = parcelaPagaResponse.readEntity(Parcela.class);

		assertEquals(OK_200, parcelaPagaResponse.getStatus());
		assertTrue(parcelaPaga.isFoiPaga());

		Movimentacao movimentacao = dao.buscaPorIdParcela(parcelaPaga.getId());
		assertEquals(-300, movimentacao.getConta().getSaldo(), 0);
	}

	@Test
	public void removeMovimentacaoAtualizandoSaldo() {
		Movimentacao despesa = new MovimentacaoBuilder("Limpeza odontológica").comValor(100).comDespesa("Dentista")
				.paraContaCorrente().naData("27/01/2016").constroi();

		Response responseDespesaSalva = client.request(JSON).post(json(despesa));
		Movimentacao despesaSalva = responseDespesaSalva.readEntity(Movimentacao.class);
		assertEquals(-100, despesaSalva.getConta().getSaldo(), 0);

		Response responseDespesaRemovida = client.path("/" + despesaSalva.getId()).request(JSON).delete();
		assertEquals(NO_CONTENT_204, responseDespesaRemovida.getStatus());

		Conta conta = new ContaDAO().buscaPorId(despesa.getConta().getId());
		assertEquals(0, conta.getSaldo(), 0);
	}

	@Test
	public void buscaMovimentacaoPorId() {
		Movimentacao despesa = new MovimentacaoBuilder("Limpeza odontológica").comValor(100).comDespesa("Dentista")
				.paraContaCorrente().naData("27/01/2016").constroi();

		Response responseDespesaSalva = client.request(JSON).post(json(despesa));
		Movimentacao despesaSalva = responseDespesaSalva.readEntity(Movimentacao.class);

		Response response = client.path("/" + despesaSalva.getId()).request(JSON).get();
		Movimentacao movimentacao = response.readEntity(Movimentacao.class);
		assertEquals(OK_200, response.getStatus());
		assertTrue(movimentacao.getId() > 0);
		assertEquals(100, movimentacao.getValor(), 0);
		assertEquals("Dentista", movimentacao.getCategoria().getDescricao());
		assertEquals("Corrente", movimentacao.getConta().getDescricao());
		assertEquals("27/01/2016", movimentacao.getDataBr());
		assertEquals("Limpeza odontológica", movimentacao.getDescricao());
	}

	@Test
	public void removeMovimentacaoRemovendoParcelasAtualizandoSaldo() {
		Movimentacao compraCelular = new MovimentacaoBuilder("Celular").comDespesa("Eletrônicos").comValor(900)
				.naData("15/01/2016").paraContaCorrente().emParcelas(3).constroi();

		Response despesaResponse = client.request(JSON).post(json(compraCelular));
		compraCelular = despesaResponse.readEntity(Movimentacao.class);

		Parcela parcelaDespesa = compraCelular.getParcelas().get(0);
		parcelaDespesa.setFoiPaga(true);
		client.path("/parcela").request(JSON).put(json(parcelaDespesa));

		compraCelular = dao.buscaPor(compraCelular.getId());
		assertEquals(-300, compraCelular.getConta().getSaldo(), 0);

		client.path("/" + compraCelular.getId()).request(JSON).delete();

		Conta conta = new ContaDAO().buscaPorId(compraCelular.getConta().getId());
		assertEquals(0, conta.getSaldo(), 0);
	}
}
