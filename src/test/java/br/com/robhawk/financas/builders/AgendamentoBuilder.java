package br.com.robhawk.financas.builders;

import static br.com.robhawk.financas.models.TipoCategoria.RECEITA;
import static br.com.robhawk.financas.models.UnidadeTemporal.MESES;

import br.com.robhawk.financas.models.Agendamento;
import br.com.robhawk.financas.models.Categoria;
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.models.Periodo;

public class AgendamentoBuilder {

	private Agendamento agendamento;

	public AgendamentoBuilder() {
		agendamento = new Agendamento();
	}

	public Agendamento constroi() {
		return agendamento;
	}

	public AgendamentoBuilder naData(String br) {
		agendamento.setData(br);
		return this;
	}

	public AgendamentoBuilder comDescricao(String descricao) {
		agendamento.setDescricao(descricao);
		return this;
	}

	public AgendamentoBuilder comValor(double valor) {
		agendamento.setValor(valor);
		return this;
	}

	public AgendamentoBuilder mensal() {
		agendamento.setPeriodo(new Periodo("Mensal", MESES, 1));
		return this;
	}

	public AgendamentoBuilder salario() {
		agendamento.setCategoria(new Categoria("Salário", RECEITA));
		return this;
	}

	public AgendamentoBuilder paraContaCorrente() {
		agendamento.setConta(new Conta("Corrente", 0));
		return this;
	}
}
