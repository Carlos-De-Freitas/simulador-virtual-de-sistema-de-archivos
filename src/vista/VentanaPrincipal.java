/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import sistemadearchivos.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import proceso.AdministradorProcesos;
import modos.User;

/**
 *
 * @author 58412
 */

public class VentanaPrincipal extends JFrame {

    private final SistemaArchivo fileSystem;
    private final User adminUser;
    private final User normalUser;
    User currentUser;
    private final AdministradorProcesos processManager;

    private TreePanelFs fileSystemTreePanel;
    private PanelDisco diskPanel;
    private PanelProcesos processPanel;
    private PanelBuffer bufferPanel;
    private PanelDetallesNodo nodeDetailsPanel;

    private JMenuItem itemNuevoDir;
    private JMenuItem itemNuevoArchivo;
    private JMenuItem itemEliminar;
    private JMenuItem itemRenombrar;
    
    private JMenuItem itemGuardarEstado;
    private JMenuItem itemCargarEstado;

    public VentanaPrincipal(SistemaArchivo fileSystem, User adminUser, User normalUser, AdministradorProcesos processManager) {
        super("Simulador de Sistema de Archivos");
        this.fileSystem = fileSystem;
        this.adminUser = adminUser;
        this.normalUser = normalUser;
        this.currentUser = adminUser;
        this.processManager = processManager;
        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        fileSystemTreePanel = new TreePanelFs(fileSystem);
        fileSystemTreePanel.setCurrentUser(currentUser);

        diskPanel = new PanelDisco(fileSystem);
        processPanel = new PanelProcesos(processManager, currentUser, this);
        bufferPanel = new PanelBuffer(fileSystem);
        nodeDetailsPanel = new PanelDetallesNodo();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(nodeDetailsPanel, BorderLayout.NORTH);
        leftPanel.add(fileSystemTreePanel, BorderLayout.CENTER);

        JSplitPane bottomSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                processPanel,
                bufferPanel
        );
        bottomSplit.setResizeWeight(0.5);

        JSplitPane rightSplit = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(diskPanel),
                bottomSplit
        );
        rightSplit.setResizeWeight(0.4);

        JSplitPane mainSplit = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightSplit
        );
        mainSplit.setResizeWeight(0.30);

        leftPanel.setMinimumSize(new Dimension(260, 200));
        diskPanel.setMinimumSize(new Dimension(200, 200));
        processPanel.setMinimumSize(new Dimension(200, 150));
        bufferPanel.setMinimumSize(new Dimension(200, 150));

        setContentPane(mainSplit);

        setJMenuBar(createMenuBar());

        fileSystemTreePanel.addSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                NodoFs node = fileSystemTreePanel.getSelectedNode();
                nodeDetailsPanel.updateDetails(node, currentUser);
            }
        });

        nodeDetailsPanel.updateDetails(fileSystem.getRoot(), currentUser);

        updateUserModeUI();
    }

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu menuArchivo = new JMenu("Archivo");

        itemNuevoDir = new JMenuItem("Crear directorio...");
        itemNuevoDir.addActionListener(e -> onCreateDirectory());

        itemNuevoArchivo = new JMenuItem("Crear archivo...");
        itemNuevoArchivo.addActionListener(e -> onCreateFile());

        itemRenombrar = new JMenuItem("Renombrar...");
        itemRenombrar.addActionListener(e -> onRenameNode());

        itemEliminar = new JMenuItem("Eliminar...");
        itemEliminar.addActionListener(e -> onDeleteNode());

        itemGuardarEstado = new JMenuItem("Guardar estado...");
        itemGuardarEstado.addActionListener(e -> onSaveState());

        itemCargarEstado = new JMenuItem("Cargar estado...");
        itemCargarEstado.addActionListener(e -> onLoadState());

        menuArchivo.add(itemNuevoDir);
        menuArchivo.add(itemNuevoArchivo);
        menuArchivo.add(itemRenombrar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemEliminar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemGuardarEstado);
        menuArchivo.add(itemCargarEstado);

        bar.add(menuArchivo);

        JMenu menuUsuario = new JMenu("Usuario");
        ButtonGroup group = new ButtonGroup();

        JRadioButtonMenuItem adminItem = new JRadioButtonMenuItem("Administrador", true);
        JRadioButtonMenuItem userItem = new JRadioButtonMenuItem("Usuario", false);

        adminItem.addActionListener(e -> {
            currentUser = adminUser;
            processPanel.setCurrentUser(currentUser);
            fileSystemTreePanel.setCurrentUser(currentUser);
            updateUserModeUI();
            refreshFileSystemView();
        });

        userItem.addActionListener(e -> {
            currentUser = normalUser;
            processPanel.setCurrentUser(currentUser);
            fileSystemTreePanel.setCurrentUser(currentUser);
            updateUserModeUI();
            refreshFileSystemView();
        });

        group.add(adminItem);
        group.add(userItem);

        menuUsuario.add(adminItem);
        menuUsuario.add(userItem);

        bar.add(menuUsuario);

        return bar;
    }

    private void updateUserModeUI() {
        boolean isAdmin = currentUser.isAdmin();

        itemNuevoDir.setEnabled(isAdmin);
        itemNuevoArchivo.setEnabled(isAdmin);
        itemEliminar.setEnabled(isAdmin);
        itemRenombrar.setEnabled(isAdmin);
        itemGuardarEstado.setEnabled(isAdmin);
        itemCargarEstado.setEnabled(isAdmin);

        String modo = isAdmin ? "Administrador" : "Usuario";
        setTitle("Simulador de Sistema de Archivos - Modo: " + modo + " (" + currentUser.getUsername() + ")");
    }

    private void onCreateDirectory() {
        NodoFs selected = fileSystemTreePanel.getSelectedNode();
        NodoDirectorio parent;

        if (selected == null) {
            parent = fileSystem.getRoot();
        } else if (selected.esDirectorio()) {
            parent = (NodoDirectorio) selected;
        } else {
            parent = ((NodoArchivo) selected).getPadre();
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Nombre del directorio:",
                "Crear directorio",
                JOptionPane.QUESTION_MESSAGE
        );
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Directorio público (visible para todos)?",
                "Visibilidad",
                JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (resp == JOptionPane.CANCEL_OPTION || resp == JOptionPane.CLOSED_OPTION) {
            return;
        }
        boolean publico = (resp == JOptionPane.YES_OPTION);

        try {
            fileSystem.createDirectory(parent, name.trim(), currentUser, publico);
            refreshFileSystemView();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onCreateFile() {
        NodoFs selected = fileSystemTreePanel.getSelectedNode();
        NodoDirectorio parent;

        if (selected == null) {
            parent = fileSystem.getRoot();
        } else if (selected.esDirectorio()) {
            parent = (NodoDirectorio) selected;
        } else {
            parent = ((NodoArchivo) selected).getPadre();
        }

        String name = JOptionPane.showInputDialog(
                this,
                "Nombre del archivo:",
                "Crear archivo",
                JOptionPane.QUESTION_MESSAGE
        );
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        String sizeStr = JOptionPane.showInputDialog(
                this,
                "Tamaño del archivo (en bloques):",
                "Tamaño",
                JOptionPane.QUESTION_MESSAGE
        );
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            return;
        }

        int sizeBlocks;
        try {
            sizeBlocks = Integer.parseInt(sizeStr.trim());
        } catch (NumberFormatException ex) {
            showError("El tamaño debe ser un número entero.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Archivo público (solo lectura para otros)?",
                "Visibilidad",
                JOptionPane.YES_NO_CANCEL_OPTION
        );
        if (resp == JOptionPane.CANCEL_OPTION || resp == JOptionPane.CLOSED_OPTION) {
            return;
        }
        boolean publico = (resp == JOptionPane.YES_OPTION);

        try {
            processManager.scheduleFileCreate(parent, name.trim(), sizeBlocks, publico, currentUser);

            processPanel.refresh();

            JOptionPane.showMessageDialog(this,
                    "Se ha creado un proceso para crear el archivo.\n" +
                            "La creación se completará cuando atiendas la petición de E/S\n" +
                            "desde el panel de Procesos (botón 'Atender siguiente').",
                    "Operación programada",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onRenameNode() {
        NodoFs selected = fileSystemTreePanel.getSelectedNode();
        if (selected == null || selected == fileSystem.getRoot()) {
            showError("Selecciona un archivo o directorio (distinto a la raíz) para renombrar.");
            return;
        }

        String newName = JOptionPane.showInputDialog(
                this,
                "Nuevo nombre:",
                "Renombrar",
                JOptionPane.QUESTION_MESSAGE
        );
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        try {
            fileSystem.renameNode(selected, newName.trim());
            refreshFileSystemView();

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void onDeleteNode() {
        NodoFs selected = fileSystemTreePanel.getSelectedNode();
        if (selected == null || selected == fileSystem.getRoot()) {
            showError("Selecciona un archivo o directorio (distinto a la raíz) para eliminar.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar \"" + selected.getNombre() + "\"?\n" +
                        "Se creará un proceso y la eliminación se realizará\n" +
                        "cuando se atienda la petición de E/S correspondiente.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );
        if (resp != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            processManager.scheduleNodeDelete(selected, currentUser);
            processPanel.refresh();

            JOptionPane.showMessageDialog(this,
                    "Se ha creado un proceso para eliminar el nodo.\n" +
                            "La eliminación se completará cuando atiendas la petición de E/S.",
                    "Operación programada",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }


    public void refreshFileSystemView() {
        fileSystemTreePanel.refreshTree();
        diskPanel.refresh();
        bufferPanel.refresh();
        processPanel.refresh();

        NodoFs node = fileSystemTreePanel.getSelectedNode();
        if (node == null) {
            node = fileSystem.getRoot();
        }
        nodeDetailsPanel.updateDetails(node, currentUser);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(
                this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
    private void onSaveState() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar estado del sistema");
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                sistemadearchivos.EstadoSistema.saveToFile(fileSystem, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                        "Estado guardado correctamente.",
                        "Guardar estado",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                showError("Error al guardar estado: " + ex.getMessage());
            }
        }
    }

    private void onLoadState() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Cargar estado del sistema");
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                sistemadearchivos.SistemaArchivo newFs =
                        sistemadearchivos.EstadoSistema.loadFromFile(chooser.getSelectedFile(), adminUser, normalUser);

                proceso.AdministradorProcesos newPm = new proceso.AdministradorProcesos(newFs);
                VentanaPrincipal newWin = new VentanaPrincipal(newFs, adminUser, normalUser, newPm);
                newWin.setVisible(true);
                this.dispose();

            } catch (Exception ex) {
                showError("Error al cargar estado: " + ex.getMessage());
            }
        }
    }
}