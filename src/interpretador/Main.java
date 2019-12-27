package interpretador;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException{
		
		Parser parser = new Parser("Fonte.c");
		
		//inicia o processo deanalise sisntatica.
		parser.programa();

	}
	
	

}
