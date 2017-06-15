package comunicacion;

import com.google.gson.Gson;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorMostrarMapas extends Procesador {

	public ProcesadorMostrarMapas(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaquetePersonaje paquetePersonaje = gson.fromJson(entrada, PaquetePersonaje.class);
		Servidor.log.append(paquetePersonaje.getIp() + " ha elegido el mapa " + paquetePersonaje.getMapa()
				+ System.lineSeparator());
		return "";
	}

}
