package vista;

import dao.CamionDAO;
import dao.ConductorDAO;
import dao.MantenimientoDAO;
import modulo.Camion;
import modulo.Conductor;
import modulo.Mantenimiento;
import service.GpsService;
import Conexion.Conexion;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Ventana Principal de Gestión de Flota, Mantenimiento y Telemetría GPS.
 */
public class VistaFlota extends JFrame {

    static final Color AZUL       = new Color(41, 128, 185);
    static final Color VERDE      = new Color(39, 174, 96);
    static final Color ROJO       = new Color(192, 57, 43);
    static final Color AMARILLO   = new Color(230, 126, 34);
    static final Color MORADO     = new Color(142, 68, 173);
    static final Color FONDO      = new Color(245, 247, 250);
    static final Color BLANCO     = Color.WHITE;
    static final Color GRIS_BORDE = new Color(210, 215, 220);
    static final Color GRIS_TEXTO = new Color(100, 110, 120);
    static final Color HEADER_BG  = new Color(44, 62, 80);

    private final CamionDAO        camionDAO        = new CamionDAO();
    private final ConductorDAO     conductorDAO     = new ConductorDAO();
    private final MantenimientoDAO mantenimientoDAO = new MantenimientoDAO();

    private DefaultTableModel modeloTablaKm;
    private DefaultTableModel modeloTablaInfo;
    private DefaultTableModel modeloTablaConductores;
    private DefaultTableModel modeloTablaMantenimientos;

    public VistaFlota() {
        setTitle("Transportes Hirata — Gestión de Flota y GPS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        setBackground(FONDO);
        setLayout(new BorderLayout());

        add(crearHeader(), BorderLayout.NORTH);
        add(crearTabs(),   BorderLayout.CENTER);
        add(crearFooter(), BorderLayout.SOUTH);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel icono = new JLabel("🚛");
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        JLabel titulo = new JLabel("TRANSPORTES HIRATA");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Sistema Integrado de Gestión y Telemetría");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(180, 190, 200));
        textos.add(titulo);
        textos.add(sub);

        left.add(icono);
        left.add(textos);
        header.add(left, BorderLayout.WEST);

        JLabel usuario = new JLabel("👤 Administrador");
        usuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        usuario.setForeground(new Color(180, 190, 200));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(usuario);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    private JTabbedPane crearTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        tabs.addTab("  Registrar Kilometraje  ", crearPanelKm());
        tabs.addTab("  Camiones y Conductores  ", crearPanelInfo());
        tabs.addTab("  Conductores  ",            crearPanelConductores());
        tabs.addTab("  Mantenimiento  ",          crearPanelMantenimiento());
        tabs.addTab("  Seguimiento GPS  ",        new PanelGPS());

        return tabs;
    }

    // --- TAB 1: REGISTRAR KILOMETRAJE ---
    private JPanel crearPanelKm() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(crearTitulo(
                "Registrar Kilometraje",
                "Ingrese el kilometraje al finalizar cada recorrido para control de flota"
        ), BorderLayout.NORTH);

        String[] cols = {"Patente", "Marca / Modelo", "Año", "Conductor", "Km Actuales"};
        modeloTablaKm = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloTablaKm);

        cargarTablaKm();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(FONDO);

        JButton btnRegistrar = crearBoton("Registrar Kilometraje", AZUL);
        btnRegistrar.addActionListener(e -> dialogoRegistrarKm(tabla));

        botones.add(btnRegistrar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarTablaKm() {
        modeloTablaKm.setRowCount(0);
        try {
            List<String[]> lista = camionDAO.listarCamionesConConductor();
            for (String[] row : lista) {
                modeloTablaKm.addRow(new Object[]{
                        row[0], // Patente
                        row[1] + " " + row[2], // Marca Modelo
                        row[3], // Año
                        row[5], // Conductor
                        row[4]  // Km
                });
            }
        } catch (Exception e) {
            // Error manejado
        }
    }

    private void dialogoRegistrarKm(JTable tabla) {
        JDialog dlg = new JDialog(this, "Registrar Kilometraje", true);
        dlg.setSize(420, 240);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BLANCO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BLANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        form.add(crearLabel("Patente del camión:"), g);
        JTextField tfPatente = crearCampo("Ej: BJKL-45");

        int filaSeleccionada = tabla.getSelectedRow();
        if (filaSeleccionada >= 0) {
            tfPatente.setText((String) modeloTablaKm.getValueAt(filaSeleccionada, 0));
        }
        g.gridx = 1; g.weightx = 0.65;
        form.add(tfPatente, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.35;
        form.add(crearLabel("Nuevo kilometraje:"), g);
        JTextField tfKm = crearCampo("Ej: 2500");
        g.gridx = 1; g.weightx = 0.65;
        form.add(tfKm, g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(BLANCO);
        bp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = crearBoton("Guardar", VERDE);
        btnGuardar.addActionListener(e -> {
            String patente = tfPatente.getText().trim().toUpperCase();
            String kmStr   = tfKm.getText().trim();

            if (patente.isEmpty()) { error(dlg, "Ingrese la patente del camión."); return; }
            if (kmStr.isEmpty() || !kmStr.matches("\\d+")) { error(dlg, "Ingrese un kilometraje válido (solo números)."); return; }
            int km = Integer.parseInt(kmStr);
            if (km <= 0) { error(dlg, "El kilometraje debe ser mayor a cero."); return; }

            Camion c = new Camion(patente, "", "", 0, km);
            boolean ok = camionDAO.registrarKilometraje(c);

            if (ok) {
                dlg.dispose();
                cargarTablaKm();
                cargarTablaInfo();
                if (km >= 5000) {
                    JOptionPane.showMessageDialog(this, "⚠ ¡ALERTA DE MANTENIMIENTO!\nEl camión " + patente + " ha superado los 5,000 km (" + km + " km).", "Mantenimiento Requerido", JOptionPane.WARNING_MESSAGE);
                } else {
                    exito(this, "Kilometraje actualizado a " + km + " km para " + patente + ".");
                }
            } else {
                error(dlg, "No se pudo registrar. Verifique la patente.");
            }
        });

        bp.add(btnCancelar);
        bp.add(btnGuardar);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // --- TAB 2: CAMIONES Y CONDUCTORES ---
    private JPanel crearPanelInfo() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(crearTitulo(
                "Camiones y Conductores",
                "Administre los vehículos de la flota y asigne conductores responsables"
        ), BorderLayout.NORTH);

        String[] cols = {"Patente", "Marca", "Modelo", "Año", "Km Actuales", "Conductor Asignado"};
        modeloTablaInfo = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloTablaInfo);

        cargarTablaInfo();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(FONDO);

        JButton btnNuevo = crearBoton("Agregar Camión", AZUL);
        btnNuevo.addActionListener(e -> dialogoCamion(null));

        JButton btnEditar = crearBoton("Editar", AMARILLO);
        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) { error(this, "Seleccione un camión para editar."); return; }
            String patente = (String) modeloTablaInfo.getValueAt(row, 0);
            String marca = (String) modeloTablaInfo.getValueAt(row, 1);
            String modelo = (String) modeloTablaInfo.getValueAt(row, 2);
            int anio = Integer.parseInt((String) modeloTablaInfo.getValueAt(row, 3));
            String kmStr = ((String) modeloTablaInfo.getValueAt(row, 4)).replace(" km", "").trim();
            int km = Integer.parseInt(kmStr);
            dialogoCamion(new Camion(patente, marca, modelo, anio, km));
        });

        JButton btnEliminar = crearBoton("Eliminar", ROJO);
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) { error(this, "Seleccione un camión para eliminar."); return; }
            String patente = (String) modeloTablaInfo.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el camión con patente " + patente + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (camionDAO.eliminarCamion(patente)) {
                    cargarTablaInfo();
                    cargarTablaKm();
                    exito(this, "Camión eliminado correctamente.");
                } else {
                    error(this, "No se pudo eliminar el camión.");
                }
            }
        });

        JButton btnAsignar = crearBoton("Asignar Conductor", MORADO);
        btnAsignar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) { error(this, "Seleccione un camión de la tabla primero."); return; }
            String patente = (String) modeloTablaInfo.getValueAt(fila, 0);
            dialogoAsignarConductor(patente);
        });

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        botones.add(btnAsignar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarTablaInfo() {
        modeloTablaInfo.setRowCount(0);
        try {
            List<String[]> lista = camionDAO.listarCamionesConConductor();
            for (String[] fila : lista) {
                modeloTablaInfo.addRow(fila);
            }
        } catch (Exception e) {}
    }

    private void dialogoCamion(Camion camionEditar) {
        boolean esNuevo = (camionEditar == null);
        JDialog dlg = new JDialog(this, esNuevo ? "Nuevo Camión" : "Editar Camión", true);
        dlg.setSize(440, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BLANCO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BLANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        JTextField tfPatente  = crearCampo("Ej: BJKL-45");
        JTextField tfMarca    = crearCampo("Ej: Volvo");
        JTextField tfModelo   = crearCampo("Ej: FH16");
        JTextField tfAnio     = crearCampo("Ej: 2022");
        JTextField tfKm       = crearCampo("Ej: 0");

        if (!esNuevo) {
            tfPatente.setText(camionEditar.getPatente());
            tfPatente.setEditable(false);
            tfPatente.setBackground(new Color(236, 240, 241));
            tfMarca.setText(camionEditar.getMarca());
            tfModelo.setText(camionEditar.getModelo());
            tfAnio.setText(String.valueOf(camionEditar.getAnio()));
            tfKm.setText(String.valueOf(camionEditar.getKilometraje()));
        }

        String[] labels = {"Patente:", "Marca:", "Modelo:", "Año:", "Km Iniciales:"};
        Component[] comps = {tfPatente, tfMarca, tfModelo, tfAnio, tfKm};

        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.35;
            form.add(crearLabel(labels[i]), g);
            g.gridx = 1; g.weightx = 0.65;
            form.add(comps[i], g);
        }

        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(BLANCO);
        bp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = crearBoton("Guardar", VERDE);
        btnGuardar.addActionListener(e -> {
            if (tfPatente.getText().trim().isEmpty() || tfMarca.getText().trim().isEmpty() || tfModelo.getText().trim().isEmpty()) {
                error(dlg, "Patente, marca y modelo son obligatorios.");
                return;
            }
            if (!tfAnio.getText().trim().matches("\\d{4}")) {
                error(dlg, "Ingrese un año válido (4 dígitos).");
                return;
            }
            if (!tfKm.getText().trim().matches("\\d+")) {
                error(dlg, "Ingrese un kilometraje válido (solo números).");
                return;
            }

            Camion c = new Camion(
                    tfPatente.getText().trim().toUpperCase(),
                    tfMarca.getText().trim(),
                    tfModelo.getText().trim(),
                    Integer.parseInt(tfAnio.getText().trim()),
                    Integer.parseInt(tfKm.getText().trim())
            );

            boolean ok = esNuevo ? camionDAO.agregarCamion(c) : camionDAO.modificarCamion(c);
            if (ok) {
                // Registrar en servicio GPS para habilitar monitoreo
                GpsService.getInstance().registrarOActualizarCamion(c.getPatente());

                dlg.dispose();
                cargarTablaInfo();
                cargarTablaKm();
                exito(this, "Camión " + c.getPatente() + (esNuevo ? " guardado correctamente." : " actualizado correctamente."));
            } else {
                error(dlg, "No se pudo guardar el camión.");
            }
        });

        bp.add(btnCancelar);
        bp.add(btnGuardar);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // --- TAB 3: CONDUCTORES ---
    private JPanel crearPanelConductores() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(crearTitulo(
                "Conductores",
                "Registre los conductores autorizados para asignarlos a los camiones de la empresa"
        ), BorderLayout.NORTH);

        String[] cols = {"ID", "Nombre Completo"};
        modeloTablaConductores = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloTablaConductores);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(400);

        cargarTablaConductores();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(FONDO);

        JButton btnNuevo = crearBoton("Agregar Conductor", AZUL);
        btnNuevo.addActionListener(e -> dialogoConductor(null));

        JButton btnEditar = crearBoton("Editar", AMARILLO);
        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) { error(this, "Seleccione un conductor para editar."); return; }
            int id = (int) modeloTablaConductores.getValueAt(row, 0);
            String nombre = (String) modeloTablaConductores.getValueAt(row, 1);
            dialogoConductor(new Conductor(id, nombre));
        });

        JButton btnEliminar = crearBoton("Eliminar", ROJO);
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) { error(this, "Seleccione un conductor para eliminar."); return; }
            int id = (int) modeloTablaConductores.getValueAt(row, 0);
            String nombre = (String) modeloTablaConductores.getValueAt(row, 1);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea eliminar al conductor " + nombre + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (conductorDAO.eliminarConductor(id)) {
                    cargarTablaConductores();
                    cargarTablaInfo();
                    exito(this, "Conductor eliminado correctamente.");
                } else {
                    error(this, "No se pudo eliminar al conductor.");
                }
            }
        });

        botones.add(btnNuevo);
        botones.add(btnEditar);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarTablaConductores() {
        modeloTablaConductores.setRowCount(0);
        try {
            List<Conductor> lista = conductorDAO.listarConductores();
            for (Conductor c : lista) {
                modeloTablaConductores.addRow(new Object[]{c.getIdConductor(), c.getNombre()});
            }
        } catch (Exception ex) {}
    }

    private void dialogoConductor(Conductor conductorEditar) {
        boolean esNuevo = (conductorEditar == null);
        JDialog dlg = new JDialog(this, esNuevo ? "Nuevo Conductor" : "Editar Conductor", true);
        dlg.setSize(380, 180);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BLANCO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BLANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        form.add(crearLabel("Nombre completo:"), g);
        JTextField tfNombre = crearCampo("Ej: Juan Pérez");
        if (!esNuevo) {
            tfNombre.setText(conductorEditar.getNombre());
        }
        g.gridx = 1; g.weightx = 0.65;
        form.add(tfNombre, g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(BLANCO);
        bp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = crearBoton("Guardar", VERDE);
        btnGuardar.addActionListener(e -> {
            String nombre = tfNombre.getText().trim();
            if (nombre.isEmpty()) { error(dlg, "Ingrese el nombre del conductor."); return; }

            Conductor c = esNuevo ? new Conductor(nombre) : new Conductor(conductorEditar.getIdConductor(), nombre);
            boolean ok = esNuevo ? conductorDAO.agregarConductor(c) : conductorDAO.modificarConductor(c);
            if (ok) {
                dlg.dispose();
                cargarTablaConductores();
                cargarTablaInfo();
                exito(this, "Conductor guardado correctamente.");
            } else {
                error(dlg, "No se pudo guardar el conductor.");
            }
        });

        bp.add(btnCancelar);
        bp.add(btnGuardar);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void dialogoAsignarConductor(String patente) {
        JDialog dlg = new JDialog(this, "Asignar Conductor a Camión", true);
        dlg.setSize(380, 200);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BLANCO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BLANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        form.add(crearLabel("Camión:"), g);
        JLabel lblPatente = new JLabel(patente);
        lblPatente.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPatente.setForeground(AZUL);
        g.gridx = 1; g.weightx = 0.65;
        form.add(lblPatente, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.35;
        form.add(crearLabel("Conductor:"), g);
        JComboBox<Conductor> cbConductor = new JComboBox<>();
        cbConductor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbConductor.setBackground(Color.WHITE);
        List<Conductor> lista = conductorDAO.listarConductores();
        for (Conductor c : lista) cbConductor.addItem(c);
        if (cbConductor.getItemCount() == 0) {
            error(this, "No hay conductores registrados. Agregue uno primero.");
            return;
        }
        g.gridx = 1; g.weightx = 0.65;
        form.add(cbConductor, g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(BLANCO);
        bp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = crearBoton("Asignar", VERDE);
        btnGuardar.addActionListener(e -> {
            Conductor seleccionado = (Conductor) cbConductor.getSelectedItem();
            if (seleccionado == null) return;
            boolean ok = camionDAO.asignarConductor(patente, seleccionado.getIdConductor());
            if (ok) {
                dlg.dispose();
                cargarTablaInfo();
                cargarTablaKm();
                exito(this, "Conductor " + seleccionado.getNombre() + " asignado a " + patente + ".");
            } else {
                error(dlg, "No se pudo asignar el conductor.");
            }
        });

        bp.add(btnCancelar);
        bp.add(btnGuardar);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // --- TAB 4: MANTENIMIENTO ---
    private JPanel crearPanelMantenimiento() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        panel.add(crearTitulo(
                "Historial de Mantenimientos",
                "Registro de servicios técnicos, revisiones mecánicas y reparaciones preventivas"
        ), BorderLayout.NORTH);

        String[] cols = {"ID", "Patente Camión", "Fecha Servicio", "Descripción de Trabajo"};
        modeloTablaMantenimientos = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTabla(modeloTablaMantenimientos);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(350);

        cargarTablaMantenimiento();

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(GRIS_BORDE));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setBackground(FONDO);

        JButton btnNuevo = crearBoton("Registrar Mantenimiento", AZUL);
        btnNuevo.addActionListener(e -> dialogoNuevoMantenimiento());

        JButton btnEliminar = crearBoton("Eliminar Registro", ROJO);
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) { error(this, "Seleccione un registro de mantenimiento para eliminar."); return; }
            int id = (int) modeloTablaMantenimientos.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "¿Desea borrar este registro de mantenimiento?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (mantenimientoDAO.eliminarMantenimiento(id)) {
                    cargarTablaMantenimiento();
                    exito(this, "Registro eliminado correctamente.");
                } else {
                    error(this, "No se pudo eliminar el registro.");
                }
            }
        });

        botones.add(btnNuevo);
        botones.add(btnEliminar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarTablaMantenimiento() {
        modeloTablaMantenimientos.setRowCount(0);
        try {
            List<Mantenimiento> lista = mantenimientoDAO.listarMantenimientos();
            for (Mantenimiento m : lista) {
                modeloTablaMantenimientos.addRow(new Object[]{
                        m.getIdMantenimiento(),
                        m.getPatenteCamion(),
                        m.getFecha().toString(),
                        m.getDescripcion()
                });
            }
        } catch (Exception e) {}
    }

    private void dialogoNuevoMantenimiento() {
        JDialog dlg = new JDialog(this, "Registrar Mantenimiento", true);
        dlg.setSize(440, 260);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(BLANCO);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BLANCO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(7, 4, 7, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0.35;
        form.add(crearLabel("Camión (Patente):"), g);
        JComboBox<String> cbCamion = new JComboBox<>();
        cbCamion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbCamion.setBackground(Color.WHITE);
        List<Camion> camiones = camionDAO.listarCamiones();
        for (Camion c : camiones) cbCamion.addItem(c.getPatente());
        if (cbCamion.getItemCount() == 0) {
            error(this, "No hay camiones registrados para aplicar mantenimiento.");
            return;
        }
        g.gridx = 1; g.weightx = 0.65;
        form.add(cbCamion, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0.35;
        form.add(crearLabel("Descripción trabajo:"), g);
        JTextField tfDesc = crearCampo("Ej: Cambio de neumáticos y aceite");
        g.gridx = 1; g.weightx = 0.65;
        form.add(tfDesc, g);

        dlg.add(form, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bp.setBackground(BLANCO);
        bp.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));

        JButton btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = crearBoton("Guardar", VERDE);
        btnGuardar.addActionListener(e -> {
            String patente = (String) cbCamion.getSelectedItem();
            String desc = tfDesc.getText().trim();
            if (desc.isEmpty()) { error(dlg, "Ingrese la descripción del mantenimiento."); return; }

            Mantenimiento m = new Mantenimiento(patente, LocalDate.now(), desc);
            boolean ok = mantenimientoDAO.registrarMantenimiento(m);
            if (ok) {
                dlg.dispose();
                cargarTablaMantenimiento();
                exito(this, "Mantenimiento registrado para " + patente + ".");
            } else {
                error(dlg, "No se pudo registrar el mantenimiento.");
            }
        });

        bp.add(btnCancelar);
        bp.add(btnGuardar);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // --- COMPONENTES COMUNES ---
    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(236, 240, 241));
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, GRIS_BORDE));
        footer.setPreferredSize(new Dimension(0, 28));

        JLabel lblStatus = new JLabel("  Sistema listo | Estado Base de Datos: " + (Conexion.isConectado() ? "Conectado a MySQL" : "Modo Autónomo Local"));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(60, 70, 80));
        footer.add(lblStatus, BorderLayout.WEST);

        return footer;
    }

    private JPanel crearTitulo(String titulo, String descripcion) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setBackground(FONDO);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Segoe UI", Font.BOLD, 17));
        t.setForeground(HEADER_BG);
        JLabel d = new JLabel(descripcion);
        d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        d.setForeground(GRIS_TEXTO);
        p.add(t); p.add(d);
        return p;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setForeground(Color.BLACK);
        tabla.setBackground(Color.WHITE);
        tabla.setRowHeight(32);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(HEADER_BG);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabla.setSelectionBackground(new Color(174, 214, 241));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setForeground(Color.BLACK);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (sel) {
                    setBackground(new Color(174, 214, 241));
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 244, 248));
                }
                return this;
            }
        });
        return tabla;
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(color); }
        });
        return btn;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton btn = crearBoton(texto, new Color(189, 195, 199));
        btn.setForeground(new Color(50, 50, 50));
        return btn;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(60, 70, 80));
        return lbl;
    }

    private JTextField crearCampo(String tooltip) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBackground(Color.WHITE);
        tf.setForeground(new Color(30, 30, 30));
        tf.setCaretColor(new Color(30, 30, 30));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_BORDE),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        tf.setToolTipText(tooltip);
        return tf;
    }

    private void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, "  " + msg, "Error de validación", JOptionPane.ERROR_MESSAGE);
    }

    private void exito(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, "  " + msg, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
}