package br.com.robhawk.financas.utils;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

import br.com.robhawk.financas.models.Categoria;
import br.com.robhawk.financas.models.Conta;
import br.com.robhawk.financas.models.Movimentacao;
import br.com.robhawk.financas.models.Parcela;
import br.com.robhawk.financas.models.TipoParcela;

public class TesteConverter {

	public static void main(String[] args) throws JAXBException {
		// Creating a new employee pojo object with data
		Parcela parcela = new Parcela();
		parcela.setDataPagamento(LocalDate.now());
		parcela.setDataVencimento(LocalDate.now());
		parcela.setMovimentacaoId(1);
		parcela.setTipo(TipoParcela.MENSAL);
		parcela.setValor(20);
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setCategoria(new Categoria());
		movimentacao.setConta(new Conta());
		movimentacao.setDataBr("10/01/2016");
		movimentacao.setParcelas(Arrays.asList(parcela));
		movimentacao.setValor(200);
		
		Conta contaCorrente = new Conta("Corrente", 3000);

		Map<String, String> properties = new HashMap<>();
		// Set the Marshaller media type to JSON or XML
		properties.put(MarshallerProperties.MEDIA_TYPE, "application/json");

		// Create a JaxBContext
		JAXBContext jc = JAXBContextFactory.createContext(
				new Class[] { Conta.class, Categoria.class, Movimentacao.class, Parcela.class }, properties);

		// Create the Marshaller Object using the JaxB Context
		Marshaller marshaller = jc.createMarshaller();
		// Set it to true if you need to include the JSON root element in the JSON output
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);

		// Set it to true if you need the JSON output to formatted
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		// Marshal the employee object to JSON and print the output to console
		marshaller.marshal(contaCorrente, System.out);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		movimentacao = (Movimentacao) unmarshaller.unmarshal(new File("C:/testes/contas.json"));
		System.out.println(movimentacao.getValor());
	}
}
