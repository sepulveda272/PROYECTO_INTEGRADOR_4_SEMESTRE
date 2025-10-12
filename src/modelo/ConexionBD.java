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
    
    private final String url = "jdbc:oracle:thin:@192.168.254.215:1521:orcl"; //jdbc:oracle:thin:@localhost:1521:XE
    private final String user = "proyectointe";
    private final String password = "proyectointe";

    // Constructor privado para Singleton
    private ConexionBD() {
        conectar();
    }

    // Método para conectar a la base de datos
    private void conectar() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                System.out.println("Conexión establecida: " + meta.getDriverName());
            }
        } catch (SQLException ex) {
            System.out.println("Error de conexión: " + ex.getMessage());
        }
    }

    // Obtener instancia única (patrón Singleton)
    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    // Obtener conexión
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Conexión cerrada. Intentando reconectar...");
                conectar();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Cerrar la conexión
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
