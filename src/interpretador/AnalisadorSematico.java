package interpretador;

import java.util.HashMap;

public class AnalisadorSematico {
	
	//tabela de simbolos
	private HashMap <String, Variavel> tabela;
	
	private int tipoAvaliado ;
	
	public AnalisadorSematico(HashMap <String,Variavel> tabela)
	{
		this.tabela = tabela;
	}

	public boolean checarVariavelDeclarada(String lexema)
	{
		// verifia se a variavel foi adicionada na tabela
		Variavel variavel = tabela.get(lexema);
		
		//retorna true se a variavel está na tabela
		return variavel != null;
	}
	
	public void adicionaVariavel(String lexema, String tipo)
	{
		//toda varivável recém declarada tem valor zero!
		Variavel variavel = new Variavel (tipo, "0");
		
		//adicionando a nova variável na tabela
		tabela.put(lexema, variavel);
	}
	
	public void imprimeTabela(){
		
		System.out.println("LEXEMA \tTIPO\tVALOR");
		
		//iterando sobre cada linha da tabela
		for(String chave: tabela.keySet())
		{
			Variavel variavel = tabela.get(chave);
			System.out.println(chave + "\t" + variavel.getTipo()+ "\t" + variavel.getValor());
		}
	}
	public String retornaValor(String nomeVariavel) {
		Variavel variavel = tabela.get(nomeVariavel);
		return variavel.getValor();
	}
	
	public void setTipoAvaliado(int tipoAvaliado){
		this.tipoAvaliado =  tipoAvaliado;
	}
	
	private int retornaNivelPrecisao(String tipo)
	{
		if(tipo.equals("char"))
			return 0;
		if(tipo.equals("int"))
			return 1;
		if(tipo.equals("float"))
			return 2;
		
		return 3;
	}
	
	public void checarTipo(String tipo)
	{
		//encontra o nuivel de precisao do tipo passado
		int nivelTipo = retornaNivelPrecisao(tipo);
		this.tipoAvaliado = Math.max(tipoAvaliado, nivelTipo);
	}
	
	public void verificaTipoFinal(String tipo){
		int nivelTipo = retornaNivelPrecisao(tipo);
		
		if(nivelTipo < this.tipoAvaliado)
			System.out.println("ALERTA: Perda de Precisão!!!");
	}
	
	public String retornatipoVariavel (String lexema){
		Variavel variavel = tabela.get(lexema);
		return variavel.getTipo();
	}
}
