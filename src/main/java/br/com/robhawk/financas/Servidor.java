package br.com.robhawk.financas;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

import br.com.robhawk.financas.providers.HeaderResponseFilter;

public class Servidor {
	public static final String URL = "http://localhost:8080/estoque-api";

	public static void main(String[] args) throws Exception {
		Server jettyServer = constroi();

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}
	}

	public static Server constroi() {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/estoque-api");

		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "br.com.robhawk.financas.resources");
		jerseyServlet.setInitParameter(ServerProperties.JSON_PROCESSING_FEATURE_DISABLE, "false");
		jerseyServlet.setInitParameter(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, "true");
		jerseyServlet.setInitParameter(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, "true");
		jerseyServlet.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, HeaderResponseFilter.class.getName());

		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);
		return jettyServer;
	}

}
