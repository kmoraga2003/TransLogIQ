package seguridad;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * Utilidad de Cifrado y Hashing para credenciales del sistema.
 */
public class CryptoUtil {

    private static final String ALGORITHM = "AES";
    private static final String DEFAULT_SECRET = "HirataTransporteSecretKey2026!";

    /**
     * Genera un Hash SHA-256 de una contraseña.
     */
    public static String hashPassword(String password) {
        if (password == null) return "";
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    /**
     * Encripta una cadena utilizando AES y una clave secreta.
     */
    public static String encrypt(String value, String secretKey) {
        try {
            SecretKeySpec keySpec = buildKey(secretKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            return value; // Fallback
        }
    }

    /**
     * Desencripta una cadena cifrada en Base64 utilizando AES.
     */
    public static String decrypt(String encryptedValue, String secretKey) {
        if (encryptedValue == null || encryptedValue.isEmpty()) return "";
        try {
            // Elimina wrapper ENC(...) si existe
            if (encryptedValue.startsWith("ENC(") && encryptedValue.endsWith(")")) {
                encryptedValue = encryptedValue.substring(4, encryptedValue.length() - 1);
            }
            SecretKeySpec keySpec = buildKey(secretKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] originalBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(originalBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Si no está encriptado o falla la clave, retorna el valor original
            return encryptedValue;
        }
    }

    private static SecretKeySpec buildKey(String secretKey) {
        try {
            if (secretKey == null || secretKey.isEmpty()) secretKey = DEFAULT_SECRET;
            byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            // Tomamos los primeros 16 bytes (128-bit AES)
            byte[] key16 = new byte[16];
            System.arraycopy(key, 0, key16, 0, 16);
            return new SecretKeySpec(key16, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException("Error creando clave de cifrado", e);
        }
    }
}
