package dao;

import modulo.Conductor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para ConductorDAO (CRUD y Fallback)")
class ConductorDAOTest {

    private ConductorDAO conductorDAO;

    @BeforeEach
    void setUp() {
        conductorDAO = new ConductorDAO();
    }

    @Test
    @DisplayName("Debe listar conductores")
    void testListarConductores() {
        List<Conductor> conductores = conductorDAO.listarConductores();
        assertNotNull(conductores);
        assertFalse(conductores.isEmpty(), "Debe retornar la lista de conductores");
    }

    @Test
    @DisplayName("Debe agregar, editar y eliminar un conductor de prueba")
    void testCrudConductor() {
        Conductor conductor = new Conductor("Pedro Ramírez");
        boolean ok = conductorDAO.agregarConductor(conductor);
        assertTrue(ok, "Debe registrar al conductor");

        List<Conductor> lista = conductorDAO.listarConductores();
        Conductor guardado = lista.stream()
                .filter(c -> "Pedro Ramírez".equals(c.getNombre()))
                .findFirst()
                .orElse(null);

        assertNotNull(guardado, "El conductor recién agregado debe ser localizado");

        guardado.setNombre("Pedro Ramírez Modificado");
        boolean modificado = conductorDAO.modificarConductor(guardado);
        assertTrue(modificado, "Debe modificar al conductor");

        boolean eliminado = conductorDAO.eliminarConductor(guardado.getIdConductor());
        assertTrue(eliminado, "Debe eliminar al conductor");
    }
}
