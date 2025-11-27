package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de la aplicación GUI.
 *
 * <p>Contiene las pestañas principales: gestión de clientes y estadísticas. Se encarga
 * de ensamblar los paneles de la interfaz y aplicar estilos visuales desde {@link Estilos}.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class VentanaPrincipal extends JFrame {

    /** Pestañas principales de la ventana. */
    public JTabbedPane pestañas;
    /** Panel con gestión de clientes. */
    public PanelClientes panelClientes;
    /** Panel con estadísticas. */
    public PanelEstadisticas panelEstadisticas;

    public VentanaPrincipal() {
        setTitle("Sistema de Atención TechClassUC");
        setSize(1100, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        pestañas = new JTabbedPane();
        panelClientes = new PanelClientes();
        panelEstadisticas = new PanelEstadisticas();

        pestañas.addTab("Gestión de Clientes", panelClientes);
        pestañas.addTab("Estadísticas", panelEstadisticas);

        add(pestañas, BorderLayout.CENTER);

        // Fondo uniforme
        getContentPane().setBackground(Estilos.COLOR_FONDO);
    }
}
