/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import javax.swing.*;
import java.awt.*;
import sistemadearchivos.*;
import modos.User;

/**
 *
 * @author 58412
 */
public class PanelDetallesNodo extends JPanel {

    private final JLabel lblNombreValor;
    private final JLabel lblTipoValor;
    private final JLabel lblPropietarioValor;
    private final JLabel lblVisibilidadValor;
    private final JLabel lblTamanioValor;

    public PanelDetallesNodo() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Detalles del nodo"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 4, 2, 4);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblNombre = new JLabel("Nombre:");
        JLabel lblTipo = new JLabel("Tipo:");
        JLabel lblPropietario = new JLabel("Propietario:");
        JLabel lblVisibilidad = new JLabel("Visibilidad:");
        JLabel lblTamanio = new JLabel("Tamaño:");

        lblNombreValor = new JLabel("-");
        lblTipoValor = new JLabel("-");
        lblPropietarioValor = new JLabel("-");
        lblVisibilidadValor = new JLabel("-");
        lblTamanioValor = new JLabel("-");

        add(lblNombre, gbc);
        gbc.gridx = 1;
        add(lblNombreValor, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(lblTipo, gbc);
        gbc.gridx = 1;
        add(lblTipoValor, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(lblPropietario, gbc);
        gbc.gridx = 1;
        add(lblPropietarioValor, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(lblVisibilidad, gbc);
        gbc.gridx = 1;
        add(lblVisibilidadValor, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(lblTamanio, gbc);
        gbc.gridx = 1;
        add(lblTamanioValor, gbc);
    }

    public void updateDetails(NodoFs node, User currentUser) {
        if (node == null) {
            lblNombreValor.setText("-");
            lblTipoValor.setText("-");
            lblPropietarioValor.setText("-");
            lblVisibilidadValor.setText("-");
            lblTamanioValor.setText("-");
            return;
        }

        lblNombreValor.setText(node.getNombre());

        if (node.esDirectorio()) {
            lblTipoValor.setText("Directorio");
            NodoDirectorio dir = (NodoDirectorio) node;
            lblTamanioValor.setText(dir.getChildrenCount() + " elemento(s)");
        } else {
            lblTipoValor.setText("Archivo");
            NodoArchivo file = (NodoArchivo) node;
            lblTamanioValor.setText(file.getSizeBlocks() + " bloque(s)");
        }

        if (node.getPropietario() != null) {
            lblPropietarioValor.setText(node.getPropietario().getUsername());
        } else {
            lblPropietarioValor.setText("(sin propietario)");
        }

        lblVisibilidadValor.setText(node.isPublico() ? "Público" : "Privado");
    }
}
