package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorAtacar extends Procesador {

	public ProcesadorAtacar(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaqueteAtacar paqueteAtacar = gson.fromJson(entrada, PaqueteAtacar.class);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteAtacar));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return "";
	}

}
