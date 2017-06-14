package comunicacion;

import java.io.IOException;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorAtacar extends Procesador {

	@Override
	public String procesar(String entrada) {
		Paquete respuesta = new Paquete(Paquete.msjExito, Comando.ATACAR);
		PaqueteAtacar paqueteAtacar = (PaqueteAtacar) gson.fromJson(entrada, PaqueteAtacar.class);
		for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
			if(conectado.getIdPersonaje() == paqueteAtacar.getIdEnemigo()) {
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteAtacar));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return gson.toJson(respuesta);
	}

}
