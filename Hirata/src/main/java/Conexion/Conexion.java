package Conexion;

import seguridad.EnvConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de Conexión a la Base de Datos MySQL con lectura segura desde .env.
 */
public class Conexion {

    private static Boolean disponible = null;

    public static Connection getConexion() {
        String url = EnvConfig.getDbUrl();
        String usuario = EnvConfig.getDbUser();
        String clave = EnvConfig.getDbPassword();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, usuario, clave);
            disponible = true;
            return conn;
        } catch (ClassNotFoundException e) {
            disponible = false;
            System.err.println("Driver MySQL no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            disponible = false;
            System.err.println("Sin conexión a MySQL: " + e.getMessage() + ". Utilizando modo memoria/fallback local.");
        }
        return null;
    }

    /**
     * Indica si la base de datos MySQL responde correctamente.
     */
    public static boolean isConectado() {
        if (disponible != null) return disponible;
        try (Connection conn = getConexion()) {
            disponible = (conn != null && !conn.isClosed());
        } catch (Exception e) {
            disponible = false;
        }
        return disponible != null && disponible;
    }
}
