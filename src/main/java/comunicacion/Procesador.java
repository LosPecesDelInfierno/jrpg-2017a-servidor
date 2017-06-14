package comunicacion;
import com.google.gson.Gson;

import servidor.EscuchaCliente;

public abstract class Procesador {
	protected final Gson gson = new Gson();
	
	public abstract String procesar(String entrada);
}
