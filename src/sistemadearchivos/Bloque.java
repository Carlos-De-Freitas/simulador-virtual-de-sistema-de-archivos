/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

/**
 *
 * @author Usuario
 */
public class Bloque {

    private final int index;          
    private boolean ocupado;
    private int fileId;               
    private int nextBlockIndex;      

    public Bloque(int index) {
        this.index = index;
        this.ocupado = false;
        this.fileId = -1;
        this.nextBlockIndex = -1;
    }

    public int getIndex() {
        return index;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public int getNextBlockIndex() {
        return nextBlockIndex;
    }

    public void setNextBlockIndex(int nextBlockIndex) {
        this.nextBlockIndex = nextBlockIndex;
    }

    public void liberar() {
        this.ocupado = false;
        this.fileId = -1;
        this.nextBlockIndex = -1;
    }
}
