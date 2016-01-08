package br.com.robhawk.financas.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@XmlRootElement
public class Conta {

	private int id;
	private String descricao;
	private Money saldo;
	public static final CurrencyUnit MOEDA_REAL = CurrencyUnit.getInstance("BRL");

	public Conta(String descricao, double saldo) {
		this.descricao = descricao;
		this.saldo = Money.of(MOEDA_REAL, saldo);
	}

	public Conta() {
	}

	@XmlAttribute
	public int getId() {
		return id;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	@XmlAttribute
	public String getDescricao() {
		return descricao;
	}

	@XmlAttribute
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlAttribute
	public double getSaldo() {
		return saldo.getAmount().doubleValue();
	}

	@XmlAttribute
	public void setSaldo(double valor) {
		this.saldo = Money.of(MOEDA_REAL, valor);
	}

	public double somaSaldo(double valor) {
		saldo.plus(valor);
		return saldo.getAmount().doubleValue();
	}

	public double subtraiSaldo(double valor) {
		saldo.minus(valor);
		return saldo.getAmount().doubleValue();
	}

	public boolean temSaldoDe(double valor) {
		return saldo.isEqual(Money.of(MOEDA_REAL, valor));
	}

	@Override
	public String toString() {
		return descricao;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(descricao).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Conta) {
			Conta other = (Conta) obj;

			return new EqualsBuilder().append(this.id, other.id).append(this.descricao, other.descricao).isEquals();
		}

		return false;
	}

}
