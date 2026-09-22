package seguridad;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Cargador y gestor de configuración desde archivo .env con desencriptación segura.
 */
public class EnvConfig {

    private static final Map<String, String> ENV_VARS = new HashMap<>();
    private static boolean loaded = false;

    private static final String DEFAULT_SECRET_KEY = "HirataSecretKey2026!";

    static {
        loadEnv();
    }

    public synchronized static void loadEnv() {
        if (loaded) return;
        
        // Buscar .env en el directorio actual o subdirectorios probables
        File envFile = findEnvFile();
        if (envFile != null && envFile.exists()) {
            try (InputStream is = new FileInputStream(envFile);
                 Scanner scanner = new Scanner(is, StandardCharsets.UTF_8.name())) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    int idx = line.indexOf('=');
                    if (idx > 0) {
                        String key = line.substring(0, idx).trim();
                        String val = line.substring(idx + 1).trim();
                        // Remover comillas si existen
                        if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                            val = val.substring(1, val.length() - 1);
                        }
                        ENV_VARS.put(key, val);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error leyendo archivo .env: " + e.getMessage());
            }
        }
        loaded = true;
    }

    private static File findEnvFile() {
        String[] paths = {
            ".env",
            "Hirata/.env",
            "../.env",
            System.getProperty("user.dir") + File.separator + ".env"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists() && f.isFile()) {
                return f;
            }
        }
        return null;
    }

    public static String get(String key, String defaultValue) {
        if (!loaded) loadEnv();
        String val = ENV_VARS.get(key);
        if (val == null || val.isEmpty()) {
            val = System.getenv(key);
        }
        if (val == null || val.isEmpty()) {
            return defaultValue;
        }

        // Si el valor está encriptado en el .env con formato ENC(...), desencriptar
        if (val.startsWith("ENC(") && val.endsWith(")")) {
            String secretKey = get("APP_SECRET_KEY", DEFAULT_SECRET_KEY);
            return CryptoUtil.decrypt(val, secretKey);
        }
        return val;
    }

    // Getters convenientes
    public static String getDbUrl() {
        return get("DB_URL", "jdbc:mysql://localhost:3306/bd_hirata");
    }

    public static String getDbUser() {
        return get("DB_USER", "root");
    }

    public static String getDbPassword() {
        return get("DB_PASSWORD", "root");
    }

    public static String getAdminUser() {
        return get("ADMIN_USER", "admin");
    }

    public static String getAdminPasswordHash() {
        // Hash SHA-256 de '1234' por defecto
        String defaultHash = CryptoUtil.hashPassword("1234");
        return get("ADMIN_PASSWORD_HASH", defaultHash);
    }
}
