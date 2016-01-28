package br.com.robhawk.financas.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.com.robhawk.financas.daos.MovimentacaoDAO;
import br.com.robhawk.financas.daos.ParcelaDAO;
import br.com.robhawk.financas.models.Movimentacao;
import br.com.robhawk.financas.models.Parcela;

@Path("/movimentacoes")
public class MovimentacoesResource {

	private final MovimentacaoDAO dao;
	private final ParcelaDAO parcelaDAO;

	public MovimentacoesResource() {
		dao = new MovimentacaoDAO();
		parcelaDAO = new ParcelaDAO();
	}

	@POST
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	public Response salvaMovimentacao(Movimentacao movimentacao) {
		movimentacao.constroiParcelasSeNecessario();
		boolean sucesso = dao.insere(movimentacao);

		if (sucesso)
			return Response.ok(movimentacao).status(CREATED_201).build();

		return Response.serverError().build();
	}

	@PUT
	@Consumes(APPLICATION_JSON)
	@Produces(APPLICATION_JSON)
	@Path("/parcela")
	public Response atualizaParcela(Parcela parcela) {
		if (parcela.getDataPagamento() == null)
			parcela.setDataPagamento(parcela.getDataVencimento());

		boolean sucesso = parcelaDAO.atualiza(parcela);

		if (sucesso)
			return Response.ok(parcelaDAO.buscaPor(parcela.getId())).build();

		return Response.serverError().build();
	}

	@DELETE
	@Path("/{id}")
	public Response removeMovimentacao(@PathParam("id") int id) {
		if (dao.deleta(id))
			return Response.noContent().build();

		return Response.serverError().build();
	}

	@GET
	@Produces(APPLICATION_JSON)
	@Consumes(APPLICATION_JSON)
	@Path("/{id}")
	public Response buscaMovimentacaoPorId(@PathParam("id") int id) {
		Movimentacao movimentacao = dao.buscaPor(id);

		if (movimentacao == null)
			return Response.status(404).build();

		return Response.ok(movimentacao).build();
	}

}
