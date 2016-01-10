package br.com.robhawk.financas.providers;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;

public class HeaderResponseFilter implements ContainerResponseFilter {

	public void filter(ContainerRequestContext request, ContainerResponseContext response) {
		MediaType type = response.getMediaType();

		if (type != null) {
			String contentType = type.toString();

			if (!contentType.contains("charset")) {
				contentType = contentType + ";charset=ISO-8859-1";
				response.getHeaders().putSingle("Content-Type", contentType);
			}
		}
	}
}