package br.com.robhawk.financas.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import br.com.robhawk.financas.models.Conta;

public class TesteConverter {

	public static void main(String[] args) throws JAXBException {
		// Creating a new employee pojo object with data
		Conta conta = new Conta("Corrente", 2000);

		Map<String, String> properties = new HashMap<>();
		// Set the Marshaller media type to JSON or XML
		properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");

		// Create a JaxBContext
		JAXBContext jc = JAXBContextFactory.createContext(new Class[] { Conta.class }, properties);

		// Create the Marshaller Object using the JaxB Context
		Marshaller marshaller = jc.createMarshaller();
		// Set it to true if you need to include the JSON root element in the JSON output
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

		// Set it to true if you need the JSON output to formatted
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// Marshal the employee object to JSON and print the output to console
		marshaller.marshal(conta, System.out);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Conta unConta = (Conta) unmarshaller.unmarshal(new File("C:/testes/contas.json"));
		System.out.println(unConta.getSaldo());
	}
}
