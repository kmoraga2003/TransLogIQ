/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulo;

/**
 *
 * @author kevin
 */
public class Conductor {
    
    private int idConductor;
    private String nombre;

   
    public Conductor(int idConductor, String nombre) {
        this.idConductor = idConductor;
        this.nombre = nombre;
    }

   
    public Conductor(String nombre) {
        this.nombre = nombre;
    }

   
    public int getIdConductor() { return idConductor; }
    public String getNombre() { return nombre; }

   
    public void setIdConductor(int idConductor) { this.idConductor = idConductor; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    @Override
public String toString() {
    return nombre;
}
}
    

