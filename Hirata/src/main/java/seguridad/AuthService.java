package seguridad;

/**
 * Servicio de autenticación para la aplicación Transportes Hirata.
 */
public class AuthService {

    /**
     * Valida si el usuario y la contraseña proporcionados son correctos.
     * Soporta verificación directa y por Hash SHA-256.
     */
    public static boolean autenticar(String usuario, String password) {
        if (usuario == null || password == null) return false;

        String adminUser = EnvConfig.getAdminUser();
        String expectedHash = EnvConfig.getAdminPasswordHash();
        String inputHash = CryptoUtil.hashPassword(password);

        // Permite usuario 'admin' y contraseña '1234'
        boolean usuarioCorrecto = usuario.trim().equalsIgnoreCase(adminUser);
        boolean passCorrecto = inputHash.equalsIgnoreCase(expectedHash) || password.equals("1234");

        return usuarioCorrecto && passCorrecto;
    }
}
