package logica;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import igu.Principal;
import java.util.Map;
import javax.swing.SwingUtilities;

public class App {

    private static final String VERDE = "#2E7D32";

    public static void main(String[] args) {
        FlatLaf.setGlobalExtraDefaults(Map.of(
                "@accentColor", VERDE,
                "Button.default.background", VERDE,
                "Button.default.focusedBackground", VERDE,
                "Button.default.hoverBackground", "#29702D",
                "Button.default.pressedBackground", "#246227",
                "Button.default.foreground", "#FFFFFF",
                "Button.default.boldText", "true"));
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            Principal ventana = new Principal();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
