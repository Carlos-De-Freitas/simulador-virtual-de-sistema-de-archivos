/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

import java.awt.Color;
import modos.User;
import estructuras.ListaSimple;

/**
 *
 * @author Usuario
 */
public class SistemaArchivo {

    private final Disco disk;
    private final DirectoryNode root;
    private int nextFileId;

    private final ListaSimple<AsignacionArchivo> allocationTable;

    public SistemaArchivo(int numBlocks, User rootUser) {
        this.disk = new Disco(numBlocks);
        this.root = new DirectoryNode("/", null, rootUser, true);
        this.nextFileId = 1;
        this.allocationTable = new ListaSimple<>();
    }

    public Disco getDisk() {
        return disk;
    }

    public DirectoryNode getRoot() {
        return root;
    }

    public ListaSimple<AsignacionArchivo> getAllocationTable() {
        return allocationTable;
    }

    
    public DirectoryNode createDirectory(DirectoryNode parent, String name,
                                         User owner, boolean publico) {

        if (!owner.isAdmin()) {
            throw new SecurityException("Solo el administrador puede crear directorios.");
        }

        if (parent == null) {
            parent = root;
        }

        if (parent.findChildByName(name) != null) {
            throw new IllegalArgumentException("Ya existe un archivo/directorio con ese nombre.");
        }

        DirectoryNode dir = new DirectoryNode(name, parent, owner, publico);
        parent.addChild(dir);
        return dir;
    }

    
    public FileNode createFile(DirectoryNode parent, String name, int sizeBlocks,
                               User owner, boolean publico, int creatorPid) {

        if (!owner.isAdmin()) {
            throw new SecurityException("Solo el administrador puede crear archivos.");
        }

        if (sizeBlocks <= 0) {
            throw new IllegalArgumentException("El tamaño en bloques debe ser > 0.");
        }

        if (parent == null) {
            parent = root;
        }

        if (parent.findChildByName(name) != null) {
            throw new IllegalArgumentException("Ya existe un archivo/directorio con ese nombre.");
        }

        if (!disk.hasFreeBlocks(sizeBlocks)) {
            throw new IllegalStateException("No hay espacio suficiente en disco.");
        }

        int fileId = nextFileId++;
        int firstBlockIndex = allocateBlocksForFile(fileId, sizeBlocks);

        FileNode fileNode = new FileNode(name, parent, owner, publico, fileId, sizeBlocks);
        parent.addChild(fileNode);

        AsignacionArchivo entry =
                new AsignacionArchivo(fileId, name, firstBlockIndex, sizeBlocks, creatorPid);

        
        entry.setColorHex(generateColorHex(fileId));

        allocationTable.addLast(entry);

        return fileNode;
    }


    public void renameNode(FsNode node, String newName) {

        if (node == null || node == root) {
            throw new IllegalArgumentException("Nodo inválido para renombrar.");
        }
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre inválido.");
        }

        DirectoryNode parent;
        if (node.esDirectorio()) {
            parent = ((DirectoryNode) node).getPadre();
        } else {
            parent = ((FileNode) node).getPadre();
        }
        if (parent == null) {
            throw new IllegalStateException("El nodo no tiene padre.");
        }

        FsNode existing = parent.findChildByName(newName.trim());
        if (existing != null && existing != node) {
            throw new IllegalArgumentException("Ya existe otro nodo con ese nombre en el mismo directorio.");
        }

        
        node.setNombre(newName.trim(), true);

        
        if (!node.esDirectorio()) {
            FileNode fileNode = (FileNode) node;
            AsignacionArchivo entry = findAllocationEntryByFileId(fileNode.getFileId());
            if (entry != null) {
                entry.setFileName(newName.trim());
            }
        }
    }


    public void deleteNode(FsNode node, User requester) {

        if (!requester.isAdmin()) {
            throw new SecurityException("Solo el administrador puede eliminar nodos.");
        }

        if (node == null || node == root) {
            throw new IllegalArgumentException("Nodo inválido para eliminar.");
        }

        if (node.esDirectorio()) {
            DirectoryNode dir = (DirectoryNode) node;
            while (dir.getChildrenCount() > 0) {
                FsNode child = dir.getChildAt(0);
                deleteNode(child, requester);
            }
            DirectoryNode parent = dir.getPadre();
            if (parent != null) {
                parent.removeChild(dir);
            }
        } else {
            FileNode fileNode = (FileNode) node;
            int fileId = fileNode.getFileId();

            AsignacionArchivo entry = findAllocationEntryByFileId(fileId);
            if (entry != null) {
                freeBlocksOfFile(entry);
                removeAllocationEntry(fileId);
            }

            DirectoryNode parent = fileNode.getPadre();
            if (parent != null) {
                parent.removeChild(fileNode);
            }
        }
    }


    private int allocateBlocksForFile(int fileId, int sizeBlocks) {
        int firstBlockIndex = -1;
        int previousIndex = -1;

        for (int i = 0; i < sizeBlocks; i++) {
            int index = disk.findFreeBlock();
            if (index == -1) {
                throw new IllegalStateException("Fallo inesperado: no hay suficientes bloques libres.");
            }

            Bloque b = disk.getBlock(index);
            b.setOcupado(true);
            b.setFileId(fileId);
            b.setNextBlockIndex(-1);

            if (firstBlockIndex == -1) {
                firstBlockIndex = index;
            }
            if (previousIndex != -1) {
                Bloque prev = disk.getBlock(previousIndex);
                prev.setNextBlockIndex(index);
            }
            previousIndex = index;
        }

        return firstBlockIndex;
    }

 
    private void freeBlocksOfFile(AsignacionArchivo entry) {
        int index = entry.getFirstBlockIndex();
        while (index != -1) {
            Bloque b = disk.getBlock(index);
            int next = b.getNextBlockIndex();
            b.liberar();
            index = next;
        }
    }


    public AsignacionArchivo findAllocationEntryByFileId(int fileId) {
        int n = allocationTable.size();
        for (int i = 0; i < n; i++) {
            AsignacionArchivo e = allocationTable.get(i);
            if (e.getFileId() == fileId) {
                return e;
            }
        }
        return null;
    }

    private void removeAllocationEntry(int fileId) {
        int n = allocationTable.size();
        for (int i = 0; i < n; i++) {
            AsignacionArchivo e = allocationTable.get(i);
            if (e.getFileId() == fileId) {
                allocationTable.removeAt(i);
                return;
            }
        }
    }


    private String generateColorHex(int fileId) {
        float hue = (float) ((fileId * 0.61803398875) % 1.0); // dorado
        Color c = Color.getHSBColor(hue, 0.5f, 1.0f);
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }
    
    public int getNextFileId() {
        return nextFileId;
    }

    public void setNextFileId(int nextFileId) {
        this.nextFileId = nextFileId;
    }
}
