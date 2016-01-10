package br.com.robhawk.financas.models;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Periodo {

	private int id;

	@NotEmpty(message = "O período deve conter uma descrição")
	private String descricao;

	@NotNull(message = "O período deve conter uma unidade temporal: MES, DIA ou ANO")
	private UnidadeTemporal unidadeTemporal;

	@Min(value = 1, message = "A quantidade do período deve ser maior que zero")
	private int quantidade;

	public Periodo() {
	}

	public Periodo(String descricao, UnidadeTemporal unidadeTemporal, int quantidade) {
		this.descricao = descricao;
		this.unidadeTemporal = unidadeTemporal;
		this.quantidade = quantidade;
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

	public UnidadeTemporal getUnidadeTemporal() {
		return unidadeTemporal;
	}

	public void setUnidadeTemporal(UnidadeTemporal unidadeTemporal) {
		this.unidadeTemporal = unidadeTemporal;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	@Override
	public String toString() {
		return descricao;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(descricao).append(unidadeTemporal).append(quantidade)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Periodo) {
			Periodo other = (Periodo) obj;

			return new EqualsBuilder().append(this.id, other.id).append(this.descricao, other.descricao)
					.append(this.unidadeTemporal, other.unidadeTemporal).append(this.quantidade, other.quantidade)
					.isEquals();
		}

		return false;
	}
}
