package seguridad;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Cifrado y Autenticación (.env & AES)")
class SecurityTest {

    @Test
    @DisplayName("Debe generar Hash SHA-256 consistente para contraseñas")
    void testHashPassword() {
        String hash1234 = CryptoUtil.hashPassword("1234");
        assertNotNull(hash1234);
        assertFalse(hash1234.isEmpty());
        assertEquals(hash1234, CryptoUtil.hashPassword("1234"));
        assertNotEquals(hash1234, CryptoUtil.hashPassword("4321"));
    }

    @Test
    @DisplayName("Debe encriptar y desencriptar valores con AES")
    void testAESEncryptionDecryption() {
        String original = "PasswordSecreta2026!";
        String secretKey = "HirataKeyTest";

        String encrypted = CryptoUtil.encrypt(original, secretKey);
        assertNotEquals(original, encrypted);

        String decrypted = CryptoUtil.decrypt(encrypted, secretKey);
        assertEquals(original, decrypted);
    }

    @Test
    @DisplayName("Debe validar autenticación con credenciales estándar admin / 1234")
    void testAutenticacionEstandar() {
        assertTrue(AuthService.autenticar("admin", "1234"), "Debe permitir acceso con admin/1234");
        assertTrue(AuthService.autenticar("ADMIN", "1234"), "Debe ser insensible a mayúsculas en el usuario");
        assertFalse(AuthService.autenticar("admin", "wrongpassword"), "Debe rechazar contraseña incorrecta");
        assertFalse(AuthService.autenticar("invaliduser", "1234"), "Debe rechazar usuario no registrado");
    }

    @Test
    @DisplayName("Debe cargar valores por defecto si no existen en .env")
    void testEnvConfigDefaults() {
        assertEquals("admin", EnvConfig.getAdminUser());
        assertNotNull(EnvConfig.getDbUrl());
        assertNotNull(EnvConfig.getDbUser());
    }
}
