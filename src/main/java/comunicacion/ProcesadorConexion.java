package comunicacion;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import servidor.Servidor;

public class ProcesadorConexion extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.CONEXION);
		PaquetePersonaje paquetePersonaje = (PaquetePersonaje) (gson.fromJson(entrada, PaquetePersonaje.class)).clone();

		Servidor.getPersonajesConectados().put(paquetePersonaje.getId(), (PaquetePersonaje) paquetePersonaje.clone());
		Servidor.getUbicacionPersonajes().put(paquetePersonaje.getId(), (PaqueteMovimiento) new PaqueteMovimiento(paquetePersonaje.getId()).clone());
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		
		return gson.toJson(respuesta);

	}

}
