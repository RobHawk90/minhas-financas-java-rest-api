package br.com.robhawk.financas.models;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.money.Money;

@XmlRootElement
public class Parcela {

	private int id;
	private LocalDate dataVencimento;
	private LocalDate dataPagamento;
	private Money valor;
	private TipoParcela tipo;
	private boolean foiPaga;
	private int movimentacaoId;

	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlTransient
	public LocalDate getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(LocalDate dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public LocalDate getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(LocalDate dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	@XmlAttribute(name = "dataPagamento")
	public String getDataPagamentoBr() {
		if (dataPagamento == null)
			return "";

		return dataPagamento.format(ofPattern("dd/MM/yyyy"));
	}

	public void setDataPagamentoBr(String dataPagamento) {
		this.dataPagamento = dataPagamento == null || dataPagamento.isEmpty() ? null
				: parse(dataPagamento, ofPattern("dd/MM/yyyy"));
	}

	@XmlAttribute
	public double getValor() {
		return valor.getAmount().doubleValue();
	}

	public void setValor(double valor) {
		this.valor = Money.of(Conta.MOEDA_REAL, valor);
	}

	@XmlAttribute
	public TipoParcela getTipo() {
		return tipo;
	}

	public void setTipo(TipoParcela tipo) {
		this.tipo = tipo;
	}

	@XmlAttribute
	public boolean isFoiPaga() {
		return foiPaga;
	}

	public void setFoiPaga(boolean foiPaga) {
		this.foiPaga = foiPaga;
	}

	public void setDataPagamentoBanco(String dataPagamento) {
		this.dataPagamento = dataPagamento == null || dataPagamento.isEmpty() ? null : parse(dataPagamento);
	}

	@XmlTransient
	public String getDataPagamentoBanco() {
		if (this.dataPagamento == null)
			return "";

		return this.dataPagamento.format(ISO_DATE);
	}

	@XmlTransient
	public void setDataVencimentoBanco(String dataVencimento) {
		this.dataVencimento = parse(dataVencimento);
	}

	public String getDataVencimentoBanco() {
		return this.dataVencimento.format(ISO_DATE);
	}

	@Override
	public String toString() {
		return "Parcela: " + dataVencimento.toString();
	}

	@XmlAttribute
	public int getMovimentacaoId() {
		return this.movimentacaoId;
	}

	public void setMovimentacaoId(int movimentacaoId) {
		this.movimentacaoId = movimentacaoId;
	}

}
