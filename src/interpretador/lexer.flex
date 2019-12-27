package interpretador;

%%
%class Lexer
%{
	int linha = 1;
%}

%eof{
	System.out.println("Arquivo terminado, lido " + linha + " linhas");
	System.exit(0);
%eof}


//Macros
letra = [a-zA-Z]
digito = [0-9]
aritmeticos = [-*/+]
tipo = "int"|"float"|"double"|"char"
reservadas = "while"|"if"|"else"|"elseif"|"do"|"for"|"return"
operador_rel = "=="|"!="|">"|"<"|">="|"<="
operador_log = "&&"|"||"|"!"
operador_incdec = "++"|"--"


%%

"("							{ return new Yytoken(TipoToken.ABRE_PARENTESES, yytext());}

")"							{ return new Yytoken(TipoToken.FECHA_PARENTESES, yytext());}

","							{ return new Yytoken(TipoToken.SEPARADOR_ARG, yytext());}

"{"							{ return new Yytoken(TipoToken.INICIO_ESCOPO, yytext());}

"}"							{ return new Yytoken(TipoToken.FIM_ESCOPO, yytext());}

";" 						{ return new Yytoken(TipoToken.FIM_COMANDO, yytext());}

{aritmeticos}				{ return new Yytoken(TipoToken.OPERADOR_ARITMETICO, yytext());}

{reservadas}				{ return new Yytoken(TipoToken.PALAVRA_RESERVADA, yytext());}

{operador_rel}				{ return new Yytoken(TipoToken.OPERADOR_RELACIONAL, yytext());}

{operador_log}				{return new Yytoken(TipoToken.OPERADOR_LOGICO, yytext());}

{tipo}						{ return new Yytoken(TipoToken.TIPO_DADO, yytext());}

{letra}({letra}|{digito})* 	{ return new Yytoken(TipoToken.ID, yytext());}

{digito}+					{ return new Yytoken(TipoToken.INT_CONST, yytext());}

"="							{ return new Yytoken(TipoToken.ATRIBUICAO, yytext());}

"+="						{ return new Yytoken(TipoToken.ATRIBUICAO, yytext());}

{operador_incdec}			{ return new Yytoken(TipoToken.OPERADOR_INCDEC, yytext());}

\n	{
		linha++;
	}

[\r\t] 	{}
.		{}