package comunicacion;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorActualizarPersonaje extends Procesador {

	@Override
	public String procesar(String entrada) {
		// Se usa al finalizarse una batalla
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.ACTUALIZARPERSONAJE);
		PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson.fromJson(entrada, PaquetePersonaje.class);
		Servidor.getConector().actualizarPersonaje(paquetePersonaje);
		
		Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), paquetePersonaje);

		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			try {
				conectado.getSalida().writeObject(gson.toJson(paquetePersonaje));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return gson.toJson(respuesta);
	}

}
