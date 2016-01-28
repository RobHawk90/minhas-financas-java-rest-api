package br.com.robhawk.financas.models;

import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;
import org.joda.money.Money;

@XmlRootElement
public class Agendamento {

	private int id;
	private LocalDate data;

	@NotEmpty(message = "O agendamento precisa conter a descrição da movimentação")
	private String descricao;
	private Periodo periodo = new Periodo();
	private Categoria categoria = new Categoria();
	private Conta conta = new Conta();
	private Money valor;

	public Agendamento() {
		this.valor = Money.of(Conta.MOEDA_REAL, 0);
	}

	@XmlAttribute
	public int getId() {
		return id;
	}

	@XmlElement
	public void setId(int id) {
		this.id = id;
	}

	@XmlAttribute
	public String getData() {
		return data.format(ofPattern("dd/MM/yyyy"));
	}

	@XmlElement
	public void setData(String data) {
		this.data = LocalDate.parse(data, ofPattern("dd/MM/yyyy"));
	}

	@XmlTransient
	public String getDataBanco() {
		return data.format(ISO_DATE);
	}

	@XmlTransient
	public void setDataBanco(String data) {
		this.data = LocalDate.parse(data);
	}

	@XmlAttribute
	public String getDescricao() {
		return descricao;
	}

	@XmlElement
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlTransient
	public Periodo getPeriodo() {
		return periodo;
	}

	@XmlTransient
	public void setPeriodo(Periodo periodo) {
		this.periodo = periodo;
	}

	@XmlTransient
	public Categoria getCategoria() {
		return categoria;
	}

	@XmlTransient
	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	@XmlTransient
	public Conta getConta() {
		return conta;
	}

	@XmlTransient
	public void setConta(Conta conta) {
		this.conta = conta;
	}

	@XmlAttribute
	public double getValor() {
		return valor.getAmount().doubleValue();
	}

	@XmlElement
	public void setValor(double valor) {
		this.valor = Money.of(Conta.MOEDA_REAL, valor);
	}

}
