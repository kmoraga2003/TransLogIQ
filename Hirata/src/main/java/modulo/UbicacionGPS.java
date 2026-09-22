package modulo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de telemetría y posicionamiento GPS para camiones.
 */
public class UbicacionGPS {

    private String patente;
    private double latitud;
    private double longitud;
    private double velocidad; // km/h
    private double rumbo; // 0-360 grados
    private String estado; // "En Ruta", "Detenido", "Alerta Velocidad"
    private LocalDateTime ultimaActualizacion;

    public UbicacionGPS(String patente, double latitud, double longitud, double velocidad, double rumbo, String estado) {
        this.patente = patente;
        this.latitud = latitud;
        this.longitud = longitud;
        this.velocidad = velocidad;
        this.rumbo = rumbo;
        this.estado = estado;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    public String getPatente() { return patente; }
    public void setPatente(String patente) { this.patente = patente; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public double getVelocidad() { return velocidad; }
    public void setVelocidad(double velocidad) { this.velocidad = velocidad; }

    public double getRumbo() { return rumbo; }
    public void setRumbo(double rumbo) { this.rumbo = rumbo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

    public String getHoraFormateada() {
        if (ultimaActualizacion == null) return "--:--:--";
        return ultimaActualizacion.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
