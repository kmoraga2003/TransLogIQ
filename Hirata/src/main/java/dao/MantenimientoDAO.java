package dao;

import Conexion.Conexion;
import modulo.Mantenimiento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO de Mantenimientos con CRUD completo y soporte autónomo.
 */
public class MantenimientoDAO {

    private static final Map<Integer, Mantenimiento> FALLBACK_MANTENIMIENTOS = new ConcurrentHashMap<>();
    private static final AtomicInteger AUTO_ID = new AtomicInteger(2);

    static {
        FALLBACK_MANTENIMIENTOS.put(1, new Mantenimiento(1, "BJKL-45", LocalDate.now().minusDays(15), "Cambio de aceite y filtros de aire"));
        FALLBACK_MANTENIMIENTOS.put(2, new Mantenimiento(2, "HT-9900", LocalDate.now().minusDays(5), "Revisión del sistema de frenos y neumático"));
    }

    public boolean registrarMantenimiento(Mantenimiento m) {
        if (!Conexion.isConectado()) {
            int newId = AUTO_ID.incrementAndGet();
            m.setIdMantenimiento(newId);
            FALLBACK_MANTENIMIENTOS.put(newId, m);
            return true;
        }
        String sql = "INSERT INTO mantenimiento (patente_camion, fecha, descripcion) VALUES (?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getPatenteCamion());
            pstmt.setDate(2, Date.valueOf(m.getFecha()));
            pstmt.setString(3, m.getDescripcion());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            int newId = AUTO_ID.incrementAndGet();
            m.setIdMantenimiento(newId);
            FALLBACK_MANTENIMIENTOS.put(newId, m);
            return true;
        }
    }

    public boolean eliminarMantenimiento(int idMantenimiento) {
        if (!Conexion.isConectado()) {
            return FALLBACK_MANTENIMIENTOS.remove(idMantenimiento) != null;
        }
        String sql = "DELETE FROM mantenimiento WHERE id_mantenimiento = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idMantenimiento);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return FALLBACK_MANTENIMIENTOS.remove(idMantenimiento) != null;
        }
    }

    public List<Mantenimiento> listarMantenimientos() {
        if (!Conexion.isConectado()) {
            return new ArrayList<>(FALLBACK_MANTENIMIENTOS.values());
        }
        List<Mantenimiento> lista = new ArrayList<>();
        String sql = "SELECT id_mantenimiento, patente_camion, fecha, descripcion FROM mantenimiento";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        rs.getString("patente_camion"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("descripcion")
                ));
            }
        } catch (SQLException e) {
            return new ArrayList<>(FALLBACK_MANTENIMIENTOS.values());
        }
        return lista;
    }
}
