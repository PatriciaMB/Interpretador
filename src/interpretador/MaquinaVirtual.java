package interpretador;
import java.util.HashMap;
import java.util.Stack;

public class MaquinaVirtual {
	
	//composição: pilha dos operandos e operadores
	private Stack <String> pilha;
	
	//agregação: mesma tabela do analisador semântico
	private HashMap <String, Variavel> tabela;
	
	//Pilha de condições 
	private Stack <String> pilhacondicao;
	
	//Variavel utilizada para habilitar/desabilitar as ações de execução da maquina virtual
	private boolean habilita = true;
	
	private boolean condicaoAnterior = true;
	
	public MaquinaVirtual (HashMap <String,Variavel> tabela)
	{
		this.tabela = tabela;
		this.pilha = new Stack <>();
		this.pilhacondicao = new Stack <>();
	}
	
	public void empilha(Yytoken token){
		
		//se a máquina estiver desabilitada, não faça nada
		if(!habilita)
		{
			return;
		}
		
		//se o que eu estou tentando empilhar for uma constante
		if(token.getTipo() == TipoToken.INT_CONST)
		{
			//guarda o número que a representa
			pilha.push(token.getLexema());
		}
		
		//se o que eu estou tentando empilhar for um operador.
		if(token.getTipo() == TipoToken.OPERADOR_ARITMETICO)
		{
			//guardo o operador correspodente.
			pilha.push(token.getLexema());			
		}
		
		// se for um ID...
		if(token.getTipo() == TipoToken.ID)
		{
			//Primeiro, encontrar a variável na tabela de símbolos.
			Variavel variavel = tabela.get(token.getLexema());
			
			//empilhar o valor atual da variavel
			pilha.push(variavel.getValor());
			
		}
		
	}
	
	//empilha a condicao
	public void empilhaCondicao (Yytoken token)
	{
		if(!habilita)
		{
			return;
		}
		if(token.getTipo().name().equals("ID")) {
			AnalisadorSematico analisadorSemantico = new AnalisadorSematico(tabela);
			pilhacondicao.push(analisadorSemantico.retornaValor((String.valueOf(token.getLexema()))));
		}else
			pilhacondicao.push(token.getLexema());
	}
	public Boolean valorHabilita() {
		return habilita;
	}
	
	//avaliando a expressao lógica
	public void avaliar()
	{
		if(!habilita)
		{
			return;
		}
		
		//desempilhar três elementos
		//Realizar os 3 pops
		String resultadoString;
		String operando2 = pilhacondicao.pop();
		String operador = pilhacondicao.pop();
		String operando1 = pilhacondicao.pop();
		
		boolean resultadoParcial=false;
		
		if(operador.equals("||"))
		{
			resultadoParcial = Boolean.parseBoolean(operando1) || Boolean.parseBoolean(operando2);
		}
		if(operador.equals("&&"))
		{
			resultadoParcial = Boolean.parseBoolean(operando1) && Boolean.parseBoolean(operando2);
		}
		if(operador.equals("=="))
		{
			resultadoParcial = Integer.parseInt(operando1) == Integer.parseInt(operando2);
		}
		if(operador.equals("!="))
		{
			resultadoParcial = Integer.parseInt(operando1) != Integer.parseInt(operando2);
		}
		if(operador.equals(">="))
		{
			resultadoParcial = Integer.parseInt(operando1) >= Integer.parseInt(operando2);
		}
		if(operador.equals("<="))
		{
			resultadoParcial = Integer.parseInt(operando1) <= Integer.parseInt(operando2);
		}
		if(operador.equals(">"))
		{
			resultadoParcial = Integer.parseInt(operando1) > Integer.parseInt(operando2);
		}
		if(operador.equals("<"))
		{
			resultadoParcial = Integer.parseInt(operando1) < Integer.parseInt(operando2);
		}
		
		//reconverter em String 
		resultadoString = String.valueOf(resultadoParcial);
		
		//System.out.println("Resultado da avaliação " +  resultadoString);
		//reeempilhar o resultado de volta
		pilhacondicao.push(resultadoString);
		condicaoAnterior = resultadoParcial;
		//habilita = !resultadoParcial;
	}
	
	public Boolean resultadoAvaliacaoAnterior() {
		return condicaoAnterior;
	}
	
	//desempilhar
	public void calcular(){
		
		//caso a máquina estiver desabilitada, não faça nada.
		if(!habilita)
		{
			return;
		}
		
		//Realizar os 3 pops
		String resultadoString;
		String operando2 = pilha.pop();
		String operador = pilha.pop();
		String operando1 = pilha.pop();
		
		//Calcular
		int resultadoParcial = 0;
		
		if (operador.equals("+"))
		{
			resultadoParcial = Integer.parseInt(operando1) + Integer.parseInt(operando2);
		}
		if (operador.equals("-"))
		{
			resultadoParcial = Integer.parseInt(operando1) - Integer.parseInt(operando2);
		}
		if (operador.equals("*"))
		{
			resultadoParcial = Integer.parseInt(operando1) * Integer.parseInt(operando2);
		}
		if (operador.equals("/"))
		{
			resultadoParcial = Integer.parseInt(operando1) / Integer.parseInt(operando2);
		}
	
		
		resultadoString = String.valueOf(resultadoParcial);
		
		//reeempilhar o resultado
		pilha.push(resultadoString);
		pilhacondicao.push(resultadoString);
		
	}
	
	
	//retorna o último valor na pilha aritmética
	public String retornaValor()
	{
		//Se nao estiver habilida retorna nada.
		if(!habilita)
		{
			return null;
		}
		//retorna o valor final calcular.
		return pilha.pop();
	}
	
	//retorna o valor último valor na pilha de condicao
	public String retornaValorVerdade()
	{
		//Se nao estiver habilida retorna nada.
		if(!habilita)
		{
			return null;
		}
		//retorna o valor final calcular.
		return pilhacondicao.pop();
	}
	
	
	//criando um método para habilitar a máquina
	public void habilitarMaquina(boolean habilita)
	{
		this.habilita = habilita;
	}
	
	//criando um método para reabilitar a máquina
	public void reabilitar()
	{
		if(!this.habilita)
		{
			this.habilita = true;
		}
	}
	
	
}
