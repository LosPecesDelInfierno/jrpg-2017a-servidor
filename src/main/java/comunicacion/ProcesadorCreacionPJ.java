package comunicacion;

import com.google.gson.Gson;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorCreacionPJ extends Procesador {

	public ProcesadorCreacionPJ(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaquetePersonaje paquetePersonaje = gson.fromJson(entrada, PaquetePersonaje.class);
		Servidor.getConector().registrarPersonaje(paquetePersonaje);
		this.contextoProcesador.setPaquetePersonaje(paquetePersonaje);
		return gson.toJson(paquetePersonaje);
	}

}
