package comunicacion;

import java.io.IOException;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaquetePersonaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesoDesconectar extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.DESCONECTAR);
		PaquetePersonaje paquete = (PaquetePersonaje) gson.fromJson(entrada, PaquetePersonaje.class);
//		entrada.close();
//		salida.close();
//		socket.close();

		//Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
		//Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
		//Servidor.getClientesConectados().remove(this);

		Servidor.getPersonajesConectados().remove(paquete.getId());
				Servidor.getUbicacionPersonajes().remove(paquete.getId());
				Servidor.getClientesConectados().remove(this);
		
		for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
			PaqueteDePersonajes paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
			paqueteDePersonajes.setComando(Comando.CONEXION);
			try {
				conectado.getSalida().writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());

		return gson.toJson(respuesta);
	}

}
