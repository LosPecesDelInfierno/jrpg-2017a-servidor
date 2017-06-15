package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.PaqueteFinalizarBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorFinalizarBatalla extends Procesador {

	public ProcesadorFinalizarBatalla(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaqueteFinalizarBatalla paqueteFinalizarBatalla = gson.fromJson(entrada, PaqueteFinalizarBatalla.class);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getId()).setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paqueteFinalizarBatalla.getIdEnemigo()).setEstado(Estado.estadoJuego);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) { // TODO: Arreglar esta cochinada... (getCliente(id))...
			if(conectado.getIdPersonaje() == paqueteFinalizarBatalla.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteFinalizarBatalla));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		return "";
	}

}
