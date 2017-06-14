package comunicacion;

import mensajeria.Comando;

public class ProcesadorFactory {
	public static Procesador crear(int comando) throws ComandoDesconocidoException {
		switch (comando) {
		case Comando.REGISTRO:
			return new ProcesadorRegistro();
		case Comando.CREACIONPJ:
			return new ProcesadorCreacionPJ();
		case Comando.INICIOSESION:
			return new ProcesadorInicioDeSesion();
		default:
			throw new ComandoDesconocidoException();
		}
	}
}
