package igu;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import logica.Sorteo;

public class Principal extends JFrame {

    private static final int MARGEN = 24;

    private final JComboBox<String> cmbMes = new JComboBox<>(nombresDeMeses());
    private final JTextField txtCantGanadores = new JTextField();
    private final JButton btnSortear = new JButton("Sortear", icono("bolillero.png", 32));
    private final JButton btnCerrarSorteo = new JButton("Cerrar sorteo", icono("limpiar.png", 24));
    private final JLabel lblEstado = new JLabel(" ");
    private final DefaultTableModel modeloGanadores = new DefaultTableModel(new Object[]{"Posición", "Número"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    private Sorteo sorteo;

    public Principal() {
        super("Sorteador Supermercado");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(icono("carrito.png", 64).getImage());

        JPanel contenido = new JPanel(new BorderLayout(MARGEN, MARGEN));
        contenido.setBorder(BorderFactory.createEmptyBorder(MARGEN, MARGEN, MARGEN, MARGEN));
        contenido.add(crearEncabezado(), BorderLayout.NORTH);
        contenido.add(crearPanelDatos(), BorderLayout.WEST);
        contenido.add(crearPanelGanadores(), BorderLayout.CENTER);
        setContentPane(contenido);

        btnSortear.addActionListener(e -> sortear());
        btnCerrarSorteo.addActionListener(e -> cerrarSorteo());
        // Enter sortea desde cualquier campo, y FlatLaf lo resalta como botón principal.
        getRootPane().setDefaultButton(btnSortear);

        pack();
        setMinimumSize(getSize());
    }

    private void sortear() {
        if (sorteo == null) {
            sorteo = crearSorteo();
            if (sorteo == null) {
                return;
            }
            // Mes y cantidad quedan fijos hasta cerrar el sorteo, para que todos
            // los ganadores salgan del mismo rango.
            habilitarDatosDelSorteo(false);
        }

        if (sorteo.estaCompleto()) {
            JOptionPane.showMessageDialog(this, "Ya se alcanzó la cantidad de ganadores.");
            return;
        }

        String ganador = sorteo.sortearGanador();
        modeloGanadores.addRow(new Object[]{sorteo.getCantidadSorteados(), ganador});
        actualizarEstado();
    }

    private void cerrarSorteo() {
        if (JOptionPane.showConfirmDialog(this,
                "¿Desea realmente finalizar el sorteo?",
                "Finalización Sorteo",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            sorteo = null;
            txtCantGanadores.setText("");
            cmbMes.setSelectedIndex(0);
            modeloGanadores.setRowCount(0);
            habilitarDatosDelSorteo(true);
            actualizarEstado();
        }
    }

    private Sorteo crearSorteo() {
        String cantidad = txtCantGanadores.getText().trim();
        if (cantidad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Es necesario completar la cantidad de ganadores.");
            return null;
        }

        int mes = cmbMes.getSelectedIndex() + 1;
        // El año en curso define si febrero tiene 28 o 29 días.
        YearMonth mesSorteado = YearMonth.of(Year.now().getValue(), mes);

        try {
            return new Sorteo(mesSorteado, Integer.parseInt(cantidad));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad de ganadores tiene que ser un número entero.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
        return null;
    }

    private void habilitarDatosDelSorteo(boolean habilitar) {
        cmbMes.setEnabled(habilitar);
        txtCantGanadores.setEnabled(habilitar);
    }

    private void actualizarEstado() {
        if (sorteo == null) {
            lblEstado.setText(" ");
        } else if (sorteo.estaCompleto()) {
            lblEstado.setText("Sorteo completo: " + sorteo.getCantidadGanadores() + " ganadores");
        } else {
            lblEstado.setText(sorteo.getCantidadSorteados() + " de " + sorteo.getCantidadGanadores() + " ganadores");
        }
    }

    private JPanel crearEncabezado() {
        JLabel titulo = new JLabel("Sorteador Supermercado");
        titulo.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");
        JLabel subtitulo = new JLabel("Sorteo mensual entre los códigos de participante de cada sobre");
        subtitulo.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        textos.add(titulo);
        textos.add(Box.createVerticalStrut(4));
        textos.add(subtitulo);
        textos.add(Box.createVerticalGlue());

        JPanel encabezado = new JPanel(new BorderLayout(16, 0));
        encabezado.add(new JLabel(icono("carrito.png", 72)), BorderLayout.WEST);
        encabezado.add(textos, BorderLayout.CENTER);
        return encabezado;
    }

    private JPanel crearPanelDatos() {
        txtCantGanadores.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Por ejemplo, 5");
        btnSortear.setIconTextGap(10);
        btnSortear.putClientProperty(FlatClientProperties.STYLE, "font: +3; margin: 6,16,6,16");
        lblEstado.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground");

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);
        agregarFila(formulario, new JLabel("Mes del sorteo"), 6);
        agregarFila(formulario, cmbMes, 16);
        agregarFila(formulario, new JLabel("Cantidad de ganadores"), 6);
        agregarFila(formulario, txtCantGanadores, 24);
        agregarFila(formulario, btnSortear, 8);
        agregarFila(formulario, btnCerrarSorteo, 16);
        agregarFila(formulario, lblEstado, 0);

        GridBagConstraints relleno = new GridBagConstraints();
        relleno.gridx = 0;
        relleno.weighty = 1;
        formulario.add(Box.createGlue(), relleno);

        JPanel tarjeta = crearTarjeta("Datos del sorteo", formulario);
        tarjeta.setPreferredSize(new Dimension(280, tarjeta.getPreferredSize().height));
        return tarjeta;
    }

    private JPanel crearPanelGanadores() {
        JTable tabla = new JTable(modeloGanadores);
        tabla.setRowHeight(30);
        tabla.setFillsViewportHeight(true);
        tabla.setShowHorizontalLines(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setPreferredScrollableViewportSize(new Dimension(360, 300));

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }
        tabla.getColumnModel().getColumn(0).setMaxWidth(100);

        return crearTarjeta("Ganadores", new JScrollPane(tabla));
    }

    private JPanel crearTarjeta(String titulo, JComponent cuerpo) {
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.putClientProperty(FlatClientProperties.STYLE_CLASS, "h3");

        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: $Table.background");
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(cuerpo, BorderLayout.CENTER);
        return tarjeta;
    }

    private static void agregarFila(JPanel panel, JComponent componente, int espacioDebajo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.insets = new Insets(0, 0, espacioDebajo, 0);
        panel.add(componente, c);
    }

    private static String[] nombresDeMeses() {
        Locale espanol = Locale.forLanguageTag("es-AR");
        String[] meses = new String[12];
        for (int i = 0; i < 12; i++) {
            String nombre = Month.of(i + 1).getDisplayName(TextStyle.FULL_STANDALONE, espanol);
            meses[i] = String.format("%02d - %s", i + 1,
                    nombre.substring(0, 1).toUpperCase(espanol) + nombre.substring(1));
        }
        return meses;
    }

    private static ImageIcon icono(String archivo, int tamanio) {
        Image imagen = new ImageIcon(Principal.class.getResource("/imagenes/" + archivo)).getImage();
        return new ImageIcon(imagen.getScaledInstance(tamanio, tamanio, Image.SCALE_SMOOTH));
    }
}
