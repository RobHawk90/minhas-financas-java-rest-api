package br.com.robhawk.financas.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import br.com.robhawk.financas.daos.CategoriaDAO;
import br.com.robhawk.financas.models.Categoria;
import br.com.robhawk.financas.models.TipoCategoria;

@Path("/categorias")
public class CategoriasResource {

	private CategoriaDAO dao;

	public CategoriasResource() {
		dao = new CategoriaDAO();
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Response adiciona(Categoria categoria) {
		if (dao.jaExiste(categoria))
			return Response.notModified().build();

		dao.insere(categoria);
		return Response.ok(categoria).status(201).build();
	}

	@GET
	@Path("/descricao/{descricao}")
	@Produces(APPLICATION_JSON)
	public Response buscaPelaDescricao(@PathParam("descricao") String descricao) {
		Categoria categoria = dao.buscaPela(descricao);
		return Response.ok(categoria).build();
	}

	@GET
	@Path("/tipo/{tipo}")
	@Produces(APPLICATION_JSON)
	public Response buscaPorTipo(@PathParam("tipo") String nome) {
		List<Categoria> categorias = dao.listaPor(TipoCategoria.getFrom(nome));
		return Response.ok(new GenericEntity<List<Categoria>>(categorias) {
		}).build();
	}

	@GET
	@Path("/{id}")
	@Produces(APPLICATION_JSON)
	public Response buscaPorId(@PathParam("id") long id) {
		Categoria categoria = dao.buscaPor(id);
		return Response.ok(categoria).build();
	}

}
