package comunicacion;

import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorCreacionPJ extends Procesador {

	@Override
	public String procesar(String entrada) {
		PaquetePersonaje paquetePersonaje = gson.fromJson(entrada, PaquetePersonaje.class);
		Servidor.getConector().registrarPersonaje(paquetePersonaje);
		return gson.toJson(paquetePersonaje);
	}

}
