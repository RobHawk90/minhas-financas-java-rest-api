package br.com.robhawk.estoque.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/status-servidor")
public class StatusServidorResource {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response getStatus() {
		return Response.ok("SERVIDOR ONLINE").build();
	}

}
