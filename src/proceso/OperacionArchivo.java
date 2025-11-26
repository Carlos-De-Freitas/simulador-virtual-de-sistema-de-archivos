/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proceso;

import sistemadearchivos.NodoDirectorio;
import sistemadearchivos.NodoFs;
import modos.User;

/**
 *
 * @author 58412
 */
public class OperacionArchivo {

    private final TipoOperacionArchivo type;

    // Para CREATE
    private final NodoDirectorio parent;
    private final String name;
    private final int sizeBlocks;
    private final boolean publico;

    private final User owner;

    private final NodoFs targetNode;

    private OperacionArchivo(TipoOperacionArchivo type,
                          NodoDirectorio parent,
                          String name,
                          int sizeBlocks,
                          boolean publico,
                          User owner,
                          NodoFs targetNode) {
        this.type = type;
        this.parent = parent;
        this.name = name;
        this.sizeBlocks = sizeBlocks;
        this.publico = publico;
        this.owner = owner;
        this.targetNode = targetNode;
    }


    public static OperacionArchivo createCreateOperation(NodoDirectorio parent,
                                                      String name,
                                                      int sizeBlocks,
                                                      boolean publico,
                                                      User owner) {
        return new OperacionArchivo(
                TipoOperacionArchivo.CREATE,
                parent,
                name,
                sizeBlocks,
                publico,
                owner,
                null
        );
    }

    public static OperacionArchivo createDeleteOperation(NodoFs target,
                                                      User owner) {
        return new OperacionArchivo(
                TipoOperacionArchivo.DELETE,
                null,
                null,
                0,
                false,
                owner,
                target
        );
    }


    public TipoOperacionArchivo getType() {
        return type;
    }

    public NodoDirectorio getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getSizeBlocks() {
        return sizeBlocks;
    }

    public boolean isPublico() {
        return publico;
    }

    public User getOwner() {
        return owner;
    }

    public NodoFs getTargetNode() {
        return targetNode;
    }
}