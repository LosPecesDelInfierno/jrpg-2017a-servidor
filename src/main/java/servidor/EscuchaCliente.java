package servidor;

import java.io.*;
import java.net.Socket;

import com.google.gson.Gson;

import cliente.*;
import comunicacion.ComandoDesconocidoException;
import comunicacion.Procesador;
import comunicacion.ProcesadorFactory;
import dominio.*;
import estados.Estado;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteAtacar;
import mensajeria.PaqueteBatalla;
import mensajeria.PaqueteDeMovimientos;
import mensajeria.PaqueteDePersonajes;
import mensajeria.PaqueteFinalizarBatalla;
import mensajeria.PaqueteMovimiento;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class EscuchaCliente extends Thread {

	private final Socket socket;
	private final ObjectInputStream entrada;
	private final ObjectOutputStream salida;
	private int idPersonaje;
	private final Gson gson = new Gson();
	
	private PaquetePersonaje paquetePersonaje;
	private PaqueteMovimiento paqueteMovimiento;
	private PaqueteBatalla paqueteBatalla;
	private PaqueteAtacar paqueteAtacar;
	private PaqueteFinalizarBatalla paqueteFinalizarBatalla;
	private PaqueteDeMovimientos paqueteDeMovimiento;
	private PaqueteDePersonajes paqueteDePersonajes;

	public EscuchaCliente(String ip, Socket socket, ObjectInputStream entrada, ObjectOutputStream salida) {
		this.socket = socket;
		this.entrada = entrada;
		this.salida = salida;
		paquetePersonaje = new PaquetePersonaje();
	}

	public void run() {
		try {
			Paquete paquete;
			Paquete paqueteSv = new Paquete(null, 0);
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
//			this.contextoProcesador = new ContextoProcesador(gson, paqueteUsuario, paquetePersonaje);
			
			String cadenaLeida = (String) entrada.readObject();
			
			while (!((paquete = gson.fromJson(cadenaLeida, Paquete.class)).getComando() == Comando.DESCONECTAR)){
				
				
//					if(paquete.getComando() == Comando.SALIR) {
//						// Cierro todo
//						entrada.close();
//						salida.close();
//						socket.close();
//						
//						// Lo elimino de los clientes conectados
//						Servidor.getClientesConectados().remove(this);
//						
//						// Indico que se desconecto
//						Servidor.log.append(socket.getLocalAddress().getHostAddress() + " se ha desconectado." + System.lineSeparator());
//						
//						return;
//					}
				
					Procesador procesador = ProcesadorFactory.crear(paquete.getComando());
					salida.writeObject(procesador.procesar(cadenaLeida));
					// Posibilidad 1 -> hago las dos tareas de server en el Proceso y luego salgo del run() de
					// EscuchaCliente, tal como hacia el switch anteriormente..
//					if(paquete.getComando() == Comando.SALIR) {
//						entrada.close();
//						salida.close();
//						socket.close();
//						return;
//					}
					// Posibilidad 2 -> uso un break en vez de un return y dejo vacío el procesoSalir.
					if(paquete.getComando() == Comando.SALIR) {
						break;
					}
				cadenaLeida = (String) entrada.readObject();
			}

			
			entrada.close();
			salida.close();
			socket.close();

			Servidor.getPersonajesConectados().remove(paquetePersonaje.getId());
			Servidor.getUbicacionPersonajes().remove(paquetePersonaje.getId());
			Servidor.getClientesConectados().remove(this);
			
			for (EscuchaCliente conectado : Servidor.getClientesConectados()) {
				PaqueteDePersonajes paqueteDePersonajes = new PaqueteDePersonajes(Servidor.getPersonajesConectados());
				paqueteDePersonajes.setComando(Comando.CONEXION);
				try {
					conectado.getSalida().writeObject(gson.toJson(paqueteDePersonajes, PaqueteDePersonajes.class));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Servidor.log.append(socket.getLocalAddress().getHostAddress() + " se ha desconectado." + System.lineSeparator());
		
		} catch (IOException | ClassNotFoundException | ComandoDesconocidoException e) {
			Servidor.log.append("Error de conexion: " + e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		} 
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public ObjectInputStream getEntrada() {
		return entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return salida;
	}
	
	public PaquetePersonaje getPaquetePersonaje(){
		return paquetePersonaje;
	}
	
	public int getIdPersonaje() {
		return idPersonaje;
	}
}

