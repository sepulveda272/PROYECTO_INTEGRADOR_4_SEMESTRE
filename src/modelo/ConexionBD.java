package modelo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionBD {
    private static ConexionBD instancia;
    private Connection connection = null;

    private final String url = "jdbc:oracle:thin:@localhost:1521:XE";

    // AHORA user y password ya no son final
    private String user;
    private String password;

    // Constructor privado para Singleton
    private ConexionBD(String user, String password) {
        this.user = user;
        this.password = password;
        conectar();
    }

    // Conecta con las credenciales actuales
    private void conectar() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Conexión establecida como " + user +
                                   " - Driver: " + meta.getDriverName());
            }
        } catch (SQLException ex) {
            System.out.println("Error de conexión con " + user + ": " + ex.getMessage());
        }
    }

    // Instancia por defecto (por ejemplo con el dueño de las tablas)
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            // Usuario “general” para iniciar la app (por ejemplo APP_OWNER o PROYECTOINTE)
            instancia = new ConexionBD("PROYECTOINTE", "proyectointe");
        }
        return instancia;
    }

    // 🔴 NUEVO: reconfigurar el Singleton con otro usuario (según el rol)
    public static void reconfigurar(String user, String password) {
        try {
            if (instancia != null && instancia.connection != null && !instancia.connection.isClosed()) {
                instancia.connection.close();
                System.out.println("Conexión anterior cerrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        instancia = new ConexionBD(user, password);
    }

    // Obtener conexión
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Conexión cerrada. Intentando reconectar como " + user + "...");
                conectar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Cerrar (si quieres hacerlo al salir de la app)
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ConexionBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
