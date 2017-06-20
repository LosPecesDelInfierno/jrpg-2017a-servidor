package comunicacion;

import java.io.IOException;

import com.google.gson.Gson;

import estados.Estado;
import mensajeria.PaqueteMensaje;
import servidor.EscuchaCliente;
import servidor.Servidor;

public class ProcesadorMensaje extends Procesador {

	public ProcesadorMensaje(ContextoProcesador contextoProcesador, Gson gson) {
		super(contextoProcesador, gson);
	}

	@Override
	public String procesar(String entrada) {
		PaqueteMensaje paqueteMensaje = gson.fromJson(entrada, PaqueteMensaje.class);
		int destinatario = paqueteMensaje.getIdDestinatario();
		if (paqueteMensaje.esDifusion()) {
			for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
				enviarMensaje(paqueteMensaje, conectado);
			}
		} else {
			for(EscuchaCliente conectado : Servidor.getClientesConectados()) {
				if (conectado.getPaquetePersonaje().getId() == destinatario)
					enviarMensaje(paqueteMensaje, conectado);
			}
		}
		return "";
	}

	private void enviarMensaje(PaqueteMensaje mensaje, EscuchaCliente destinatario) {
		try {
			if (destinatario.getPaquetePersonaje().getEstado() == Estado.estadoJuego) {
				destinatario.getSalida().writeObject(gson.toJson(mensaje));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
