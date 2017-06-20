package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.PaqueteComercio;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorComercio extends Procesador {

	public ProcesadorComercio(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {

		// Le reenvio al id del otro personaje que quieren comerciar
		PaqueteComercio paqueteComercio = gson.fromJson(entrada, PaqueteComercio.class);
		Servidor.log.append(paqueteComercio.getId() + " quiere comerciar con " + paqueteComercio.getIdEnemigo() + System.lineSeparator());
		
		//seteo estado de batalla
		Servidor.getPersonajesConectados().get(paqueteComercio.getId()).setEstado(Estado.estadoComercio);
		Servidor.getPersonajesConectados().get(paqueteComercio.getIdEnemigo()).setEstado(Estado.estadoComercio);

		//salida.writeObject(gson.toJson(paqueteBatalla));
		for(EscuchaCliente conectado : Servidor.getClientesConectados()){
			if(conectado.getIdPersonaje() == paqueteComercio.getIdEnemigo()){
				int aux = paqueteComercio.getId();
				paqueteComercio.setId(paqueteComercio.getIdEnemigo());
				paqueteComercio.setIdEnemigo(aux);
				paqueteComercio.setMiTurno(false);
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteComercio));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		
		int aux = paqueteComercio.getId();
		paqueteComercio.setId(paqueteComercio.getIdEnemigo());
		paqueteComercio.setIdEnemigo(aux);
		paqueteComercio.setMiTurno(true);
		return gson.toJson(paqueteComercio);
	}
}
