package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.PaqueteFinalizarComercio;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorFinalizarComercio extends Procesador {

	public ProcesadorFinalizarComercio(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaqueteFinalizarComercio paquete = gson.fromJson(entrada, PaqueteFinalizarComercio.class);
		Servidor.getPersonajesConectados().get(paquete.getId()).setEstado(Estado.estadoJuego);
		Servidor.getPersonajesConectados().get(paquete.getIdEnemigo()).setEstado(Estado.estadoJuego);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) { // TODO: Arreglar esta cochinada... (getCliente(id))...
			if(conectado.getIdPersonaje() == paquete.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paquete));
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
