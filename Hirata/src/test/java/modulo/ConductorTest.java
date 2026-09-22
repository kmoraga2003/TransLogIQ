package modulo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para la Clase Conductor")
class ConductorTest {

    @Test
    @DisplayName("Debe crear Conductor con ID y Nombre")
    void testConductorConId() {
        Conductor conductor = new Conductor(1, "Juan Pérez");
        assertEquals(1, conductor.getIdConductor());
        assertEquals("Juan Pérez", conductor.getNombre());
        assertEquals("Juan Pérez", conductor.toString());
    }

    @Test
    @DisplayName("Debe crear Conductor solo con Nombre")
    void testConductorSinId() {
        Conductor conductor = new Conductor("Carlos Gómez");
        assertEquals("Carlos Gómez", conductor.getNombre());

        conductor.setIdConductor(5);
        assertEquals(5, conductor.getIdConductor());
    }
}
