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
public class NodoArchivo extends NodoFs {

    private int fileId;
    private int sizeBlocks;

    public NodoArchivo(String nombre, NodoDirectorio padre, User propietario, boolean publico,
                    int fileId, int sizeBlocks) {
        super(nombre, padre, propietario, publico);
        this.fileId = fileId;
        this.sizeBlocks = sizeBlocks;
    }

    @Override
    public boolean esDirectorio() {
        return false;
    }

    public int getFileId() {
        return fileId;
    }

    public int getSizeBlocks() {
        return sizeBlocks;
    }

    public void setSizeBlocks(int sizeBlocks) {
        this.sizeBlocks = sizeBlocks;
    }
}
