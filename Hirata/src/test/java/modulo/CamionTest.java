package modulo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias para la Clase Camion")
class CamionTest {

    private Camion camion;

    @BeforeEach
    void setUp() {
        camion = new Camion("BJKL-45", "Volvo", "FH16", 2022, 4500);
    }

    @Test
    @DisplayName("Debe inicializar correctamente las propiedades del Camión")
    void testInicializacionCamion() {
        assertEquals("BJKL-45", camion.getPatente());
        assertEquals("Volvo", camion.getMarca());
        assertEquals("FH16", camion.getModelo());
        assertEquals(2022, camion.getAnio());
        assertEquals(4500, camion.getKilometraje());
    }

    @Test
    @DisplayName("Debe actualizar correctamente las propiedades con Setters")
    void testSettersCamion() {
        camion.setPatente("HT-9900");
        camion.setMarca("Scania");
        camion.setModelo("R500");
        camion.setAnio(2024);
        camion.setKilometraje(6000);

        assertEquals("HT-9900", camion.getPatente());
        assertEquals("Scania", camion.getMarca());
        assertEquals("R500", camion.getModelo());
        assertEquals(2024, camion.getAnio());
        assertEquals(6000, camion.getKilometraje());
    }
}
