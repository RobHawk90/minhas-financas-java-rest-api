package br.com.robhawk.financas.utils;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.validation.ValidationError;

public class ResponseValidator {

	private Response response;
	private final List<ValidationError> erros;

	public ResponseValidator(Response response) {
		this.response = response;

		if (response.getStatus() == BAD_REQUEST_400)
			erros = response.readEntity(new GenericType<List<ValidationError>>() {
			});
		else
			erros = new LinkedList<>();
	}

	public void assertBadRequest() {
		assertEquals(BAD_REQUEST_400, response.getStatus());
	}

	/*
	 * Caso o texto do teste esteja errado, o JUnit mostrará tudo.
	 */
	public void assertMensagemIgual(String erro) {
		assertEquals(erro, getListaMensagensErro().stream().filter(mensagem -> mensagem.equals(erro)).findFirst()
				.orElse(getTextoMensagensErro()));
	}

	public final List<String> getListaMensagensErro() {
		return erros.stream().map(ValidationError::getMessage).collect(Collectors.toList());
	}

	public final String getTextoMensagensErro() {
		final StringBuffer erro = new StringBuffer();
		getListaMensagensErro().forEach(mensagem -> erro.append(mensagem + "\n"));
		return erro.toString();
	}

}
