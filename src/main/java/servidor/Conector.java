package servidor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.LinkedList;
import java.util.List;
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

				PreparedStatement st = connect.prepareStatement("INSERT INTO registro (usuario, password) VALUES (?,?)");
				st.setString(1, user.getUsername());
				st.setString(2, user.getPassword());
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

	public boolean registrarPersonaje(PaquetePersonaje paquetePersonaje) {

		try {

			// Registro al personaje en la base de datos
			PreparedStatement stRegistrarPersonaje = connect.prepareStatement(
					"INSERT INTO personaje (idUsuario, casta,raza,fuerza,destreza,inteligencia,saludTope,energiaTope,nombre,experiencia,nivel,idAlianza) VALUES (?, ?,?,?,?,?,?,?,?,?,?,?)",
					PreparedStatement.RETURN_GENERATED_KEYS);
			stRegistrarPersonaje.setString(1, paquetePersonaje.getUsuario());
			stRegistrarPersonaje.setString(2, paquetePersonaje.getCasta());
			stRegistrarPersonaje.setString(3, paquetePersonaje.getRaza());
			stRegistrarPersonaje.setInt(4, paquetePersonaje.getFuerza());
			stRegistrarPersonaje.setInt(5, paquetePersonaje.getDestreza());
			stRegistrarPersonaje.setInt(6, paquetePersonaje.getInteligencia());
			stRegistrarPersonaje.setInt(7, paquetePersonaje.getSaludTope());
			stRegistrarPersonaje.setInt(8, paquetePersonaje.getEnergiaTope());
			stRegistrarPersonaje.setString(9, paquetePersonaje.getNombre());
			stRegistrarPersonaje.setInt(10, 0);
			stRegistrarPersonaje.setInt(11, 1);
			stRegistrarPersonaje.setInt(12, -1);
			stRegistrarPersonaje.execute();

			// Recupero la última key generada
			ResultSet rs = stRegistrarPersonaje.getGeneratedKeys();
			if (rs != null && rs.next()) {

				// Obtengo el id
				int idPersonaje = rs.getInt(1);

				// Le asigno el id al paquete personaje que voy a devolver
				paquetePersonaje.setId(idPersonaje);

				// Por último registro el inventario y la mochila
				if (this.registrarInventarioMochila(idPersonaje)) {
					Servidor.log.append("El usuario " + paquetePersonaje.getUsuario() + " ha creado el personaje "
							+ paquetePersonaje.getId() + System.lineSeparator());
					return true;
				} else {
					Servidor.log.append("Error al registrar la mochila y el inventario del usuario " + paquetePersonaje.getUsuario() + " con el personaje" + paquetePersonaje.getId() + System.lineSeparator());
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
			
			stActualizarPersonaje.setInt(1, paquetePersonaje.getFuerzaBase());
			stActualizarPersonaje.setInt(2, paquetePersonaje.getDestrezaBase());
			stActualizarPersonaje.setInt(3, paquetePersonaje.getInteligenciaBase());
			stActualizarPersonaje.setInt(4, paquetePersonaje.getSaludBase());
			stActualizarPersonaje.setInt(5, paquetePersonaje.getEnergiaBase());
			stActualizarPersonaje.setInt(6, paquetePersonaje.getExperiencia());
			stActualizarPersonaje.setInt(7, paquetePersonaje.getNivel());
			stActualizarPersonaje.setInt(8, paquetePersonaje.getId());
			stActualizarPersonaje.executeUpdate();
			
			if (paquetePersonaje.ganoBatalla()) {
				Item item = this.getRandomItem(paquetePersonaje.getFuerzaBase(), paquetePersonaje.getDestrezaBase(), paquetePersonaje.getInteligenciaBase());
				String queryInventario = "UPDATE Inventario SET IDItem = ? WHERE IDPersonaje = ? AND IDTipoItem = ?";
				PreparedStatement stActualizarInventario = connect.prepareStatement(queryInventario);
				stActualizarInventario.setInt(1, item.getId());
				stActualizarInventario.setInt(2, paquetePersonaje.getId());
				stActualizarInventario.setInt(3, item.getIdTipoItem());
				stActualizarInventario.executeUpdate();
				Servidor.log.append("Asigno al personaje " + paquetePersonaje.getNombre() + " el item " + item.getNombre() + System.lineSeparator());
				paquetePersonaje.agregarItem(item);
			} else {
				String queryLimpiarInventario = "UPDATE Inventario SET IDItem = null WHERE IDPersonaje = ?";
				PreparedStatement stLimpiarInventario = connect.prepareStatement(queryLimpiarInventario);
				stLimpiarInventario.setInt(1, paquetePersonaje.getId());
				stLimpiarInventario.executeUpdate();
				
				String queryInventario = "UPDATE Inventario SET IDItem = ? WHERE IDPersonaje = ? AND IDTipoItem = ?";
				PreparedStatement stActualizarInventario = connect.prepareStatement(queryInventario);
				stActualizarInventario.setInt(2, paquetePersonaje.getId());
				for (Item item : paquetePersonaje.getInventario()) {
					stActualizarInventario.setInt(1, item.getId());
					stActualizarInventario.setInt(3, item.getIdTipoItem());
					stActualizarInventario.executeUpdate();
				}
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
			PreparedStatement st = connect.prepareStatement("SELECT * FROM personaje WHERE idUsuario = ?");
			st.setString(1, user.getUsername());
			result = st.executeQuery();

			// Obtengo los atributos del personaje
			int idPersonaje = result.getInt("idPersonaje");
			PaquetePersonaje personaje = new PaquetePersonaje();
			personaje.setId(idPersonaje);
			personaje.setUsuario(result.getString("idUsuario"));
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

			List<Item> items = leerItems(inventario);
			personaje.equipar(items);
			
			return personaje;

		} catch (SQLException ex) {
			Servidor.log.append("Fallo al intentar recuperar el personaje " + user.getUsername() + System.lineSeparator());
			Servidor.log.append(ex.getMessage() + System.lineSeparator());
			ex.printStackTrace();
		}

		return new PaquetePersonaje();
	}
	
	private List<Item> leerItems(ResultSet resultset) throws SQLException {
		List<Item> items = new LinkedList<Item>();
		Item itemAnterior = new Item();
		while (resultset.next()) {
			int idItem = resultset.getInt("ID");
			ModificadorItem modificador = rsToModificadorItem(resultset);
			if (idItem == itemAnterior.getId()) {
				itemAnterior.addModificador(modificador);
			} else {
				Item item = rsToItem(resultset);
				item.addModificador(modificador);
				items.add(item);
				itemAnterior = item;
			}
		}
		return items;
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
			
			PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
			paqueteUsuario.setUsername(usuario);
			paqueteUsuario.setPassword(password);
			
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
			String query = "Select I.*, M.* From ModificadorItem M INNER JOIN (SELECT * FROM Item WHERE FuerzaRequerida <= ? and DestrezaRequerida <= ? and InteligenciaRequerida <= ? ORDER BY RANDOM() LIMIT 1) as I ON M.IDItem = I.ID";
			st = connect.prepareStatement(query);		
			st.setInt(1, fuerza);
			st.setInt(2, destreza);
			st.setInt(3, inteligencia);
			result = st.executeQuery();
			
			return leerItems(result).get(0);
			
		} catch (SQLException e) {
			Servidor.log.append("Fallo al intentar recuperar random item " + System.lineSeparator());
			Servidor.log.append(e.getMessage() + System.lineSeparator());
			e.printStackTrace();
		}
		return null;
	}
}
