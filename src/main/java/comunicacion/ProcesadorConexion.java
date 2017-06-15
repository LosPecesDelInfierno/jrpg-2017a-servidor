package comunicacion;

import com.google.gson.Gson;

import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorConexion extends Procesador {

	public ProcesadorConexion(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaquetePersonaje paquetePersonaje = gson.fromJson(entrada, PaquetePersonaje.class);
		contextoProcesador.setPaquetePersonaje(paquetePersonaje);
		
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), (PaquetePersonaje) paquetePersonaje.clone());
		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()).clone());
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		return "";
	}

}
