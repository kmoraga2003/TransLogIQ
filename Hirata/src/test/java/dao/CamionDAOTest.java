package dao;

import modulo.Camion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para CamionDAO (CRUD y Fallback)")
class CamionDAOTest {

    private CamionDAO camionDAO;

    @BeforeEach
    void setUp() {
        camionDAO = new CamionDAO();
    }

    @Test
    @DisplayName("Debe listar camiones registrados")
    void testListarCamiones() {
        List<Camion> camiones = camionDAO.listarCamiones();
        assertNotNull(camiones);
        assertFalse(camiones.isEmpty(), "Debe retornar camiones registrados o de fallback");
    }

    @Test
    @DisplayName("Debe agregar, modificar y eliminar un camión de prueba")
    void testCrudCamion() {
        Camion testCamion = new Camion("TEST-99", "Mercedes", "Actros", 2024, 100);

        // Create
        boolean agregado = camionDAO.agregarCamion(testCamion);
        assertTrue(agregado, "Debe agregar el camión exitosamente");

        // Read / Verify
        List<Camion> lista = camionDAO.listarCamiones();
        boolean encontrado = lista.stream().anyMatch(c -> c.getPatente().equals("TEST-99"));
        assertTrue(encontrado, "El camión agregado debe figurar en la lista");

        // Update
        testCamion.setKilometraje(1500);
        boolean modificado = camionDAO.modificarCamion(testCamion);
        assertTrue(modificado, "Debe modificar el camión exitosamente");

        // Delete
        boolean eliminado = camionDAO.eliminarCamion("TEST-99");
        assertTrue(eliminado, "Debe eliminar el camión exitosamente");
    }
}
