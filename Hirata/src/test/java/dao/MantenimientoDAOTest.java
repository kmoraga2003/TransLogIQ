package dao;

import modulo.Mantenimiento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para MantenimientoDAO")
class MantenimientoDAOTest {

    private MantenimientoDAO mantenimientoDAO;

    @BeforeEach
    void setUp() {
        mantenimientoDAO = new MantenimientoDAO();
    }

    @Test
    @DisplayName("Debe listar registros de mantenimiento")
    void testListarMantenimientos() {
        List<Mantenimiento> lista = mantenimientoDAO.listarMantenimientos();
        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "Debe retornar registros de mantenimiento");
    }

    @Test
    @DisplayName("Debe registrar y eliminar mantenimiento")
    void testRegistrarYEliminar() {
        Mantenimiento m = new Mantenimiento("BJKL-45", LocalDate.now(), "Alineación y balanceo");
        boolean ok = mantenimientoDAO.registrarMantenimiento(m);
        assertTrue(ok, "Debe registrar el mantenimiento");

        List<Mantenimiento> lista = mantenimientoDAO.listarMantenimientos();
        Mantenimiento guardado = lista.stream()
                .filter(item -> "Alineación y balanceo".equals(item.getDescripcion()))
                .findFirst()
                .orElse(null);

        assertNotNull(guardado);
        boolean eliminado = mantenimientoDAO.eliminarMantenimiento(guardado.getIdMantenimiento());
        assertTrue(eliminado, "Debe borrar el registro de mantenimiento");
    }
}
