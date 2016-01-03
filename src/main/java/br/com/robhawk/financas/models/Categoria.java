package br.com.robhawk.financas.models;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Categoria {

	private int id;
	private String descricao;
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
}
