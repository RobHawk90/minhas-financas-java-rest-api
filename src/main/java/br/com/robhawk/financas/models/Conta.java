package br.com.robhawk.financas.models;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

@XmlRootElement
public class Conta {

	private int id;

	@NotEmpty(message = "A conta deve conter uma descrição")
	private String descricao;
	@NotNull(message = "Deve ser numérico")
	private Money saldo;
	public static final CurrencyUnit MOEDA_REAL = CurrencyUnit.getInstance("BRL");

	public Conta(String descricao, double saldo) {
		this.descricao = descricao;
		this.saldo = Money.of(MOEDA_REAL, saldo);
	}

	public Conta() {
		//this.saldo = Money.of(MOEDA_REAL, 0);
	}

	@XmlAttribute
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@XmlAttribute
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@XmlAttribute
	public double getSaldo() {
		return saldo == null ? 0 : saldo.getAmount().doubleValue();
	}

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
