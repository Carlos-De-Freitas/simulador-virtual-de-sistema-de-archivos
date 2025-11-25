/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

/**
 *
 * @author Usuario
 */
public class Disco {

    private final Bloque[] blocks;

    public Disco(int numBlocks) {
        if (numBlocks <= 0) {
            throw new IllegalArgumentException("numBlocks debe ser > 0");
        }
        this.blocks = new Bloque[numBlocks];
        for (int i = 0; i < numBlocks; i++) {
            this.blocks[i] = new Bloque(i);
        }
    }

    public int getNumBlocks() {
        return blocks.length;
    }

    public Bloque getBlock(int index) {
        if (index < 0 || index >= blocks.length) {
            throw new IndexOutOfBoundsException("Índice de bloque inválido: " + index);
        }
        return blocks[index];
    }

    public int findFreeBlock() {
        for (int i = 0; i < blocks.length; i++) {
            if (!blocks[i].isOcupado()) {
                return i;
            }
        }
        return -1;
    }

    public boolean hasFreeBlocks(int cantidad) {
        int libres = 0;
        for (int i = 0; i < blocks.length; i++) {
            if (!blocks[i].isOcupado()) {
                libres++;
                if (libres >= cantidad) {
                    return true;
                }
            }
        }
        return false;
    }
}
