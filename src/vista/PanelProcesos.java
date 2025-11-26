/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.AbstractTableModel;
import proceso.*;
import modos.User;
import estructuras.ListaSimple;

/**
 *
 * @author 58412
 */
public class PanelProcesos extends JPanel {

    private final AdministradorProcesos processManager;
    private final VentanaPrincipal mainWindow;

    private final JTable processTable;
    private final JTable requestTable;
    private final ProcessTableModel processModel;
    private final RequestTableModel requestModel;

    private final JComboBox<DiskSchedulerType> comboAlgoritmo;
    private final JLabel lblAlgoritmo;
    private final JLabel lblHead;
    private final JLabel lblMovimientos;

    private User currentUser;

    public PanelProcesos(AdministradorProcesos processManager, User initialUser, VentanaPrincipal mainWindow) {
        super(new BorderLayout());
        this.processManager = processManager;
        this.currentUser = initialUser;
        this.mainWindow = mainWindow;

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);

        JButton btnNuevoProceso = new JButton("Nuevo proceso");
        btnNuevoProceso.addActionListener(e -> onNuevoProceso());

        JButton btnNuevaPeticion = new JButton("Nueva petición E/S");
        btnNuevaPeticion.addActionListener(e -> onNuevaPeticion());

        JButton btnAtender = new JButton("Atender siguiente");
        btnAtender.addActionListener(e -> onAtender());

        toolbar.add(btnNuevoProceso);
        toolbar.add(btnNuevaPeticion);
        toolbar.add(btnAtender);
        toolbar.addSeparator();

        toolbar.add(new JLabel("Algoritmo: "));
        comboAlgoritmo = new JComboBox<>(DiskSchedulerType.values());
        comboAlgoritmo.setSelectedItem(processManager.getSchedulerType());
        comboAlgoritmo.addActionListener(e -> {
            DiskSchedulerType type =
                    (DiskSchedulerType) comboAlgoritmo.getSelectedItem();
            processManager.setSchedulerType(type);
            refresh();
        });
        toolbar.add(comboAlgoritmo);

        add(toolbar, BorderLayout.NORTH);

        processModel = new ProcessTableModel();
        requestModel = new RequestTableModel();

        processTable = new JTable(processModel);
        requestTable = new JTable(requestModel);

        JScrollPane scrollProc = new JScrollPane(processTable);
        JScrollPane scrollReq = new JScrollPane(requestTable);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollProc, scrollReq);
        split.setResizeWeight(0.5);

        add(split, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblAlgoritmo = new JLabel();
        lblHead = new JLabel();
        lblMovimientos = new JLabel();

        statusPanel.add(lblAlgoritmo);
        statusPanel.add(Box.createHorizontalStrut(15));
        statusPanel.add(lblHead);
        statusPanel.add(Box.createHorizontalStrut(15));
        statusPanel.add(lblMovimientos);

        add(statusPanel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createTitledBorder("Procesos / E/S"));

        updateStatusLabels();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    private void onNuevoProceso() {
        String nombre = JOptionPane.showInputDialog(
                this,
                "Nombre del proceso:",
                "Nuevo proceso",
                JOptionPane.QUESTION_MESSAGE
        );
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "No hay usuario activo.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        processManager.createProcess(nombre.trim(), currentUser);
        refresh();
    }

    private void onNuevaPeticion() {
        int row = processTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona un proceso en la tabla.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Proceso p = processManager.getProcessAt(row);

        String blockStr = JOptionPane.showInputDialog(
                this,
                "Número de bloque a acceder:",
                "Nueva petición de E/S",
                JOptionPane.QUESTION_MESSAGE
        );
        if (blockStr == null || blockStr.trim().isEmpty()) {
            return;
        }

        int blockIndex;
        try {
            blockIndex = Integer.parseInt(blockStr.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El bloque debe ser un número entero.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        long time = System.currentTimeMillis();
        processManager.addIORequestToProcess(p, blockIndex, time);
        refresh();
    }

    private void onAtender() {
        if (processManager.getScheduler().hasRequests()) {
            IORequest served = processManager.dispatchNextRequest();

            mainWindow.refreshFileSystemView();

            if (served != null) {
                JOptionPane.showMessageDialog(this,
                        "Atendida petición de P" + served.getProceso().getPid() +
                                " al bloque " + served.getBlockIndex() +
                                ". Cabezal ahora en " + processManager.getCurrentHeadPosition(),
                        "Petición atendida",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No hay peticiones de E/S pendientes.",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private class ProcessTableModel extends AbstractTableModel {

        private final String[] columns = {"PID", "Nombre", "Usuario", "Estado", "Peticiones pendientes"};

        @Override
        public int getRowCount() {
            return processManager.getProcessCount();
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
            Proceso p = processManager.getProcessAt(rowIndex);
            switch (columnIndex) {
                case 0: return p.getPid();
                case 1: return p.getNombre();
                case 2: return p.getOwner().getUsername();
                case 3: return p.getEstado();
                case 4: return p.getPendingRequests().size();
                default: return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0 || columnIndex == 4) {
                return Integer.class;
            }
            if (columnIndex == 3) {
                return ProcesoEstado.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    private class RequestTableModel extends AbstractTableModel {

        private final String[] columns = {"PID", "Proceso", "Bloque", "Tiempo llegada"};

        @Override
        public int getRowCount() {
            Scheduler s = processManager.getScheduler();
            return s.getPendingCount();
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
            Scheduler s = processManager.getScheduler();
            ListaSimple<IORequest> list = s.getRequests();
            IORequest req = list.get(rowIndex);

            switch (columnIndex) {
                case 0: return req.getProceso().getPid();
                case 1: return req.getProceso().getNombre();
                case 2: return req.getBlockIndex();
                case 3: return req.getArrivalTime();
                default: return "";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0 || columnIndex == 2) {
                return Integer.class;
            }
            if (columnIndex == 3) {
                return Long.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }

    public void refresh() {
        processModel.fireTableDataChanged();
        requestModel.fireTableDataChanged();
        updateStatusLabels();
    }

    private void updateStatusLabels() {
        lblAlgoritmo.setText("Algoritmo actual: " + processManager.getSchedulerType());
        lblHead.setText("Cabezal en bloque: " + processManager.getCurrentHeadPosition());
        lblMovimientos.setText("Movimientos totales: " + processManager.getTotalHeadMovements());
    }
}
