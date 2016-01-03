package br.com.robhawk.financas;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

public class Servidor implements ContainerResponseFilter {
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

		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);
		return jettyServer;
	}

	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
		MediaType type = response.getMediaType();

		if (type != null) {
			String contentType = type.toString();

			if (!contentType.contains("charset")) {
				contentType = contentType + ";charset=utf-8";
				response.getHeaders().putSingle("Content-Type", contentType);
			}
		}
	}
}
