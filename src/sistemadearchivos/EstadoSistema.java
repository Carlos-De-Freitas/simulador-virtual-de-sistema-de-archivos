/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemadearchivos;

import java.io.*;
import modos.User;
/**
 *
 * @author 58412
 */
public class EstadoSistema {

    public static void saveToFile(SistemaArchivo fs, File file) throws IOException {
        StringBuilder sb = new StringBuilder();

        int numBlocks = fs.getDisk().getNumBlocks();
        int nextFileId = fs.getNextFileId();

        sb.append("DISK ").append(numBlocks).append(" ").append(nextFileId).append("\n");

        for (int i = 0; i < numBlocks; i++) {
            Bloque b = fs.getDisk().getBlock(i);
            int ocupado = b.isOcupado() ? 1 : 0;
            int fileId = b.getFileId();
            int next = b.getNextBlockIndex();
            sb.append("BLOCK ")
              .append(i).append(" ")
              .append(ocupado).append(" ")
              .append(fileId).append(" ")
              .append(next).append("\n");
        }

        sb.append("NODES\n");
        writeNodeRecursive(fs.getRoot(), "/", sb);

        sb.append("FAT\n");
        int fatSize = fs.getAllocationTable().size();
        for (int i = 0; i < fatSize; i++) {
            AsignacionArchivo e = fs.getAllocationTable().get(i);

            sb.append("FATENTRY ")
              .append(e.getFileId()).append(" ")
              .append(sanitize(e.getFileName())).append(" ")
              .append(e.getFirstBlockIndex()).append(" ")
              .append(e.getNumBlocks()).append(" ")
              .append(e.getCreatorPid()).append(" ")
              .append(sanitize(e.getColorHex()))
              .append("\n");
        }

        writeAll(file, sb.toString());
    }

    public static SistemaArchivo loadFromFile(File file, User adminUser, User normalUser) throws IOException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8")
        );

        String line = br.readLine();
        if (line == null || !line.startsWith("DISK ")) {
            br.close();
            throw new IOException("Formato inválido: falta cabecera DISK.");
        }

        String[] parts = line.trim().split(" +");
        if (parts.length < 2) {
            br.close();
            throw new IOException("Formato inválido en línea DISK.");
        }

        int numBlocks = Integer.parseInt(parts[1]);
        int nextFileId = 1;
        if (parts.length >= 3) {
            nextFileId = Integer.parseInt(parts[2]);
        }

        SistemaArchivo fs = new SistemaArchivo(numBlocks, adminUser);
        fs.setNextFileId(nextFileId);

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.equals("NODES")) {
                break;
            }
            if (!line.startsWith("BLOCK ")) {
                continue;
            }

            String[] p = line.split(" +");
            if (p.length < 5) {
                continue;
            }
            int index = Integer.parseInt(p[1]);
            int ocupado = Integer.parseInt(p[2]);
            int fileId = Integer.parseInt(p[3]);
            int next = Integer.parseInt(p[4]);

            if (index < 0 || index >= numBlocks) {
                continue;
            }
            Bloque b = fs.getDisk().getBlock(index);
            if (ocupado == 1) {
                b.setOcupado(true);
                b.setFileId(fileId);
                b.setNextBlockIndex(next);
            } else {
                b.liberar();
            }
        }

        int maxFileId = 0;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.equals("FAT")) {
                break;
            }
            if (!line.startsWith("NODE ")) {
                continue;
            }

            String[] p = line.split(" +");
            if (p.length < 5) {
                continue;
            }

            String path = p[1];
            String type = p[2];
            String ownerName = p[3];
            boolean publico = "1".equals(p[4]) || "true".equalsIgnoreCase(p[4]);

            User owner = adminUser;
            if (ownerName.equals(normalUser.getUsername())) {
                owner = normalUser;
            }

            if ("/".equals(path)) {
                fs.getRoot().setPublico(publico);
                continue;
            }

            String parentPath = getParentPath(path);
            String name = getNameFromPath(path);

            NodoDirectorio parentDir = ensureDirectoryPath(fs, parentPath, adminUser);
            NodoFs existing = parentDir.findChildByName(name);

            if ("DIR".equalsIgnoreCase(type)) {
                if (existing == null) {
                    NodoDirectorio dir = new NodoDirectorio(name, parentDir, owner, publico);
                    parentDir.addChild(dir);
                } else if (existing.esDirectorio()) {
                    existing.setPublico(publico);
                }
            } else if ("FILE".equalsIgnoreCase(type)) {
                if (p.length < 7) {
                    continue;
                }
                int fileId = Integer.parseInt(p[5]);
                int sizeBlocks = Integer.parseInt(p[6]);
                if (existing == null) {
                    NodoArchivo fn = new NodoArchivo(name, parentDir, owner, publico, fileId, sizeBlocks);
                    parentDir.addChild(fn);
                }
                if (fileId > maxFileId) {
                    maxFileId = fileId;
                }
            }
        }

        if (line != null && line.equals("FAT")) {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (!line.startsWith("FATENTRY ")) {
                    continue;
                }
                String[] p = line.split(" +");
                if (p.length < 7) {
                    continue;
                }

                int fileId = Integer.parseInt(p[1]);
                String fileName = restore(p[2]);
                int firstBlockIndex = Integer.parseInt(p[3]);
                int numBlocksEntry = Integer.parseInt(p[4]);
                int creatorPid = Integer.parseInt(p[5]);
                String colorHex = restore(p[6]);

                AsignacionArchivo entry =
                        new AsignacionArchivo(fileId, fileName, firstBlockIndex, numBlocksEntry, creatorPid);
                entry.setColorHex(colorHex);
                fs.getAllocationTable().addLast(entry);

                if (fileId > maxFileId) {
                    maxFileId = fileId;
                }
            }
        }

        br.close();

        int currentNext = fs.getNextFileId();
        if (currentNext <= maxFileId) {
            fs.setNextFileId(maxFileId + 1);
        }

        return fs;
    }

    private static void writeNodeRecursive(NodoFs node, String path, StringBuilder sb) {
        String ownerName = "-";
        if (node.getPropietario() != null) {
            ownerName = node.getPropietario().getUsername();
        }
        int publico = node.isPublico() ? 1 : 0;

        if (node.esDirectorio()) {
            sb.append("NODE ")
              .append(path).append(" ")
              .append("DIR").append(" ")
              .append(ownerName).append(" ")
              .append(publico)
              .append("\n");
        } else {
            NodoArchivo fn = (NodoArchivo) node;
            sb.append("NODE ")
              .append(path).append(" ")
              .append("FILE").append(" ")
              .append(ownerName).append(" ")
              .append(publico).append(" ")
              .append(fn.getFileId()).append(" ")
              .append(fn.getSizeBlocks())
              .append("\n");
        }

        if (node.esDirectorio()) {
            NodoDirectorio dir = (NodoDirectorio) node;
            int count = dir.getChildrenCount();
            for (int i = 0; i < count; i++) {
                NodoFs child = dir.getChildAt(i);
                String childPath;
                if ("/".equals(path)) {
                    childPath = "/" + child.getNombre();
                } else {
                    childPath = path + "/" + child.getNombre();
                }
                writeNodeRecursive(child, childPath, sb);
            }
        }
    }

    private static String getParentPath(String path) {
        if (path == null || "/".equals(path)) {
            return "/";
        }
        int last = path.lastIndexOf('/');
        if (last <= 0) {
            return "/";
        }
        if (last == 0) {
            return "/";
        }
        return path.substring(0, last);
    }

    private static String getNameFromPath(String path) {
        if (path == null || "/".equals(path)) {
            return "/";
        }
        int last = path.lastIndexOf('/');
        if (last < 0) {
            return path;
        }
        return path.substring(last + 1);
    }


    private static NodoDirectorio ensureDirectoryPath(SistemaArchivo fs, String dirPath, User defaultOwner) {
        NodoDirectorio current = fs.getRoot();
        if (dirPath == null || dirPath.length() == 0 || "/".equals(dirPath)) {
            return current;
        }

        int i = (dirPath.charAt(0) == '/') ? 1 : 0;
        int len = dirPath.length();
        while (i < len) {
            int slash = dirPath.indexOf('/', i);
            String segment;
            if (slash == -1) {
                segment = dirPath.substring(i);
                i = len;
            } else {
                segment = dirPath.substring(i, slash);
                i = slash + 1;
            }
            if (segment.length() == 0) {
                continue;
            }

            NodoFs child = current.findChildByName(segment);
            if (child != null && child.esDirectorio()) {
                current = (NodoDirectorio) child;
            } else if (child == null) {
                NodoDirectorio newDir = new NodoDirectorio(segment, current, defaultOwner, true);
                current.addChild(newDir);
                current = newDir;
            } else {
                break;
            }
        }
        return current;
    }

    private static void writeAll(File file, String text) throws IOException {
        OutputStream out = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(out, "UTF-8");
        writer.write(text);
        writer.flush();
        writer.close();
    }

    private static String sanitize(String s) {
        if (s == null) return "-";
        return s.replace(' ', '_');
    }

    private static String restore(String s) {
        if (s == null) return "";
        return s.replace('_', ' ');
    }
}
