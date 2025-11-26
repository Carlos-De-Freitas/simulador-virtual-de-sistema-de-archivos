/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import sistemadearchivos.NodoDirectorio;
import sistemadearchivos.SistemaArchivo;
import sistemadearchivos.NodoFs;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import modos.User;
/**
 *
 * @author 58412
 */
public class TreePanelFs extends JPanel {

    private final SistemaArchivo fileSystem;
    private User currentUser;

    private final JTree tree;
    private DefaultTreeModel treeModel;

    public TreePanelFs(SistemaArchivo fileSystem) {
        super(new BorderLayout());
        this.fileSystem = fileSystem;
        this.currentUser = null;

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(fileSystem.getRoot());
        this.treeModel = new DefaultTreeModel(rootNode);
        this.tree = new JTree(treeModel);

        JScrollPane scroll = new JScrollPane(tree);
        add(scroll, BorderLayout.CENTER);

        setBorder(BorderFactory.createTitledBorder("Sistema de archivos"));

        refreshTree();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        refreshTree();
    }

    public void addSelectionListener(TreeSelectionListener listener) {
        tree.addTreeSelectionListener(listener);
    }

    public NodoFs getSelectedNode() {
        TreePath path = tree.getSelectionPath();
        if (path == null) {
            return null;
        }
        Object last = path.getLastPathComponent();
        if (last instanceof DefaultMutableTreeNode) {
            Object obj = ((DefaultMutableTreeNode) last).getUserObject();
            if (obj instanceof NodoFs) {
                return (NodoFs) obj;
            }
        }
        return null;
    }

    public void refreshTree() {
        NodoFs rootNode = fileSystem.getRoot();
        DefaultMutableTreeNode visualRoot = buildTreeNode(rootNode);
        if (visualRoot == null) {
            visualRoot = new DefaultMutableTreeNode(rootNode);
        }
        treeModel.setRoot(visualRoot);
        treeModel.reload();

        tree.expandRow(0);
    }

    private DefaultMutableTreeNode buildTreeNode(NodoFs node) {
        if (!isNodeVisible(node)) {
            return null;
        }
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);

        if (node.esDirectorio()) {
            NodoDirectorio dir = (NodoDirectorio) node;
            int count = dir.getChildrenCount();
            for (int i = 0; i < count; i++) {
                NodoFs child = dir.getChildAt(i);
                DefaultMutableTreeNode childVisual = buildTreeNode(child);
                if (childVisual != null) {
                    treeNode.add(childVisual);
                }
            }
        }
        return treeNode;
    }

    private boolean isNodeVisible(NodoFs node) {
        if (node == fileSystem.getRoot()) {
            return true;
        }

        if (currentUser == null) {
            return true;
        }
        if (currentUser.isAdmin()) {
            return true;
        }

        if (node.isPublico()) {
            return true;
        }
        return node.getPropietario() != null && node.getPropietario().equals(currentUser);
    }
}