/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

import modos.User;

/**
 *
 * @author 58412
 */
public abstract class NodoFs {

    protected String nombre;
    protected NodoDirectorio padre;
    protected User propietario;
    protected boolean publico;

    public NodoFs(String nombre, NodoDirectorio padre, User propietario, boolean publico) {
        this.nombre = nombre;
        this.padre = padre;
        this.propietario = propietario;
        this.publico = publico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre, boolean esAdmin) {
        if (!esAdmin) {
            throw new SecurityException("Solo el administrador puede renombrar nodos.");
        }
        this.nombre = nombre;
    }

    public NodoDirectorio getPadre() {
        return padre;
    }

    public void setPadre(NodoDirectorio padre) {
        this.padre = padre;
    }

    public User getPropietario() {
        return propietario;
    }

    public boolean isPublico() {
        return publico;
    }

    public void setPublico(boolean publico) {
        this.publico = publico;
    }

    public abstract boolean esDirectorio();

    @Override
    public String toString() {
        return nombre;
    }
}
