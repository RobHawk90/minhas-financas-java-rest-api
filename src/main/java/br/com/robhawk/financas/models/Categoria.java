package br.com.robhawk.financas.models;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {

	private int id;

	@NotEmpty(message = "A categoria deve conter uma descrição")
	private String descricao;

	@NotNull(message = "A categoria deve conter um tipo")
	private TipoCategoria tipo;

	public Categoria() {
	}

	public Categoria(String descricao, TipoCategoria tipo) {
		this.descricao = descricao;
		this.tipo = tipo;
	}

	public long getId() {
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

	public TipoCategoria getTipo() {
		return tipo;
	}

	public void setTipo(TipoCategoria tipo) {
		this.tipo = tipo;
	}

	@Override
	public String toString() {
		return descricao;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(descricao).append(tipo).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Categoria) {
			Categoria other = (Categoria) obj;

			return new EqualsBuilder().append(this.id, other.id).append(this.descricao, other.descricao)
					.append(this.tipo, other.tipo).isEquals();
		}

		return false;
	}
}
