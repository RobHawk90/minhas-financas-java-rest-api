package br.com.robhawk.financas.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import br.com.robhawk.financas.daos.ContaDAO;
import br.com.robhawk.financas.models.Conta;

@Path("/contas")
public class ContasResource {

	private ContaDAO dao;

	public ContasResource() {
		dao = new ContaDAO();
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Response adicionaConta(Conta conta) {
		if (dao.jaExiste(conta))
			return Response.notModified().build();

		return dao.insereOuAtualiza(conta);
	}

	@DELETE
	@Path("/{id}")
	@Consumes(APPLICATION_JSON)
	public Response removeConta(@PathParam("id") int id) {
		dao.deleta(id);
		return Response.noContent().build();
	}

	@GET
	@Produces(APPLICATION_JSON)
	public Response listaContas() {
		List<Conta> contas = dao.listaTodas();
		return Response.ok(new GenericEntity<List<Conta>>(contas) {
		}).build();
	}

	@GET
	@Path("/{id}")
	@Produces(APPLICATION_JSON)
	public Response buscaContaPorId(@PathParam("id") int id) {
		Conta conta = dao.buscaPorId(id);

		if (conta == null)
			return Response.status(404).build();

		return Response.ok(conta).build();
	}
}
