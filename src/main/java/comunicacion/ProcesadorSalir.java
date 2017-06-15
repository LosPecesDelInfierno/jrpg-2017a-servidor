package comunicacion;

import com.google.gson.Gson;

import mensajeria.Comando;
import mensajeria.Paquete;
import servidor.Servidor;

public class ProcesadorSalir extends Procesador {

	public ProcesadorSalir(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
//		Posibilidad 2 sólo devolvería respuesta.
		return "";
		
//		Posibilidad 1 acá abajo :)
//		Paquete paquete = gson.fromJson(entrada, Paquete.class);
//		// Lo elimino de los clientes conectados
//		Servidor.getClientesConectados().remove(this);
//		// Indico que se desconecto
//		Servidor.log.append(paquete.getIp() + " se ha desconectado." + System.lineSeparator());
//		return gson.toJson(respuesta);
	}

}
