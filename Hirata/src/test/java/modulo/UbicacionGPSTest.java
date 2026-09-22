package modulo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para UbicacionGPS")
class UbicacionGPSTest {

    @Test
    @DisplayName("Debe gestionar coordenadas, velocidad y horas formateadas")
    void testUbicacionGPS() {
        UbicacionGPS gps = new UbicacionGPS("BJKL-45", -33.4489, -70.6693, 85.0, 90.0, "En Ruta");

        assertEquals("BJKL-45", gps.getPatente());
        assertEquals(-33.4489, gps.getLatitud(), 0.0001);
        assertEquals(-70.6693, gps.getLongitud(), 0.0001);
        assertEquals(85.0, gps.getVelocidad(), 0.1);
        assertEquals("En Ruta", gps.getEstado());
        assertNotNull(gps.getHoraFormateada());
    }
}
