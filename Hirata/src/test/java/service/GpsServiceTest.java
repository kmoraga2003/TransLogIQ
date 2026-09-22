package service;

import modulo.UbicacionGPS;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para el Servicio GPS de Flota")
class GpsServiceTest {

    private GpsService gpsService;

    @BeforeEach
    void setUp() {
        gpsService = GpsService.getInstance();
    }

    @AfterEach
    void tearDown() {
        gpsService.detenerSimulacion();
    }

    @Test
    @DisplayName("Debe devolver las posiciones iniciales de los camiones registrados")
    void testPosicionesIniciales() {
        Map<String, UbicacionGPS> pos = gpsService.getPosiciones();
        assertNotNull(pos);
        assertTrue(pos.containsKey("BJKL-45"));
        assertTrue(pos.containsKey("FH-2023"));
    }

    @Test
    @DisplayName("Debe iniciar y detener la simulación de movimiento GPS")
    void testIniciarYDetenerSimulacion() {
        assertFalse(gpsService.isCorriendo());
        gpsService.iniciarSimulacion();
        assertTrue(gpsService.isCorriendo());
        gpsService.detenerSimulacion();
        assertFalse(gpsService.isCorriendo());
    }

    @Test
    @DisplayName("Debe simular alerta de exceso de velocidad")
    void testSimularAlerta() {
        gpsService.simularAlerta("BJKL-45");
        UbicacionGPS ubicacion = gpsService.getUbicacion("BJKL-45");
        assertNotNull(ubicacion);
        assertEquals("Alerta Velocidad", ubicacion.getEstado());
        assertTrue(ubicacion.getVelocidad() > 90.0);
    }
}
