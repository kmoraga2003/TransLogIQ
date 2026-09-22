package dao;

import Conexion.Conexion;
import modulo.Conductor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO de Conductores con CRUD completo y modo Fallback autónomo.
 */
public class ConductorDAO {

    private static final Map<Integer, Conductor> FALLBACK_CONDUCTORES = new ConcurrentHashMap<>();
    private static final AtomicInteger AUTO_ID = new AtomicInteger(3);

    static {
        FALLBACK_CONDUCTORES.put(1, new Conductor(1, "Juan Pérez"));
        FALLBACK_CONDUCTORES.put(2, new Conductor(2, "Carlos Gómez"));
        FALLBACK_CONDUCTORES.put(3, new Conductor(3, "Roberto Silva"));
    }

    public boolean agregarConductor(Conductor c) {
        if (!Conexion.isConectado()) {
            int newId = AUTO_ID.incrementAndGet();
            c.setIdConductor(newId);
            FALLBACK_CONDUCTORES.put(newId, c);
            return true;
        }
        String sql = "INSERT INTO conductor (nombre) VALUES (?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNombre());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            int newId = AUTO_ID.incrementAndGet();
            c.setIdConductor(newId);
            FALLBACK_CONDUCTORES.put(newId, c);
            return true;
        }
    }

    public boolean modificarConductor(Conductor c) {
        if (!Conexion.isConectado()) {
            if (FALLBACK_CONDUCTORES.containsKey(c.getIdConductor())) {
                FALLBACK_CONDUCTORES.put(c.getIdConductor(), c);
                return true;
            }
            return false;
        }
        String sql = "UPDATE conductor SET nombre = ? WHERE id_conductor = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getNombre());
            pstmt.setInt(2, c.getIdConductor());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FALLBACK_CONDUCTORES.put(c.getIdConductor(), c);
            return true;
        }
    }

    public boolean eliminarConductor(int idConductor) {
        if (!Conexion.isConectado()) {
            return FALLBACK_CONDUCTORES.remove(idConductor) != null;
        }
        String sql = "DELETE FROM conductor WHERE id_conductor = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idConductor);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return FALLBACK_CONDUCTORES.remove(idConductor) != null;
        }
    }

    public List<Conductor> listarConductores() {
        if (!Conexion.isConectado()) {
            return new ArrayList<>(FALLBACK_CONDUCTORES.values());
        }
        List<Conductor> lista = new ArrayList<>();
        String sql = "SELECT id_conductor, nombre FROM conductor";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Conductor(rs.getInt("id_conductor"), rs.getString("nombre")));
            }
        } catch (SQLException e) {
            return new ArrayList<>(FALLBACK_CONDUCTORES.values());
        }
        return lista;
    }
}
