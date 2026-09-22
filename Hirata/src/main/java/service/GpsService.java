package service;

import modulo.UbicacionGPS;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Servicio de simulación y seguimiento GPS en tiempo real para camiones.
 */
public class GpsService {

    private static GpsService instance;
    private final Map<String, UbicacionGPS> posiciones = new ConcurrentHashMap<>();
    private ScheduledExecutorService executor;
    private boolean corriendo = false;
    private final List<Consumer<Map<String, UbicacionGPS>>> listeners = new ArrayList<>();
    private final Random random = new Random();

    // Coordenadas base (Centro de ruta de transporte)
    private static final double BASE_LAT = -33.4489; // Santiago, Chile
    private static final double BASE_LON = -70.6693;

    private GpsService() {
        // Inicializa algunas ubicaciones por defecto para la flota
        posiciones.put("BJKL-45", new UbicacionGPS("BJKL-45", BASE_LAT + 0.02, BASE_LON - 0.03, 75.0, 45.0, "En Ruta"));
        posiciones.put("FH-2023", new UbicacionGPS("FH-2023", BASE_LAT - 0.04, BASE_LON + 0.05, 0.0, 180.0, "Detenido"));
        posiciones.put("HT-9900", new UbicacionGPS("HT-9900", BASE_LAT + 0.08, BASE_LON + 0.02, 92.5, 270.0, "Alerta Velocidad"));
    }

    public static synchronized GpsService getInstance() {
        if (instance == null) {
            instance = new GpsService();
        }
        return instance;
    }

    public synchronized void addListener(Consumer<Map<String, UbicacionGPS>> listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(Consumer<Map<String, UbicacionGPS>> listener) {
        listeners.remove(listener);
    }

    public Map<String, UbicacionGPS> getPosiciones() {
        return Collections.unmodifiableMap(posiciones);
    }

    public UbicacionGPS getUbicacion(String patente) {
        return posiciones.get(patente);
    }

    public void registrarOActualizarCamion(String patente) {
        if (!posiciones.containsKey(patente)) {
            double offsetLat = (random.nextDouble() - 0.5) * 0.1;
            double offsetLon = (random.nextDouble() - 0.5) * 0.1;
            posiciones.put(patente, new UbicacionGPS(patente, BASE_LAT + offsetLat, BASE_LON + offsetLon, 60.0, random.nextInt(360), "En Ruta"));
            notificarListeners();
        }
    }

    public synchronized void iniciarSimulacion() {
        if (corriendo) return;
        corriendo = true;
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::actualizarSimulacion, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void detenerSimulacion() {
        if (!corriendo) return;
        corriendo = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    public boolean isCorriendo() {
        return corriendo;
    }

    private void actualizarSimulacion() {
        for (Map.Entry<String, UbicacionGPS> entry : posiciones.entrySet()) {
            UbicacionGPS pos = entry.getValue();
            if ("Detenido".equals(pos.getEstado())) {
                // Pequeña probabilidad de volver a arrancar
                if (random.nextDouble() < 0.1) {
                    pos.setEstado("En Ruta");
                    pos.setVelocidad(40 + random.nextInt(40));
                }
                continue;
            }

            // Mover coordenadas según rumbo y velocidad
            double deltaLat = (Math.cos(Math.toRadians(pos.getRumbo())) * pos.getVelocidad() * 0.000005);
            double deltaLon = (Math.sin(Math.toRadians(pos.getRumbo())) * pos.getVelocidad() * 0.000005);

            pos.setLatitud(pos.getLatitud() + deltaLat);
            pos.setLongitud(pos.getLongitud() + deltaLon);
            pos.setUltimaActualizacion(java.time.LocalDateTime.now());

            // Pequeña variación de rumbo y velocidad
            double nuevoRumbo = (pos.getRumbo() + (random.nextDouble() - 0.5) * 5) % 360;
            if (nuevoRumbo < 0) nuevoRumbo += 360;
            pos.setRumbo(nuevoRumbo);

            double nuevaVel = pos.getVelocidad() + (random.nextDouble() - 0.5) * 4;
            if (nuevaVel < 0) nuevaVel = 0;
            if (nuevaVel > 120) nuevaVel = 120;
            pos.setVelocidad(Math.round(nuevaVel * 10.0) / 10.0);

            if (pos.getVelocidad() > 90) {
                pos.setEstado("Alerta Velocidad");
            } else if (pos.getVelocidad() == 0) {
                pos.setEstado("Detenido");
            } else {
                pos.setEstado("En Ruta");
            }
        }
        notificarListeners();
    }

    private void notificarListeners() {
        Map<String, UbicacionGPS> copia = new HashMap<>(posiciones);
        for (Consumer<Map<String, UbicacionGPS>> listener : listeners) {
            try {
                listener.accept(copia);
            } catch (Exception e) {
                // Ignore listener exceptions
            }
        }
    }

    public void simularAlerta(String patente) {
        UbicacionGPS pos = posiciones.get(patente);
        if (pos != null) {
            pos.setVelocidad(115.0);
            pos.setEstado("Alerta Velocidad");
            notificarListeners();
        }
    }
}
