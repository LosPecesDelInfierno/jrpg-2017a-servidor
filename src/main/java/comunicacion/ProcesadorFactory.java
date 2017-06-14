package comunicacion;

import mensajeria.Comando;

public class ProcesadorFactory {
	public static Procesador crear(int comando) throws ComandoDesconocidoException {
		switch (comando) {
		case Comando.REGISTRO:
			return new ProcesadorRegistro();
		case Comando.CREACIONPJ:
			return new ProcesadorCreacionPJ();
		default:
			throw new ComandoDesconocidoException();
		}
	}
}
