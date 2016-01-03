package br.com.robhawk.financas.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Conta {

	private int id;
	private String descricao;

	public Conta(String descricao) {
		this.descricao = descricao;
	}

	public Conta() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
