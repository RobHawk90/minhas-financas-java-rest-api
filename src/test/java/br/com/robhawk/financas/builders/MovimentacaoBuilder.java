package br.com.robhawk.financas.builders;

import static br.com.robhawk.financas.models.TipoCategoria.DESPESA;
import static br.com.robhawk.financas.models.TipoCategoria.RECEITA;
import static br.com.robhawk.financas.models.TipoParcela.ANUAL;

import br.com.robhawk.financas.daos.CategoriaDAO;
import br.com.robhawk.financas.daos.ContaDAO;
import br.com.robhawk.financas.models.Categoria;
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.models.Movimentacao;

public class MovimentacaoBuilder {

	private final CategoriaDAO categoriaDAO;
	private final ContaDAO contaDAO;

	private Movimentacao movimentacao;

	public MovimentacaoBuilder(String descricao) {
		categoriaDAO = new CategoriaDAO();
		contaDAO = new ContaDAO();

		movimentacao = new Movimentacao();
		movimentacao.setDescricao(descricao);
	}

	public MovimentacaoBuilder paraContaCorrente() {
		Conta contaCorrente = contaDAO.buscaPor("Corrente");

		if (contaCorrente == null) {
			contaCorrente = new Conta("Corrente", 0);
			contaDAO.insere(contaCorrente);
		}

		movimentacao.setConta(contaCorrente);

		if (contaCorrente.getId() == 0)
			System.out.println("NÃO INSERIU A CONTA " + MovimentacaoBuilder.class.getSimpleName());

		return this;
	}

	public MovimentacaoBuilder comDespesa(String descricao) {
		Categoria alimentacao = categoriaDAO.buscaPor(descricao);

		if (alimentacao == null) {
			alimentacao = new Categoria(descricao, DESPESA);
			categoriaDAO.insere(alimentacao);
		}

		if (alimentacao.getId() == 0)
			System.out.println("NÃO INSERIU A CATEGORIA " + MovimentacaoBuilder.class.getSimpleName());

		movimentacao.setCategoria(alimentacao);

		return this;
	}

	public MovimentacaoBuilder comReceita(String descricao) {
		Categoria salario = categoriaDAO.buscaPor(descricao);

		if (salario == null) {
			salario = new Categoria(descricao, RECEITA);
			categoriaDAO.insere(salario);
		}

		if (salario.getId() == 0)
			System.out.println("NÃO INSERIU A CATEGORIA " + MovimentacaoBuilder.class.getSimpleName());

		movimentacao.setCategoria(salario);

		return this;
	}

	public MovimentacaoBuilder naData(String br) {
		movimentacao.setDataBr(br);

		return this;
	}

	public MovimentacaoBuilder comValor(double valor) {
		movimentacao.setValor(valor);

		return this;
	}

	public MovimentacaoBuilder emParcelas(int quantidade) {
		movimentacao.setQuantidadeParcelas(quantidade);

		return this;
	}

	public MovimentacaoBuilder emParcelasAnuais(int quantidade) {
		movimentacao.setQuantidadeParcelas(quantidade);
		movimentacao.setTipoParcela(ANUAL);

		return this;
	}

	public Movimentacao constroi() {
		System.out.println("descricao: " + movimentacao.getDescricao());
		System.out.println("data: " + movimentacao.getData());
		System.out.println("valor: " + movimentacao.getValor());
		System.out.println("categoria: " + movimentacao.getCategoria());
		System.out.println("conta: " + movimentacao.getConta());
		System.out.println("emParcelas: " + movimentacao.isEmParcelas());
		return movimentacao;
	}

}
