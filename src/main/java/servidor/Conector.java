package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import dominio.Item;
import dominio.ModificadorItem;
import mensajeria.PaquetePersonaje;
import mensajeria.PaqueteUsuario;

public class Conector {

	private String url = "primeraBase.bd";
	Connection connect;

	public void connect() {
		try {
			Servidor.log.append("Estableciendo conexi�n con la base de datos..." + System.lineSeparator());
			connect = DriverManager.getConnection("jdbc:sqlite:" + url);
			Servidor.log.append("Conexi�n con la base de datos establecida con �xito." + System.lineSeparator());
		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar establecer la conexi�n con la base de datos. " + ex.getMessage()
					+ System.lineSeparator());
		}
	}

	public void close() {
		try {
			connect.close();
		} catch (SQLException ex) {
			Servidor.log.append("Error al intentar cerrar la conexi�n con la base de datos." + System.lineSeparator());
			Logger.getLogger(Conector.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public boolean registrarUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			PreparedStatement st1 = connect.prepareStatement("SELECT * FROM registro WHERE usuario= ? ");
			st1.setString(1, user.getUsername());
			result = st1.executeQuery();

			if (!result.next()) {

				PreparedStatement st = connect.prepareStatement("INSERT INTO registro (usuario, password, idPersonaje) VALUES (?,?,?)");
				st.setString(1, user.getUsername());
				st.setString(2, user.getPassword());
				st.setInt(3, user.getIdPj());
				st.execute();
				Servidor.log.append("El usuario " + user.getUsername() + " se ha registrado." + System.lineSeparator());
				return true;
			} else {
				Servidor.log.append("El usuario " + user.getUsername() + " ya se encuentra en uso." + System.lineSeparator());
				return false;
			}
		} catch (SQLException ex) {
			Servidor.log.append("Eror al intentar registrar el usuario " + user.getUsername() + System.lineSeparator());
			System.err.println(ex.getMessage());
			return false;
		}

	}

	public boolean registrarPersonaje(PaquetePersonaje paquetePersonaje, PaqueteUsuario paqueteUsuario) {

		try {

			// Registro al personaje en la base de datos
			PreparedStatement stRegistrarPersonaje = connect.prepareStatement(
					"INSERT INTO personaje (casta,raza,fuerza,destreza,inteligencia,saludTope,energiaTope,nombre,experiencia,nivel,idAlianza) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			stRegistrarPersonaje.setString(1, paquetePersonaje.getCasta());
			stRegistrarPersonaje.setString(2, paquetePersonaje.getRaza());
			stRegistrarPersonaje.setInt(3, paquetePersonaje.getFuerza());
			stRegistrarPersonaje.setInt(4, paquetePersonaje.getDestreza());
			stRegistrarPersonaje.setInt(5, paquetePersonaje.getInteligencia());
			stRegistrarPersonaje.setInt(6, paquetePersonaje.getSaludTope());
			stRegistrarPersonaje.setInt(7, paquetePersonaje.getEnergiaTope());
			stRegistrarPersonaje.setString(8, paquetePersonaje.getNombre());
			stRegistrarPersonaje.setInt(9, 0);
			stRegistrarPersonaje.setInt(10, 1);
			stRegistrarPersonaje.setInt(11, -1);
			stRegistrarPersonaje.execute();

			// Recupero la última key generada
			ResultSet rs = stRegistrarPersonaje.getGeneratedKeys();
			if (rs != null && rs.next()) {

				// Obtengo el id
				int idPersonaje = rs.getInt(1);

				// Le asigno el id al paquete personaje que voy a devolver
				paquetePersonaje.setId(idPersonaje);

				// Le asigno el personaje al usuario
				PreparedStatement stAsignarPersonaje = connect.prepareStatement("UPDATE registro SET idPersonaje=? WHERE usuario=? AND password=?");
				stAsignarPersonaje.setInt(1, idPersonaje);
				stAsignarPersonaje.setString(2, paqueteUsuario.getUsername());
				stAsignarPersonaje.setString(3, paqueteUsuario.getPassword());
				stAsignarPersonaje.execute();

				// Por último registro el inventario y la mochila
				if (this.registrarInventarioMochila(idPersonaje)) {
					Servidor.log.append("El usuario " + paqueteUsuario.getUsername() + " ha creado el personaje "
							+ paquetePersonaje.getId() + System.lineSeparator());
					return true;
				} else {
					Servidor.log.append("Error al registrar la mochila y el inventario del usuario " + paqueteUsuario.getUsername() + " con el personaje" + paquetePersonaje.getId() + System.lineSeparator());
					return false;
				}
			}
			return false;

		} catch (SQLException e) {
			Servidor.log.append(
					"Error al intentar crear el personaje " + paquetePersonaje.getNombre() + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public boolean registrarInventarioMochila(int idPersonaje) {
		try {
			String queryInventario = "INSERT INTO Inventario (IDPersonaje, IDTipoItem) VALUES (?, 1), (?, 2), (?, 3), (?, 4), (?, 5), (?, 6)";
			PreparedStatement stRegistrarInventario = connect.prepareStatement(queryInventario);
			for (int i = 1; i <= 6; i++) {
				stRegistrarInventario.setInt(i, idPersonaje);
			}
			stRegistrarInventario.execute();
			
			// TODO: Registrar mochila. (por ahora no se usa)
//			PreparedStatement stRegistrarMochila = connect.prepareStatement("INSERT INTO Mochila(IDPersonaje, NroItem) VALUES(?,?)");
//			stRegistrarMochila.setInt(1, idPersonaje);

			Servidor.log.append("Se ha registrado el inventario de " + idPersonaje + System.lineSeparator());
			return true;

		} catch (SQLException e) {
			Servidor.log.append("Error al registrar el inventario de " + idPersonaje + System.lineSeparator());
			e.printStackTrace();
			return false;
		}
	}

	public boolean loguearUsuario(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			// Busco usuario y contraseña
			PreparedStatement st = connect
					.prepareStatement("SELECT * FROM registro WHERE usuario = ? AND password = ? ");
			st.setString(1, user.getUsername());
			st.setString(2, user.getPassword());
			result = st.executeQuery();

			// Si existe inicio sesión
			if (result.next()) {
				Servidor.log.append("El usuario " + user.getUsername() + " ha iniciado sesión." + System.lineSeparator());
				return true;
			}

			// Si no existe informo y devuelvo false
			Servidor.log.append("El usuario " + user.getUsername() + " ha realizado un intento fallido de inicio de sesión." + System.lineSeparator());
			return false;

		} catch (SQLException e) {
			Servidor.log.append("El usuario " + user.getUsername() + " fallo al iniciar sesión." + System.lineSeparator());
			e.printStackTrace();
			return false;
		}

	}

	public void actualizarPersonaje(PaquetePersonaje paquetePersonaje) {
		try {
			PreparedStatement stActualizarPersonaje = connect
					.prepareStatement("UPDATE personaje SET fuerza=?, destreza=?, inteligencia=?, saludTope=?, energiaTope=?, experiencia=?, nivel=? "
							+ "  WHERE idPersonaje=?");
			
			stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerza());
			stActualizarPersonaje.setInt(2, paquetePersonaje.getDestreza());
			stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligencia());
			stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludTope());
			stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaTope());
			stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(8, paquetePersonaje.getId());
			
			stActualizarPersonaje.executeUpdate();
			
			// TODO: Revisar (a veces llega que ganan los dos........................)
			if (paquetePersonaje.ganoBatalla()) {
				Item item = this.getRandomItem(paquetePersonaje.getFuerza(), paquetePersonaje.getDestreza(), paquetePersonaje.getInteligencia());
				String queryInventario = "UPDATE Inventario SET IDItem = ? WHERE IDPersonaje = ? AND IDTipoItem = ?";			
				PreparedStatement stActualizarInventario = connect.prepareStatement(queryInventario);
				stActualizarInventario.setInt(1, item.getId());
				stActualizarInventario.setInt(2, paquetePersonaje.getId());
				stActualizarInventario.setInt(3, item.getIdTipoItem());
				stActualizarInventario.executeUpdate();
			}
			
			Servidor.log.append("El personaje " + paquetePersonaje.getNombre() + " se ha actualizado con éxito."  + System.lineSeparator());;
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar actualizar el personaje " + paquetePersonaje.getNombre()  + System.lineSeparator());
			e.printStackTrace();
		}	
	}

	public PaquetePersonaje getPersonaje(PaqueteUsuario user) {
		ResultSet result = null;
		try {
			// Selecciono el personaje de ese usuario
			PreparedStatement st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, user.getUsername());
			result = st.executeQuery();

			// Obtengo el id
			int idPersonaje = result.getInt("idPersonaje");

			// Selecciono los datos del personaje
			PreparedStatement stSeleccionarPersonaje = connect
					.prepareStatement("SELECT * FROM personaje WHERE idPersonaje = ?");
			stSeleccionarPersonaje.setInt(1, idPersonaje);
			result = stSeleccionarPersonaje.executeQuery();

			// Obtengo los atributos del personaje
			PaquetePersonaje personaje = new PaquetePersonaje();
			personaje.setId(idPersonaje);
			personaje.setRaza(result.getString("raza"));
			personaje.setCasta(result.getString("casta"));
			personaje.setFuerza(result.getInt("fuerza"));
			personaje.setInteligencia(result.getInt("inteligencia"));
			personaje.setDestreza(result.getInt("destreza"));
			personaje.setEnergiaTope(result.getInt("energiaTope"));
			personaje.setSaludTope(result.getInt("saludTope"));
			personaje.setNombre(result.getString("nombre"));
			personaje.setExperiencia(result.getInt("experiencia"));
			personaje.setNivel(result.getInt("nivel"));
			
			// Items del inventario
			String queryInventario = "SELECT Item.*, ModificadorItem.* FROM Inventario INNER JOIN Item ON Inventario.IDITEM = Item.ID INNER JOIN ModificadorItem ON Item.ID = ModificadorItem.IDItem WHERE IDPersonaje = ?";
			PreparedStatement stGetInventario = connect.prepareStatement(queryInventario);
			stGetInventario.setInt(1, idPersonaje);
			ResultSet inventario = stGetInventario.executeQuery();

			Item itemAnterior = new Item();
			while (inventario.next()) {
				int idItem = inventario.getInt("ID");
				ModificadorItem modificador = rsToModificadorItem(inventario);
				if (idItem == itemAnterior.getId()) {
					itemAnterior.addModificador(modificador);
				} else {
					Item item = rsToItem(inventario);
					item.addModificador(modificador);
					personaje.agregarItem(item);
					itemAnterior = item;
				}
			}
			return personaje;

		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
			ex.printStackTrace();
		}

		return new PaquetePersonaje();
	}
	
	private Item rsToItem(ResultSet resultSet) throws SQLException {
		int id = resultSet.getInt("ID");
		String nombre = resultSet.getString("Nombre");
		int idTipoItem = resultSet.getInt("IDTipoItem");
		int fuerzaRequerida = resultSet.getInt("FuerzaRequerida");
		int destrezaRequerida = resultSet.getInt("DestrezaRequerida");
		int inteligenciaRequerida = resultSet.getInt("InteligenciaRequerida");
		String foto = resultSet.getString("Foto");
		return new Item(id, nombre, idTipoItem, fuerzaRequerida, destrezaRequerida, inteligenciaRequerida, foto);
	}
	
	private ModificadorItem rsToModificadorItem(ResultSet resultSet) throws SQLException {
		int idAtributoModificable = resultSet.getInt("IDAtributoModificable");
		int valor  = resultSet.getInt("Valor");
		boolean esPorcentaje = resultSet.getBoolean("EsPorcentaje");
		return new ModificadorItem(idAtributoModificable, valor, esPorcentaje);
	}
	
	public PaqueteUsuario getUsuario(String usuario) {
		ResultSet result = null;
		PreparedStatement st;
		
		try {
			st = connect.prepareStatement("SELECT * FROM registro WHERE usuario = ?");
			st.setString(1, usuario);
			result = st.executeQuery();

			String password = result.getString("password");
			int idPersonaje = result.getInt("idPersonaje");
			
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			paqueteUsuario.setIdPj(idPersonaje);
			
			return paqueteUsuario;
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar recuperar el usuario " + usuario + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		
		return new PaqueteUsuario();
	}
	
	/**
	 * <h3>Método getRandomItem</h3>
	 * <p>Devuelve un item elegido al azar de la base de datos que cumpla
	 * con los requerimientos necesarios para usarlo </p>
	 */
	public Item getRandomItem(int fuerza, int destreza, int inteligencia) {
		ResultSet result = null;
		PreparedStatement st;
		try {
			st = connect.prepareStatement("SELECT * FROM Item WHERE FuerzaRequerida <= ? " +
					"and DestrezaRequerida <= ? and InteligenciaRequerida <= ? " +
					"ORDER BY RANDOM() LIMIT 1");		
			st.setInt(1, fuerza);
			st.setInt(2, destreza);
			st.setInt(3, inteligencia);
			result = st.executeQuery();
			
			Servidor.log.append("Asigno al personaje el item " + result.getString("nombre") + System.lineSeparator());
			return rsToItem(result);
			
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar recuperar random item " + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		return null;
	}
}
