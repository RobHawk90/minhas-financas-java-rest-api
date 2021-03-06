package br.com.robhawk.financas.providers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

/**
 * Intercepta as respostas que est�o sendo enviadas e converte caracteres para o
 * formato compativel com pt-BR.
 * 
 * @author Robert
 */
public class HeaderResponseFilter implements ContainerResponseFilter {

	public void filter(ContainerRequestContext request, ContainerResponseContext response) {
		MediaType type = response.getMediaType();

		if (type != null) {
			String contentType = type.toString();

			if (!contentType.contains("charset")) {
				contentType = contentType + ";charset=ISO-8859-1";
				//contentType = contentType + ";charset=UTF-8";
				response.getHeaders().putSingle("Content-Type", contentType);
			}
		}

		response.getHeaders().add("Access-Control-Allow-Origin", "*");
		response.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
		response.getHeaders().add("Access-Control-Allow-Headers", "Content-Type");
	}
}