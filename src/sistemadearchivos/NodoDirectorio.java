/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

import estructuras.ListaSimple;
import modos.User;

/**
 *
 * @author 58412
 */
public class NodoDirectorio extends NodoFs {

    private final ListaSimple<NodoFs> children;

    public NodoDirectorio(String nombre, NodoDirectorio padre, User propietario, boolean publico) {
        super(nombre, padre, propietario, publico);
        this.children = new ListaSimple<>();
    }

    @Override
    public boolean esDirectorio() {
        return true;
    }

    public int getChildrenCount() {
        return children.size();
    }

    public NodoFs getChildAt(int index) {
        return children.get(index);
    }

    public void addChild(NodoFs child) {
        if (child == null) return;
        children.addLast(child);
        child.setPadre(this);
    }

    public NodoFs findChildByName(String name) {
        int n = children.size();
        for (int i = 0; i < n; i++) {
            NodoFs node = children.get(i);
            if (node.getNombre().equals(name)) {
                return node;
            }
        }
        return null;
    }

    public boolean removeChild(NodoFs child) {
        int n = children.size();
        for (int i = 0; i < n; i++) {
            if (children.get(i) == child) {
                children.removeAt(i);
                return true;
            }
        }
        return false;
    }
}