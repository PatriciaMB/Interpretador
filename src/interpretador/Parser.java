package interpretador;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Parser {
	
	private Lexer lexer;
	private Yytoken lookAhead;
	private AnalisadorSematico analisadorSemantico;
	private MaquinaVirtual maquinaVirtual;
	private HashMap <String, Variavel> tabela;
	
	public Parser (String nomeArquivo){
		try{
			tabela = new HashMap<>();
			
			//Inicializando o lexer
			lexer = new Lexer(new FileReader(nomeArquivo));
			//Ler o primeiro Token
			lookAhead = lexer.yylex();
			
			//inicializando o analizador Semântico
			analisadorSemantico = new AnalisadorSematico(tabela);
			
			//Inicializando a máquina virtual
			maquinaVirtual = new MaquinaVirtual (tabela);
			
		}catch(IOException ex){
			System.out.println("Erro ao abrir o arquivo!");
		}
	}
	
	private void match(TipoToken esperado){
		try{
			// se o que eu li (lookahead)for o que estou esperando...
			if(lookAhead.getTipo() == esperado)
			{
				//continue, leia o próximo token.
				lookAhead = lexer.yylex();
			}
			else
			{
				//caso contrário, erro sintático
				System.out.println("Erro sintatico, esperado: "+esperado+ ", lido: "+lookAhead.getTipo());
				
				//encerra o programa
				System.exit(0);
			}
		} catch(IOException ex){
			System.out.println("Erro ao ler o arquivo");
		}
	}
	
	// analise da grámatica da linguagem
	public void programa(){
		//int
		match(TipoToken.TIPO_DADO);
		//main
		match(TipoToken.ID);
		// ()
		match(TipoToken.ABRE_PARENTESES);
		match(TipoToken.FECHA_PARENTESES);
		
		//{
		match(TipoToken.INICIO_ESCOPO);
		
		//nao terminal
		corpo();
		
		//impressao do estado final da tabela 
		analisadorSemantico.imprimeTabela();
		
		// }
		match(TipoToken.FIM_ESCOPO);
	}
	
	private void corpo(){
		declaracao();
		
		//impressao do estado final da tabela 
		analisadorSemantico.imprimeTabela();
		
		corpoCMD();
	}
	
	private void declaracao(){
		if(lookAhead.getTipo() == TipoToken.TIPO_DADO)	{
			//Salvo o tipo, antes do método "match"
			String tipo = lookAhead.getLexema();
			match (TipoToken.TIPO_DADO);
			 
			//A variável não existe?
			
			if(!analisadorSemantico.checarVariavelDeclarada(lookAhead.getLexema()))
			{

				//Adicionar na tabela!
				analisadorSemantico.adicionaVariavel(lookAhead.getLexema(),tipo);
			}
			else
			{
				System.out.println("Variável previamente declarada!!!" + lookAhead.getLexema());
				System.exit(0);
			}
			match(TipoToken.ID);
			listaVar(tipo);
		}
	}
	
	private void corpoCMD(){
		if(lookAhead.getTipo() ==TipoToken.ID)
		{
			
			//iniciando analise de tipos 
			String tipo = analisadorSemantico.retornatipoVariavel(lookAhead.getLexema());
			
			//resetar o tipo avaliado.
			analisadorSemantico.setTipoAvaliado(0);
			
			//"salvando a variável que modificará seu resultado"
			Variavel variavel = tabela.get(lookAhead.getLexema());
						
			match(TipoToken.ID);
			match(TipoToken.ATRIBUICAO);
			expressao();
			
//			System.out.println("Valor final = "+maquinaVirtual.retornaValor());
			if(maquinaVirtual.resultadoAvaliacaoAnterior()== true && maquinaVirtual.valorHabilita() == true) {
				//atribuir o resultado final na variavel
			    variavel.setValor(maquinaVirtual.retornaValor());
			}
			else if (maquinaVirtual.resultadoAvaliacaoAnterior()== false && maquinaVirtual.valorHabilita() == true) {
				variavel.setValor(maquinaVirtual.retornaValor());
			}
			
			
			//analisadorSemantico.imprimeTabela();
			//verificar se ocorreu perdad e precisao
			analisadorSemantico.verificaTipoFinal(tipo);
			
			match(TipoToken.FIM_COMANDO);
			comandoBloco();
			corpoCMD();
			
		}
	}
	
	private void comandoBloco(){
			
		if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("if"))
		{	//if
			match(TipoToken.PALAVRA_RESERVADA);
			// (
			match(TipoToken.ABRE_PARENTESES);
			
			//
				condicao();
			
			// )
			match(TipoToken.FECHA_PARENTESES);
			
			//{
			match(TipoToken.INICIO_ESCOPO);
			
				corpoCMD();
			
			// }
			if(lookAhead.getTipo() == TipoToken.FIM_ESCOPO)
			{
				match(TipoToken.FIM_ESCOPO);
								
				if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("else"))
				{
					//Desabilita a MV de acordo com o resultado anterior da avaliação
					maquinaVirtual.habilitarMaquina(!maquinaVirtual.resultadoAvaliacaoAnterior());
					Else();
				}
			}
				
			
			//String retornaValor = maquinaVirtual.retornaValorVerdade();
			//System.out.println(retornaValor);
		}
		else if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("while"))
		{	//while
			match(TipoToken.PALAVRA_RESERVADA);
			// (
			match(TipoToken.ABRE_PARENTESES);
			
				condicao();
			
			// )
			match(TipoToken.FECHA_PARENTESES);
			
			//{
			match(TipoToken.INICIO_ESCOPO);
			
				corpoCMD();
			
			// }
			match(TipoToken.FIM_ESCOPO);
			
							
		}
		else if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("do"))	{
				
				//do
				match(TipoToken.PALAVRA_RESERVADA);
				
				//{
				match(TipoToken.INICIO_ESCOPO);
				
					corpoCMD();
				
				// }
				match(TipoToken.FIM_ESCOPO);
				
				if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("while"))
				{
					// while
					match(TipoToken.PALAVRA_RESERVADA);
					
					// (
					match(TipoToken.ABRE_PARENTESES);
					
						condicao();
				
					//)
					match(TipoToken.FECHA_PARENTESES);
					
					match(TipoToken.FIM_COMANDO);
				}
				
											
			}
		else{
			
			if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("for"))
			{
				//for
				match(TipoToken.PALAVRA_RESERVADA);
				//(
				match(TipoToken.ABRE_PARENTESES);					
				//Adicionar na tabela!
				String tipo = lookAhead.getLexema();
				// tipo int
				match(TipoToken.TIPO_DADO);
				// variavel
				analisadorSemantico.adicionaVariavel(lookAhead.getLexema(),tipo);
				match(TipoToken.ID);			
				// = 			
				match(TipoToken.ATRIBUICAO);	
				
				// valor atribuido a variavel
				if(lookAhead.getTipo() == TipoToken.INT_CONST)
					match(TipoToken.INT_CONST);
				
				//analisadorSemantico.imprimeTabela();		
				// ;
				match(TipoToken.FIM_COMANDO);
				
				condicao();	
				// ;
				match(TipoToken.FIM_COMANDO);		
				// i++
				incremento(true);
				//)
				match(TipoToken.FECHA_PARENTESES);			
				//{
				match(TipoToken.INICIO_ESCOPO);	
				corpoCMD();	
				// }
				match(TipoToken.FIM_ESCOPO);	
				
			}
		}
	}
	
	private void condicao(){

		relacional();
		
		
		if(lookAhead.getTipo() == TipoToken.OPERADOR_LOGICO)
		{
			match(TipoToken.OPERADOR_LOGICO);
			condicao();
		}
		maquinaVirtual.avaliar();
		

	}
	
	private void relacional(){
		maquinaVirtual.empilhaCondicao(lookAhead);
		expressao();
		
		if(lookAhead.getTipo() == TipoToken.OPERADOR_RELACIONAL)
		{
			maquinaVirtual.empilhaCondicao(lookAhead);
			match(TipoToken.OPERADOR_RELACIONAL);
			maquinaVirtual.empilhaCondicao(lookAhead);
			expressao();
			
		}		
		
	}
		
	private void Else(){
		//Else -> else {corpocmd} | Vazio
		
		if(lookAhead.getTipo() == TipoToken.PALAVRA_RESERVADA && lookAhead.getLexema().equals("else"))
		{
			//else
			match(TipoToken.PALAVRA_RESERVADA);
			//{
			match(TipoToken.INICIO_ESCOPO);
			
			corpoCMD();
			
			// }
			match(TipoToken.FIM_ESCOPO);	
			
			maquinaVirtual.reabilitar();
		}
		
	}
	
	private void incremento(Boolean isFor){

		if(lookAhead.getTipo() == TipoToken.ID)
		{
			match(TipoToken.ID);
			if(isFor && lookAhead.getTipo() == TipoToken.OPERADOR_ARITMETICO) {
				//Caso especial para o for
				match(TipoToken.OPERADOR_ARITMETICO);
				match(TipoToken.INT_CONST);
				//analisadorSemantico.imprimeTabela();
			}
			else if(lookAhead.getTipo() == TipoToken.OPERADOR_INCDEC && (lookAhead.getLexema().equals("++")|| lookAhead.getLexema().equals("--")))
			{
				match(TipoToken.OPERADOR_INCDEC);
				//analisadorSemantico.imprimeTabela();
			}
			else
			{
				if(lookAhead.getTipo() == TipoToken.OPERADOR_ARITMETICO && (lookAhead.getLexema().equals("+")|| lookAhead.getLexema().equals("-")))
				{
					match(TipoToken.OPERADOR_ARITMETICO);
					match(TipoToken.ID);
					//analisadorSemantico.imprimeTabela();
				}
			}
			
		}		
	}
		
	private void expressao(){
		termo();
		if(lookAhead.getTipo() == TipoToken.OPERADOR_ARITMETICO && (lookAhead.getLexema().equals("+")|| lookAhead.getLexema().equals("-")))
		{
			//"push" do terminal.
			maquinaVirtual.empilha(lookAhead);
			
			match(TipoToken.OPERADOR_ARITMETICO);
			expressao();
			
			//"pop" dos três operandos
			maquinaVirtual.calcular();
		
		}
	}
	
	private void termo(){
		
		fator();
		if(lookAhead.getTipo() == TipoToken.OPERADOR_ARITMETICO && (lookAhead.getLexema().equals("*") || lookAhead.getLexema().equals("/")))
		{
			//"push" do terminal
			maquinaVirtual.empilha(lookAhead);
			
			match(TipoToken.OPERADOR_ARITMETICO);
			termo();
			
			//"pop" da expressão
			maquinaVirtual.calcular();
		}
	}
	
	private void fator(){
		if(lookAhead.getTipo() == TipoToken.ID)
		{
			//Verificar o tipo da variável
			String tipo = analisadorSemantico.retornatipoVariavel(lookAhead.getLexema());
			analisadorSemantico.checarTipo(tipo);
			
			//"push" do terminal
			maquinaVirtual.empilha(lookAhead);
			
			match(TipoToken.ID);
		}
		else if(lookAhead.getTipo() == TipoToken.INT_CONST)
		{
			//"push" do terminal
			maquinaVirtual.empilha(lookAhead);
			
			match(TipoToken.INT_CONST);
		}
		else
		{
			match(TipoToken.ABRE_PARENTESES);
			expressao();
			match(TipoToken.FECHA_PARENTESES);
		}
	}
	
	private void listaVar(String tipo){
		if(lookAhead.getTipo() == TipoToken.FIM_COMANDO)
		{
			match(TipoToken.FIM_COMANDO);
			declaracao();
		}
		else
		{
			match(TipoToken.SEPARADOR_ARG);
			
			// a variavel nao existe?
			if(!analisadorSemantico.checarVariavelDeclarada(lookAhead.getLexema())){
				// adicionar na tabela
				analisadorSemantico.adicionaVariavel(lookAhead.getLexema(),tipo);
			}
			else
			{
				System.out.println("Variavel previamente declarada!!!!" + lookAhead.getLexema());
				System.exit(0);
			}
			match(TipoToken.ID);
			listaVar(tipo);
		}
	}

}
