/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo;
import java.time.LocalDate; // Usamos la librería moderna de fechas
/**
 *
 * @author kevin
 */
public class Mantenimiento {
    
    private int idMantenimiento;
    private String patenteCamion;
    private LocalDate fecha; 
    private String descripcion;

   
    public Mantenimiento(int idMantenimiento, String patenteCamion, LocalDate fecha, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.patenteCamion = patenteCamion;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

   
    public Mantenimiento(String patenteCamion, LocalDate fecha, String descripcion) {
        this.patenteCamion = patenteCamion;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }

   
    public int getIdMantenimiento() { return idMantenimiento; }
    public void setIdMantenimiento(int idMantenimiento) { this.idMantenimiento = idMantenimiento; }

    public String getPatenteCamion() { return patenteCamion; }
    public void setPatenteCamion(String patenteCamion) { this.patenteCamion = patenteCamion; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}