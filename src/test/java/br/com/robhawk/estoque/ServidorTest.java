package br.com.robhawk.estoque;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.server.Server;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServidorTest {

	private Server server;
	private WebTarget target;

	@Before
	public void setUp() throws Exception {
		server = Servidor.constroi();
		server.start();

		target = ClientBuilder.newClient().target(Servidor.URL);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
	}

	@Test
	public void servidorOnline() {
		Response response = target.path("/status-servidor").request().get();
		String status = response.readEntity(String.class);

		assertEquals(200, response.getStatus());
		assertEquals("SERVIDOR ONLINE", status);
	}

}
