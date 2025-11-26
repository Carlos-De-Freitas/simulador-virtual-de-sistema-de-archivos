/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package simuladordesistemadearchivos;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import modos.*;
import proceso.AdministradorProcesos;
import sistemadearchivos.SistemaArchivo;
import vista.VentanaPrincipal;

/**
 *
 * @author Usuario
 */
public class SimuladorDeSistemaDeArchivos {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            User admin = new User("admin", UserMode.ADMIN);
            User normal = new User("user", UserMode.USER);

            int numBlocks = askDiskSize();

            SistemaArchivo fs = new SistemaArchivo(numBlocks, admin);

            AdministradorProcesos pm = new AdministradorProcesos(fs);

            VentanaPrincipal win = new VentanaPrincipal(fs, admin, normal, pm);
            win.setVisible(true);
        });
    }

    private static int askDiskSize() {
        final int defaultSize = 100;

        while (true) {
            String input = JOptionPane.showInputDialog(
                    null,
                    "Ingrese el tamaño del disco (número de bloques):",
                    "Configuración de disco",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (input == null) {
                return defaultSize;
            }

            input = input.trim();
            if (input.isEmpty()) {
                return defaultSize;
            }

            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "El tamaño debe ser un entero positivo.",
                            "Valor inválido",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "El tamaño debe ser un número entero.",
                        "Valor inválido",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}
