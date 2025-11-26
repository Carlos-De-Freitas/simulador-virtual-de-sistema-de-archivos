/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import sistemadearchivos.Bloque;
import sistemadearchivos.AsignacionArchivo;
import sistemadearchivos.SistemaArchivo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 *
 * @author 58412
 */
public class PanelDisco extends JPanel {

    private final SistemaArchivo fileSystem;

    private final int cellSize = 24;
    private final int marginLeft = 10;
    private final int marginTop = 20;

    public PanelDisco(SistemaArchivo fileSystem) {
        this.fileSystem = fileSystem;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Disco"));

        int numBlocks = fileSystem.getDisk().getNumBlocks();
        int initialCols = 10;
        int initialRows = (int) Math.ceil(numBlocks / (double) initialCols);
        int width = initialCols * cellSize + marginLeft * 2;
        int height = initialRows * cellSize + marginTop * 2 + 20;
        setPreferredSize(new Dimension(width, height));

        ToolTipManager.sharedInstance().registerComponent(this);
        setToolTipText("");
    }

    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int numBlocks = fileSystem.getDisk().getNumBlocks();

        int panelWidth = getWidth();
        int usableWidth = Math.max(panelWidth - marginLeft * 2, cellSize);
        int cols = Math.max(1, usableWidth / cellSize);
        int rows = (int) Math.ceil(numBlocks / (double) cols);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < numBlocks; i++) {
            int row = i / cols;
            int col = i % cols;

            int x = marginLeft + col * cellSize;
            int y = marginTop + row * cellSize;

            Bloque b = fileSystem.getDisk().getBlock(i);

            Color fill;
            if (!b.isOcupado()) {
                fill = new Color(230, 230, 230);
            } else {
                fill = getColorForBlock(b);
            }

            g2.setColor(fill);
            g2.fillRect(x, y, cellSize - 2, cellSize - 2);

            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, y, cellSize - 2, cellSize - 2);

            g2.setFont(g2.getFont().deriveFont(9f));
            g2.drawString(String.valueOf(i), x + 2, y + cellSize - 6);
        }
    }

    private Color getColorForBlock(Bloque b) {
        int fileId = b.getFileId();
        if (fileId <= 0) {
            return new Color(200, 200, 200);
        }
        AsignacionArchivo entry = fileSystem.findAllocationEntryByFileId(fileId);
        if (entry == null) {
            return new Color(180, 180, 180);
        }
        try {
            return Color.decode(entry.getColorHex());
        } catch (Exception e) {
            return new Color(180, 180, 180);
        }
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int numBlocks = fileSystem.getDisk().getNumBlocks();

        int panelWidth = getWidth();
        int usableWidth = Math.max(panelWidth - marginLeft * 2, cellSize);
        int cols = Math.max(1, usableWidth / cellSize);

        int col = (event.getX() - marginLeft) / cellSize;
        int row = (event.getY() - marginTop) / cellSize;

        if (col < 0 || row < 0) {
            return null;
        }

        int index = row * cols + col;
        if (index < 0 || index >= numBlocks) {
            return null;
        }

        Bloque b = fileSystem.getDisk().getBlock(index);

        if (!b.isOcupado()) {
            return "Bloque " + index + " - Libre";
        }

        int fileId = b.getFileId();
        AsignacionArchivo entry = fileSystem.findAllocationEntryByFileId(fileId);

        if (entry == null) {
            return "Bloque " + index + " - Ocupado (fileId=" + fileId + ")";
        }

        return "Bloque " + index +
                " - Archivo: " + entry.getFileName() +
                " (FileID=" + entry.getFileId() +
                ", PID creador=" + entry.getCreatorPid() + ")";
    }
}
