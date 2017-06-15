package comunicacion;

import com.google.gson.Gson;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class ProcesadorInicioDeSesion extends Procesador {

	public ProcesadorInicioDeSesion(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		// Recibo el paquete usuario
		PaqueteUsuario paqueteUsuario = gson.fromJson(entrada, PaqueteUsuario.class);
		
		// Si se puede loguear el usuario le envio un mensaje de exito y el paquete personaje con los datos
		if (Servidor.getConector().loguearUsuario(paqueteUsuario)) {
			
			PaquetePersonaje paquetePersonaje = Servidor.getConector().getPersonaje(paqueteUsuario);
			contextoProcesador.setPaquetePersonaje(paquetePersonaje);
			paquetePersonaje.setComando(Comando.INICIOSESION);
			paquetePersonaje.setMensaje(Paquete.msjExito);
			return gson.toJson(paquetePersonaje);
			
		} else {
			Paquete respuesta = new Paquete(Paquete.msjFracaso, Comando.INICIOSESION);
			return gson.toJson(respuesta);
		}
	}

}
