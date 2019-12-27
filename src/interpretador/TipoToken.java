package interpretador;

public enum TipoToken {
	ID,          // identificador
	INT_CONST,   // constante inteira
	CHAR_CONST,  // constante char
	FLOAT_CONST, // constante float
	DOUBLE_CONST,// contante double
	TIPO_DADO,   
	OPERADOR_ARITMETICO, 
	OPERADOR_RELACIONAL, 
	OPERADOR_LOGICO,
	INICIO_ESCOPO, // abre chaves
	FIM_ESCOPO,    // fecha chaves
	ABRE_PARENTESES,
	FECHA_PARENTESES,
	SEPARADOR_ARG,  // separador de argumentos(vírgula)
	ATRIBUICAO,     // operador de atribuição (=)
	PALAVRA_RESERVADA, // palavras reservadas (if, for, switch, case, else, do, ...)
	FIM_COMANDO,       // ponto e vérgula
	OPERADOR_INCDEC
}
