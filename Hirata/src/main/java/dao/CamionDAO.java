package dao;

import modulo.Camion;
import Conexion.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DAO de Camiones con soporte CRUD completo y modo Fallback para pruebas autónomas.
 */
public class CamionDAO {

    // Lista en memoria de respaldo si MySQL no está disponible
    private static final Map<String, Camion> FALLBACK_CAMIONES = new ConcurrentHashMap<>();
    private static final Map<String, Integer> FALLBACK_ASIGNACIONES = new ConcurrentHashMap<>();

    static {
        FALLBACK_CAMIONES.put("BJKL-45", new Camion("BJKL-45", "Volvo", "FH16", 2022, 4850));
        FALLBACK_CAMIONES.put("FH-2023", new Camion("FH-2023", "Scania", "R500", 2023, 1200));
        FALLBACK_CAMIONES.put("HT-9900", new Camion("HT-9900", "Mercedes-Benz", "Actros", 2021, 5400));
        
        FALLBACK_ASIGNACIONES.put("BJKL-45", 1);
        FALLBACK_ASIGNACIONES.put("FH-2023", 2);
    }

    public boolean agregarCamion(Camion c) {
        if (!Conexion.isConectado()) {
            FALLBACK_CAMIONES.put(c.getPatente(), c);
            return true;
        }
        String sql = "INSERT INTO camion (patente, marca, modelo, anio, kilometraje_actual) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getPatente());
            pstmt.setString(2, c.getMarca());
            pstmt.setString(3, c.getModelo());
            pstmt.setInt(4, c.getAnio());
            pstmt.setInt(5, c.getKilometraje());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FALLBACK_CAMIONES.put(c.getPatente(), c);
            return true;
        }
    }

    public boolean modificarCamion(Camion c) {
        if (!Conexion.isConectado()) {
            if (FALLBACK_CAMIONES.containsKey(c.getPatente())) {
                FALLBACK_CAMIONES.put(c.getPatente(), c);
                return true;
            }
            return false;
        }
        String sql = "UPDATE camion SET marca = ?, modelo = ?, anio = ?, kilometraje_actual = ? WHERE patente = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, c.getMarca());
            pstmt.setString(2, c.getModelo());
            pstmt.setInt(3, c.getAnio());
            pstmt.setInt(4, c.getKilometraje());
            pstmt.setString(5, c.getPatente());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FALLBACK_CAMIONES.put(c.getPatente(), c);
            return true;
        }
    }

    public boolean eliminarCamion(String patente) {
        if (!Conexion.isConectado()) {
            FALLBACK_ASIGNACIONES.remove(patente);
            return FALLBACK_CAMIONES.remove(patente) != null;
        }
        String sql = "DELETE FROM camion WHERE patente = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FALLBACK_ASIGNACIONES.remove(patente);
            return FALLBACK_CAMIONES.remove(patente) != null;
        }
    }

    public boolean registrarKilometraje(Camion c) {
        if (!Conexion.isConectado()) {
            Camion exist = FALLBACK_CAMIONES.get(c.getPatente());
            if (exist != null) {
                exist.setKilometraje(c.getKilometraje());
                return true;
            }
            return false;
        }
        String sql = "UPDATE camion SET kilometraje_actual = ? WHERE patente = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, c.getKilometraje());
            pstmt.setString(2, c.getPatente());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            Camion exist = FALLBACK_CAMIONES.get(c.getPatente());
            if (exist != null) {
                exist.setKilometraje(c.getKilometraje());
                return true;
            }
            return false;
        }
    }

    public List<Camion> listarCamiones() {
        if (!Conexion.isConectado()) {
            return new ArrayList<>(FALLBACK_CAMIONES.values());
        }
        List<Camion> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM camion")) {
            while (rs.next()) {
                lista.add(new Camion(
                        rs.getString("patente"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getInt("anio"),
                        rs.getInt("kilometraje_actual")
                ));
            }
        } catch (Exception e) {
            return new ArrayList<>(FALLBACK_CAMIONES.values());
        }
        return lista;
    }

    public boolean asignarConductor(String patente, int idConductor) {
        if (!Conexion.isConectado()) {
            FALLBACK_ASIGNACIONES.put(patente, idConductor);
            return true;
        }
        String sql = "UPDATE camion SET id_conductor = ? WHERE patente = ?";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idConductor);
            pstmt.setString(2, patente);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            FALLBACK_ASIGNACIONES.put(patente, idConductor);
            return true;
        }
    }

    public List<String[]> listarCamionesConConductor() {
        if (!Conexion.isConectado()) {
            ConductorDAO condDAO = new ConductorDAO();
            List<String[]> res = new ArrayList<>();
            for (Camion c : FALLBACK_CAMIONES.values()) {
                Integer idCond = FALLBACK_ASIGNACIONES.get(c.getPatente());
                String nombreCond = "Sin asignar";
                if (idCond != null) {
                    var conds = condDAO.listarConductores();
                    for (var cd : conds) {
                        if (cd.getIdConductor() == idCond) {
                            nombreCond = cd.getNombre();
                            break;
                        }
                    }
                }
                res.add(new String[]{
                        c.getPatente(),
                        c.getMarca(),
                        c.getModelo(),
                        String.valueOf(c.getAnio()),
                        c.getKilometraje() + " km",
                        nombreCond
                });
            }
            return res;
        }
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT c.patente, c.marca, c.modelo, c.anio, c.kilometraje_actual, " +
                "COALESCE(co.nombre, 'Sin asignar') AS conductor " +
                "FROM camion c LEFT JOIN conductor co ON c.id_conductor = co.id_conductor";
        try (Connection conn = Conexion.getConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new String[]{
                        rs.getString("patente"),
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        String.valueOf(rs.getInt("anio")),
                        rs.getInt("kilometraje_actual") + " km",
                        rs.getString("conductor")
                });
            }
        } catch (SQLException e) {
            return new ArrayList<>(FALLBACK_CAMIONES.values()).stream().map(c -> new String[]{
                    c.getPatente(), c.getMarca(), c.getModelo(), String.valueOf(c.getAnio()), c.getKilometraje() + " km", "Sin asignar"
            }).toList();
        }
        return lista;
    }
}
