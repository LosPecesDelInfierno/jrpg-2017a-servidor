package comunicacion;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class ProcesadorInicioDeSesion extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Comando.INICIOSESION);
		
		// Recibo el paquete usuario
		PaqueteUsuario paqueteUsuario = (PaqueteUsuario) (gson.fromJson(entrada, PaqueteUsuario.class));
		
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
			
			PaquetePersonaje paquetePersonaje = new PaquetePersonaje();
			paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
			paquetePersonaje.setComando(Comando.INICIOSESION);
			paquetePersonaje.setMensaje(Paquete.msjExito);
			//int idPersonaje = paquetePersonaje.getId();
			return gson.toJson(paquetePersonaje);
			
		} else {
			respuesta.setMensaje(Paquete.msjFracaso);
			return gson.toJson(respuesta);
		}
	}

}
