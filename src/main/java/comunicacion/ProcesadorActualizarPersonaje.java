package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;
import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorActualizarPersonaje extends Procesador {

	public ProcesadorActualizarPersonaje(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		// Se usa al finalizarse una batalla
		PaquetePersonaje paquetePersonaje = gson.fromJson(entrada, PaquetePersonaje.class);
//		contextoProcesador.setPaquetePersonaje(paquetePersonaje);
		Servidor.getConector().actualizarPersonaje(paquetePersonaje);
		
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(gson.toJson(paquetePersonaje));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return "";
	}

}
