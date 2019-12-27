package interpretador;

public class Yytoken {
	
	private TipoToken tipo;
	private String lexema;
	
	public Yytoken(TipoToken tipo, String lexema)
	{
		this.tipo = tipo;
		this.lexema = lexema;
	}
	
	public TipoToken getTipo()
	{
		return this.tipo;
	}
	
	public String getLexema()
	{
		return this.lexema;
	}
	
	public String toString()
	{
		return "Tipo = "+this.tipo+" , lexema = "+this.lexema;
	}
	
	
	
}
