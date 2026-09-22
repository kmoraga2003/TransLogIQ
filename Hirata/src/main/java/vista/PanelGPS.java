package vista;

import modulo.UbicacionGPS;
import service.GpsService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * Panel de Seguimiento GPS en tiempo real con Mapa Interactivo Swing 2D.
 */
public class PanelGPS extends JPanel {

    private final GpsService gpsService = GpsService.getInstance();
    private DefaultTableModel modeloTabla;
    private JTable tablaGPS;
    private MapaCanvas mapaCanvas;
    private String patenteSeleccionada = null;

    public PanelGPS() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header / Control bar
        add(crearBarraControles(), BorderLayout.NORTH);

        // Center split: Left Map Canvas, Right Realtime Table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.60);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(null);

        mapaCanvas = new MapaCanvas();
        splitPane.setLeftComponent(mapaCanvas);

        splitPane.setRightComponent(crearPanelTabla());

        add(splitPane, BorderLayout.CENTER);

        // Escuchar actualizaciones del GpsService
        gpsService.addListener(this::actualizarDatos);

        // Iniciar simulación por defecto
        gpsService.iniciarSimulacion();
    }

    private JPanel crearBarraControles() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel titulo = new JLabel(" Seguimiento GPS y Telemetría en Vivo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(new Color(44, 62, 80));
        JLabel sub = new JLabel("Monitoreo de posicionamiento, velocidad y estados de la flota en mapa 2D");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(100, 110, 120));
        info.add(titulo);
        info.add(sub);

        bar.add(info, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);

        JButton btnStart = new JButton("▶ Iniciar GPS");
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnStart.setBackground(new Color(39, 174, 96));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e -> gpsService.iniciarSimulacion());

        JButton btnPause = new JButton("⏸ Pausar GPS");
        btnPause.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPause.setBackground(new Color(230, 126, 34));
        btnPause.setForeground(Color.WHITE);
        btnPause.setFocusPainted(false);
        btnPause.addActionListener(e -> gpsService.detenerSimulacion());

        JButton btnAlerta = new JButton("⚠ Simular Alerta");
        btnAlerta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAlerta.setBackground(new Color(192, 57, 43));
        btnAlerta.setForeground(Color.WHITE);
        btnAlerta.setFocusPainted(false);
        btnAlerta.addActionListener(e -> {
            if (patenteSeleccionada != null) {
                gpsService.simularAlerta(patenteSeleccionada);
            } else {
                gpsService.simularAlerta("BJKL-45");
            }
        });

        btns.add(btnStart);
        btns.add(btnPause);
        btns.add(btnAlerta);

        bar.add(btns, BorderLayout.EAST);
        return bar;
    }

    private JPanel crearPanelTabla() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 220)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JLabel title = new JLabel(" Telemetría en Tiempo Real");
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(new Color(44, 62, 80));
        p.add(title, BorderLayout.NORTH);

        String[] cols = {"Patente", "Velocidad", "Estado", "Últ. Hora"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaGPS = new JTable(modeloTabla);
        tablaGPS.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaGPS.setRowHeight(28);
        tablaGPS.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaGPS.getTableHeader().setBackground(new Color(44, 62, 80));
        tablaGPS.getTableHeader().setForeground(Color.WHITE);

        // Custom Cell Renderer
        tablaGPS.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = (String) table.getValueAt(row, 2);
                if (isSelected) {
                    setBackground(new Color(174, 214, 241));
                    setForeground(Color.BLACK);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
                    if ("Alerta Velocidad".equals(estado)) {
                        setForeground(new Color(192, 57, 43));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("Detenido".equals(estado)) {
                        setForeground(new Color(230, 126, 34));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        setForeground(new Color(39, 174, 96));
                        setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
                return this;
            }
        });

        tablaGPS.getSelectionModel().addListSelectionListener(e -> {
            int row = tablaGPS.getSelectedRow();
            if (row >= 0) {
                patenteSeleccionada = (String) modeloTabla.getValueAt(row, 0);
                mapaCanvas.repaint();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaGPS);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);

        return p;
    }

    private void actualizarDatos(Map<String, UbicacionGPS> datos) {
        SwingUtilities.invokeLater(() -> {
            modeloTabla.setRowCount(0);
            for (UbicacionGPS u : datos.values()) {
                modeloTabla.addRow(new Object[]{
                        u.getPatente(),
                        u.getVelocidad() + " km/h",
                        u.getEstado(),
                        u.getHoraFormateada()
                });
            }
            mapaCanvas.setPosiciones(datos);
        });
    }

    /**
     * Canvas 2D interactivo para la representación visual del mapa y los camiones.
     */
    private class MapaCanvas extends JPanel {
        private Map<String, UbicacionGPS> posiciones;
        private static final double CENTER_LAT = -33.4489;
        private static final double CENTER_LON = -70.6693;

        public MapaCanvas() {
            setBackground(new Color(230, 238, 245));
            setBorder(BorderFactory.createLineBorder(new Color(200, 210, 220)));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (posiciones == null) return;
                    int w = getWidth();
                    int h = getHeight();
                    for (UbicacionGPS pos : posiciones.values()) {
                        Point pt = coordsToScreen(pos.getLatitud(), pos.getLongitud(), w, h);
                        if (pt.distance(e.getPoint()) < 20) {
                            patenteSeleccionada = pos.getPatente();
                            repaint();
                            break;
                        }
                    }
                }
            });
        }

        public void setPosiciones(Map<String, UbicacionGPS> posiciones) {
            this.posiciones = posiciones;
            repaint();
        }

        private Point coordsToScreen(double lat, double lon, int width, int height) {
            double scale = 3500.0;
            int x = (int) (width / 2.0 + (lon - CENTER_LON) * scale);
            int y = (int) (height / 2.0 - (lat - CENTER_LAT) * scale);
            return new Point(x, y);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Dibujar rejilla / calles simuladas
            g2.setColor(new Color(215, 225, 235));
            for (int x = 0; x < w; x += 40) g2.drawLine(x, 0, x, h);
            for (int y = 0; y < h; y += 40) g2.drawLine(0, y, w, y);

            // Carreteras principales
            g2.setStroke(new BasicStroke(6f));
            g2.setColor(new Color(255, 255, 255));
            g2.drawLine(0, h / 2, w, h / 2);
            g2.drawLine(w / 2, 0, w / 2, h);
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(241, 196, 15));
            g2.drawLine(0, h / 2, w, h / 2);
            g2.drawLine(w / 2, 0, w / 2, h);

            // Leyenda de Brújula
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            g2.setColor(new Color(100, 110, 120));
            g2.drawString("Norte ▲", w - 60, 25);
            g2.drawString("Escala: 1:50.000", 15, h - 15);

            if (posiciones == null || posiciones.isEmpty()) {
                g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                g2.drawString("Cargando posiciones GPS...", w / 2 - 80, h / 2);
                return;
            }

            // Dibujar camiones
            for (UbicacionGPS pos : posiciones.values()) {
                Point p = coordsToScreen(pos.getLatitud(), pos.getLongitud(), w, h);
                boolean esSeleccionado = pos.getPatente().equals(patenteSeleccionada);

                Color colorIcono;
                if ("Alerta Velocidad".equals(pos.getEstado())) {
                    colorIcono = new Color(192, 57, 43);
                } else if ("Detenido".equals(pos.getEstado())) {
                    colorIcono = new Color(230, 126, 34);
                } else {
                    colorIcono = new Color(39, 174, 96);
                }

                // Halo de selección
                if (esSeleccionado) {
                    g2.setColor(new Color(41, 128, 185, 100));
                    g2.fillOval(p.x - 22, p.y - 22, 44, 44);
                    g2.setColor(new Color(41, 128, 185));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawOval(p.x - 22, p.y - 22, 44, 44);
                }

                // Marcador del camión
                g2.setColor(colorIcono);
                g2.fillOval(p.x - 12, p.y - 12, 24, 24);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(p.x - 12, p.y - 12, 24, 24);

                // Flecha de Dirección
                AffineTransform old = g2.getTransform();
                g2.translate(p.x, p.y);
                g2.rotate(Math.toRadians(pos.getRumbo()));
                g2.setColor(Color.BLACK);
                Polygon arrow = new Polygon();
                arrow.addPoint(0, -10);
                arrow.addPoint(-5, 0);
                arrow.addPoint(5, 0);
                g2.fill(arrow);
                g2.setTransform(old);

                // Etiqueta con Patente y Velocidad
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(new Color(44, 62, 80));
                g2.drawString(pos.getPatente(), p.x + 15, p.y - 4);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(new Color(100, 100, 100));
                g2.drawString(pos.getVelocidad() + " km/h", p.x + 15, p.y + 10);
            }
        }
    }
}
