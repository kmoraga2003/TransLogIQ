package vista;

import seguridad.AuthService;

import javax.swing.*;
import java.awt.*;

/**
 * Pantalla de inicio de sesión con soporte de credenciales de prueba (admin / 1234).
 */
public class VistaLogin extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;

    public VistaLogin() {
        setTitle("Transportes Hirata — Acceso al Sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Banner
        JPanel header = new JPanel(new GridLayout(2, 1, 0, 5));
        header.setBackground(new Color(44, 62, 80));
        header.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel title = new JLabel("TRANSPORTES HIRATA", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Sistema Integrado de Gestión y Telemetría", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(180, 190, 200));

        header.add(title);
        header.add(sub);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form Panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(25, 35, 15, 35));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 5, 8, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;

        // Label Usuario
        g.gridy = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(new Color(60, 70, 80));
        form.add(lblUser, g);

        // Input Usuario
        g.gridy = 1;
        txtUsuario = new JTextField("admin");
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        form.add(txtUsuario, g);

        // Label Password
        g.gridy = 2;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(new Color(60, 70, 80));
        form.add(lblPass, g);

        // Input Password
        g.gridy = 3;
        txtPassword = new JPasswordField("1234");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        form.add(txtPassword, g);

        // Botón Ingresar
        g.gridy = 4;
        g.insets = new Insets(20, 5, 10, 5);
        JButton btnLogin = new JButton("Iniciar Sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> ejecutarLogin());
        form.add(btnLogin, g);

        // Presionar Enter para login
        getRootPane().setDefaultButton(btnLogin);

        mainPanel.add(form, BorderLayout.CENTER);

        // Footer Banner (Credenciales de Prueba)
        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 2));
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblHint1 = new JLabel("🔑 Credenciales de Prueba Estándar:", SwingConstants.CENTER);
        lblHint1.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHint1.setForeground(new Color(44, 62, 80));

        JLabel lblHint2 = new JLabel("Usuario: admin | Contraseña: 1234", SwingConstants.CENTER);
        lblHint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint2.setForeground(new Color(100, 110, 120));

        footer.add(lblHint1);
        footer.add(lblHint2);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void ejecutarLogin() {
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (AuthService.autenticar(usuario, pass)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                VistaFlota ventana = new VistaFlota();
                ventana.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }
}
