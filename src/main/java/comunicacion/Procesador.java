package comunicacion;
import com.google.gson.Gson;

public abstract class Procesador {
	protected ContextoProcesador contextoProcesador;
	protected Gson gson;
	
	public Procesador(ContextoProcesador contextoProcesador, Gson gson) {
		this.contextoProcesador = contextoProcesador;
		this.gson = gson;
	}
	
	public abstract String procesar(String entrada);
}
