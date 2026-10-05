package logica;

import igu.Principal;
import javax.swing.SwingUtilities;

public class App {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Principal ventana = new Principal();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
