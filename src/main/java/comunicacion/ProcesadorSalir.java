package comunicacion;

import mensajeria.Comando;
import mensajeria.Paquete;
import servidor.Servidor;

public class ProcesadorSalir extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete p = gson.fromJson(entrada, Paquete.class);
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.SALIR);
		// Lo elimino de los clientes conectados
		Servidor.getClientesConectados().remove(p);
		
		// Indico que se desconecto
		Servidor.log.append(p.getIp() + " se ha desconectado." + System.lineSeparator());
		
		return gson.toJson(respuesta);
	}

}
