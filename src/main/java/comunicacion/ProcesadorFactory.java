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
		case Comando.ACTUALIZARPERSONAJE:
			return new ProcesadorActualizarPersonaje();
		case Comando.ATACAR:
			return new ProcesadorAtacar();
		case Comando.BATALLA:
			return new ProcesadorBatalla();
		case Comando.CONEXION:
			return new ProcesadorConexion();
		case Comando.FINALIZARBATALLA:
			return new ProcesadorFinalizarBatalla();
		case Comando.MOSTRARMAPAS:
			return new ProcesadorMostrarMapas();
		case Comando.MOVIMIENTO:
			return new ProcesadorMoviento();
		case Comando.SALIR:
			return new ProcesadorSalir();
		default:
			throw new ComandoDesconocidoException();
		}
	}
}
