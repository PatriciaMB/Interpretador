package interpretador;

public class Variavel {
	
	// tipo da variável
	private String tipo;
	
	//valor atual da variável
	private String valor;
	
	public Variavel(String tipo, String valor){
		 this.tipo = tipo;
		 this.valor = valor;
		 
	}

	public String getTipo() {
		return tipo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
	
	

}
