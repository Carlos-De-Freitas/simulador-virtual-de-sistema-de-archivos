/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import sistemadearchivos.AsignacionArchivo;
import sistemadearchivos.SistemaArchivo;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import estructuras.ListaSimple;

/**
 *
 * @author 58412
 */
public class PanelBuffer extends JPanel {

    private final SistemaArchivo fileSystem;
    private final JTable table;
    private final AllocationTableModel model;

    public PanelBuffer(SistemaArchivo fileSystem) {
        super(new BorderLayout());
        this.fileSystem = fileSystem;

        this.model = new AllocationTableModel();
        this.table = new JTable(model);

        table.getColumnModel().getColumn(5).setCellRenderer(new ColorCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        setBorder(BorderFactory.createTitledBorder("Tabla de asignación"));
    }

    public void refresh() {
        model.fireTableDataChanged();
    }


    private class AllocationTableModel extends AbstractTableModel {

        private final String[] columns = {
                "File ID",
                "Nombre",
                "Bloque inicial",
                "Bloques",
                "PID creador",
                "Color"
        };

        @Override
        public int getRowCount() {
            ListaSimple<AsignacionArchivo> list = fileSystem.getAllocationTable();
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            ListaSimple<AsignacionArchivo> list = fileSystem.getAllocationTable();
            AsignacionArchivo e = list.get(rowIndex);

            switch (columnIndex) {
                case 0: return e.getFileId();
                case 1: return e.getFileName();
                case 2: return e.getFirstBlockIndex();
                case 3: return e.getNumBlocks();
                case 4: return e.getCreatorPid();
                case 5: return e.getColorHex();
                default: return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4) {
                return Integer.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private static class ColorCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (value instanceof String) {
                String hex = (String) value;
                try {
                    Color color = Color.decode(hex);
                    c.setBackground(color);
                    setText(hex);
                } catch (Exception e) {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                }
            }
            return c;
        }
    }
}