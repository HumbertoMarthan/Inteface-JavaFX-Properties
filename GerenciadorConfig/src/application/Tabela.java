package application;

public class Tabela {
	
	private  String descricao;
	private  String propriedade;
	private  String valor;
	private  String valorAnterior;
	
	public Tabela(String propriedade, String valorAtual,String valorAnterior,String descricao) {
		this.propriedade = propriedade;
		this.valor = valorAtual;
		this.valorAnterior = valorAnterior;
		this.descricao = descricao;
	}
	
	public Tabela(String propriedade, String valorAtual,String descricao) {
		this.propriedade = propriedade;
		this.valor = valorAtual;
		this.descricao = descricao;
	}

	public String getPropriedade() {
		return propriedade;
	}

	public void setPropriedade(String propriedade) {
		this.propriedade = propriedade;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getValorAnterior() {
		return valorAnterior;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	
	
	
}
