package modulo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para la Clase Mantenimiento")
class MantenimientoTest {

    @Test
    @DisplayName("Debe crear registro de Mantenimiento correctamente")
    void testCreacionMantenimiento() {
        LocalDate fecha = LocalDate.now();
        Mantenimiento m = new Mantenimiento(10, "BJKL-45", fecha, "Cambio de frenos");

        assertEquals(10, m.getIdMantenimiento());
        assertEquals("BJKL-45", m.getPatenteCamion());
        assertEquals(fecha, m.getFecha());
        assertEquals("Cambio de frenos", m.getDescripcion());
    }
}
