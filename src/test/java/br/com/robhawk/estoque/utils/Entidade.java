package br.com.robhawk.estoque.utils;

import javax.ws.rs.client.Entity;

public class Entidade {

	public static final String JSON = "application/json;charset=ISO-8859-1";
	public static final String TEXT = "text/html;charset=ISO-8859-1";

	public static final <T> Entity<T> json(T objeto) {
		return Entity.entity(objeto, JSON);
	}
}
