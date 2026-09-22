package st.hirata;

import vista.VistaLogin;

/**
 * Punto de entrada principal de la aplicación Transportes Hirata.
 */
public class Hirata { 

    public static void main(String[] args) {
        // CrossPlatformLookAndFeel = Look&Feel propio de Java
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getCrossPlatformLookAndFeelClassName()
            );
        } catch (Exception e) {
            // Fallback por defecto
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            VistaLogin login = new VistaLogin();
            login.setVisible(true);
        });
    }
}
