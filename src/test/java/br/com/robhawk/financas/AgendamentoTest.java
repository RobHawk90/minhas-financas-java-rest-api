//package br.com.robhawk.financas;
//
//import static br.com.robhawk.financas.utils.Entidade.json;
//
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//
//import org.eclipse.jetty.server.Server;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import br.com.robhawk.financas.builders.AgendamentoBuilder;
//import br.com.robhawk.financas.database.DAO;
//import br.com.robhawk.financas.models.Agendamento;
//import br.com.robhawk.financas.utils.DatabaseHelper;
//import br.com.robhawk.financas.utils.Entidade;
//
//public class AgendamentoTest {
//
//	private static Server server;
//	private static WebTarget client;
//
//	@BeforeClass
//	public static void setUpClass() throws Exception {
//		DatabaseHelper.constroiBancoDeTestes();
//		DAO.escopoTestes();
//		DAO.exibeSql(true);
//
//		client = ClientBuilder.newClient().target(Servidor.URL + "/agendamentos");
//
//		server = Servidor.constroi();
//		server.start();
//	}
//
//	@After
//	public void setUp() {
//		DatabaseHelper.deletaRegistrosDaTabela("agendamentos");
//	}
//
//	@AfterClass
//	public void tearDownClass() throws Exception {
//		server.stop();
//	}
//
//	@Test
//	public void salvaAgendamento() {
//		new AgendamentoBuilder().salario().mensal().comValor(2500).naData("10").paraContaCorrente();
//		client.request().post(json());
//	}
//}
