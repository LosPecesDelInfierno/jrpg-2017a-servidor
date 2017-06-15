package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.PaqueteBatalla;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorBatalla extends Procesador {

	public ProcesadorBatalla(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		
		// Le reenvio al id del personaje batallado que quieren pelear
		PaqueteBatalla paqueteBatalla = gson.fromJson(entrada, PaqueteBatalla.class);
		Servidor.log.append(paqueteBatalla.getId() + " quiere batallar con " + paqueteBatalla.getIdEnemigo() + System.lineSeparator());
		
		//seteo estado de batalla
		Servidor.getPersonajesConectados().get(paqueteBatalla.getId()).setEstado(Estado.estadoBatalla);
		Servidor.getPersonajesConectados().get(paqueteBatalla.getIdEnemigo()).setEstado(Estado.estadoBatalla);

		//salida.writeObject(gson.toJson(paqueteBatalla));
		for(EscuchaCliente conectado : Servidor.getClientesConectados()){
			if(conectado.getIdPersonaje() == paqueteBatalla.getIdEnemigo()){
				int aux = paqueteBatalla.getId();
				paqueteBatalla.setId(paqueteBatalla.getIdEnemigo());
				paqueteBatalla.setIdEnemigo(aux);
				paqueteBatalla.setMiTurno(false);
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteBatalla));
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
		}
		synchronized(Servidor.atencionConexiones){
			Servidor.atencionConexiones.notify();
		}
		
		int aux = paqueteBatalla.getId();
		paqueteBatalla.setId(paqueteBatalla.getIdEnemigo());
		paqueteBatalla.setIdEnemigo(aux);
		paqueteBatalla.setMiTurno(true);
		return gson.toJson(paqueteBatalla);
	}

}
