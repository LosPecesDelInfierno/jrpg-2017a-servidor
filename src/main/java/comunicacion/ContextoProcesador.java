package comunicacion;

import mensajeria.PaquetePersonaje;

public class ContextoProcesador {
	private PaquetePersonaje paquetePersonaje;
	
	public PaquetePersonaje getPaquetePersonaje() {
		return this.paquetePersonaje;
	}
	
	public void setPaquetePersonaje(PaquetePersonaje paquetePersonaje) {
		this.paquetePersonaje = paquetePersonaje;
	}
}
