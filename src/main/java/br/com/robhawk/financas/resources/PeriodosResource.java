package br.com.robhawk.financas.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import br.com.robhawk.financas.daos.PeriodoDAO;
import br.com.robhawk.financas.models.Periodo;

@Path("/periodos")
public class PeriodosResource {

	private final PeriodoDAO dao;

	public PeriodosResource() {
		dao = new PeriodoDAO();
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Response salvaPeriodo(@Valid Periodo periodo) {
		if (dao.jaExiste(periodo))
			return Response.notModified().build();

		if (periodo.getId() > 0) {
			if (dao.atualiza(periodo))
				return Response.ok(periodo).build();
		} else if (dao.insere(periodo))
			return Response.ok(periodo).status(201).build();

		return Response.serverError().build();
	}

	@DELETE
	@Path("/{id}")
	public Response removePeriodo(@Min(1) @PathParam("id") int id) {
		if (dao.deleta(id))
			return Response.noContent().build();

		return Response.serverError().build();
	}

	@GET
	@Produces(APPLICATION_JSON)
	public Response listaTodosOsPeriodos() {
		List<Periodo> periodos = dao.listaTodos();
		return Response.ok(new GenericEntity<List<Periodo>>(periodos) {
		}).build();
	}

	@GET
	@Path("/{id}")
	@Produces(APPLICATION_JSON)
	public Response buscaPeriodoPorId(@PathParam("id") int id) {
		Periodo periodo = dao.buscaPor(id);

		if (periodo == null)
			return Response.status(404).build();

		return Response.ok(periodo).build();
	}

	@GET
	@Path("/descricao/{descricao}")
	@Produces(APPLICATION_JSON)
	public Response listaPeriodosContendoDescricao(@PathParam("descricao") String descricao) {
		List<Periodo> periodos = dao.listaPor(descricao);
		return Response.ok(new GenericEntity<List<Periodo>>(periodos) {
		}).build();
	}
}
