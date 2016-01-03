package br.com.robhawk.financas.database;

public enum EscopoConexao {

	PRODUCAO("jdbc:mysql://localhost:3306/minhas_financas", "root", ""),
	TESTES("jdbc:mysql://localhost:3306/minhas_financas_testes", "root", "");

	private String url;
	private String usuario;
	private String senha;

	private EscopoConexao(String url, String usuario, String senha) {
		this.url = url;
		this.usuario = usuario;
		this.senha = senha;
	}

	public String getUrl() {
		return url;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getSenha() {
		return senha;
	}

}
