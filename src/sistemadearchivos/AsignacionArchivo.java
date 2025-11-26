/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

/**
 *
 * @author Usuario
 */
public class AsignacionArchivo {

    private final int fileId;
    private String fileName;
    private int firstBlockIndex;
    private int numBlocks;
    private String colorHex;   // para la UI
    private final int creatorPid; // PID del proceso que creó el archivo (-1 si desconocido)

    public AsignacionArchivo(int fileId, String fileName,
                               int firstBlockIndex, int numBlocks,
                               int creatorPid) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.firstBlockIndex = firstBlockIndex;
        this.numBlocks = numBlocks;
        this.creatorPid = creatorPid;
        this.colorHex = "#FFFFFF"; // por defecto
    }

    public int getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFirstBlockIndex() {
        return firstBlockIndex;
    }

    public void setFirstBlockIndex(int firstBlockIndex) {
        this.firstBlockIndex = firstBlockIndex;
    }

    public int getNumBlocks() {
        return numBlocks;
    }

    public void setNumBlocks(int numBlocks) {
        this.numBlocks = numBlocks;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public int getCreatorPid() {
        return creatorPid;
    }
}
