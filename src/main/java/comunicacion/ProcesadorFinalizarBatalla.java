package comunicacion;

import java.io.IOException;

import estados.Estado;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorFinalizarBatalla extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.FINALIZARBATALLA);
		PaqueteFinalizarBatalla paqueteFinalizarBatalla = (PaqueteFinalizarBatalla) gson.fromJson(entrada, PaqueteFinalizarBatalla.class);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId()).setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteFinalizarBatalla));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		return gson.toJson(respuesta);
	}

}
