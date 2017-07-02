package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import mensajeria.PaqueteIntercambio;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorIntercambio extends Procesador {

	public ProcesadorIntercambio(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaqueteIntercambio paqueteIntercambio = gson.fromJson(entrada, PaqueteIntercambio.class);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteIntercambio.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteIntercambio));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

}
