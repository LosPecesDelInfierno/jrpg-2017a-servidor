package comunicacion;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteUsuario;
import servidor.Servidor;

public class ProcesadorRegistro extends Procesador {

	@Override
	public String procesar(String entrada) {
		PaqueteUsuario paqueteUsuario = gson.fromJson(entrada, PaqueteUsuario.class);
		Paquete respuesta = new Paquete();
		respuesta.setComando(Comando.REGISTRO);

		if (Servidor.getConector().registrarUsuario(paqueteUsuario)) {
			respuesta.setMensaje(Paquete.msjExito);
		} else {
			respuesta.setMensaje(Paquete.msjFracaso);
		}
		return gson.toJson(respuesta);
	}

}
