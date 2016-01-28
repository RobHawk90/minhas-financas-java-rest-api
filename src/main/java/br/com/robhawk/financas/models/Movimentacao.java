package br.com.robhawk.financas.models;

import static br.com.robhawk.financas.models.Conta.MOEDA_REAL;
import static br.com.robhawk.financas.models.TipoParcela.MENSAL;
import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.time.format.DateTimeFormatter.ofPattern;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.joda.money.Money;

@XmlRootElement
public class Movimentacao {

	private int id;
	private String descricao;
	private LocalDate data;
	private Money valor;
	private boolean emParcelas;
	private Conta conta;
	private Categoria categoria;

	private TipoParcela tipoParcela = MENSAL;
	private int quantidadeParcelas = 1;
	private List<Parcela> parcelas = new LinkedList<>();

	public Movimentacao() {
		this.valor = Money.of(MOEDA_REAL, 0);
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
	public String getDescricao() {
		return descricao;
	}

	@XmlElement
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlTransient
	public LocalDate getData() {
		return data;
	}

	@XmlTransient
	public String getDataBanco() {
		return data.format(ISO_DATE);
	}

	@XmlAttribute(name = "data")
	public String getDataBr() {
		return data.format(ofPattern("dd/MM/yyyy"));
	}

	@XmlElement(name = "data")
	public void setDataBr(String data) {
		this.data = parse(data, ofPattern("dd/MM/yyyy"));
	}

	@XmlTransient
	public void setDataBanco(String data) {
		this.data = parse(data);
	}

	@XmlAttribute
	public double getValor() {
		return valor.getAmount().doubleValue();
	}

	@XmlElement
	public void setValor(double valor) {
		this.valor = Money.of(Conta.MOEDA_REAL, valor);
	}

	@XmlTransient
	public boolean isEmParcelas() {
		return emParcelas;
	}

	@XmlAttribute
	public int getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	@XmlElement
	public void setQuantidadeParcelas(int quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
		this.emParcelas = quantidadeParcelas > 1;
	}

	@XmlAttribute
	public TipoParcela getTipoParcela() {
		return tipoParcela;
	}

	@XmlElement
	public void setTipoParcela(TipoParcela tipoParcela) {
		this.tipoParcela = tipoParcela;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public List<Parcela> getParcelas() {
		return parcelas;
	}

	public void setParcelas(List<Parcela> parcelas) {
		this.parcelas = parcelas;
	}

	/**
	 * Se for uma nova movimentação e a quantidade de parcelas for indicada como
	 * maior que 1, o valor da movimentação é dividido pelo número de parcelas e
	 * colocado em cada uma para a data do próximo período.
	 */
	@XmlTransient
	public void constroiParcelasSeNecessario() {
		if (isEmParcelas() && getData() != null && parcelas.isEmpty()) {
			double valorDeCadaParcela = getValor() / getQuantidadeParcelas();
			List<Parcela> parcelas = new LinkedList<>();

			for (int i = 0; i < getQuantidadeParcelas(); i++) {
				Parcela parcela = new Parcela();
				parcela.setValor(valorDeCadaParcela);
				parcela.setDataVencimento(getData().plusMonths(i + 1));
				parcela.setTipo(getTipoParcela());
				parcelas.add(parcela);
			}

			setParcelas(parcelas);
		}
	}
}
