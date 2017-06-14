package comunicacion;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorMostrarMapas extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.MOSTRARMAPAS);
		// Indico en el log que el usuario se conecto a ese mapa
		PaquetePersonaje paquetePersonaje = (PaquetePersonaje) gson.fromJson(entrada, PaquetePersonaje.class);
		//Servidor.log.append(socket.getInetAddress().getHostAddress() + " ha elegido el mapa " + paquetePersonaje.getMapa() + System.lineSeparator());
		Servidor.log.append(paquetePersonaje.getIp() +
				" ha elegido el mapa " + paquetePersonaje.getMapa() + System.lineSeparator());
		return gson.toJson(respuesta);
	}

}
