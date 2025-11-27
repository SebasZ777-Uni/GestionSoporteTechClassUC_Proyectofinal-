package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Panel que muestra la gestión de clientes: tablas de espera y atendidos y botones de acción.
 *
 * <p>Proporciona la interfaz para agregar, atender, eliminar y deshacer acciones sobre clientes.
 * Es utilizado por {@link VentanaPrincipal} y gestionado por {@link controlador.ControladorAtencion}.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class PanelClientes extends JPanel {

    /** Tabla que muestra los clientes en espera. */
    public JTable tablaEspera;
    /** Tabla que muestra los clientes atendidos (historial). */
    public JTable tablaAtendidos;
    /** Botón para agregar clientes. */
    public JButton btnAgregar, btnAtender, btnEliminar, btnDeshacer, btnMostrarHistorial;

    /**
     * Construye el panel con tablas y botones y aplica estilos.
     *
     * @since 1.0
     */
    public PanelClientes() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Estilos.COLOR_FONDO);

        // --- Títulos
        JLabel lblTitulo = new JLabel("Gestión de Clientes", SwingConstants.CENTER);
        lblTitulo.setFont(Estilos.FUENTE_TITULO);
        lblTitulo.setForeground(Estilos.COLOR_SECUNDARIO);
        add(lblTitulo, BorderLayout.NORTH);

        // --- Tablas
        String[] columnas = {"ID", "Nombre", "Tipo Solicitud", "Prioridad", "Tiempo (min)", "Hora de Ingreso"};
        tablaEspera = Estilos.tablaModelo(columnas);
        tablaAtendidos = Estilos.tablaModelo(columnas);
        Estilos.estilizarTabla(tablaEspera);
        Estilos.estilizarTabla(tablaAtendidos);
        JPanel panelTablas = new JPanel(new GridLayout(1, 2, 10, 10));
        panelTablas.setOpaque(false);

        panelTablas.add(crearPanelTabla("Clientes en Espera", tablaEspera));
        panelTablas.add(crearPanelTabla("Clientes Atendidos", tablaAtendidos));

        add(panelTablas, BorderLayout.CENTER);

        // --- Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Estilos.COLOR_FONDO);

        btnAgregar = Estilos.boton("Agregar Cliente", Estilos.COLOR_PRIMARIO);
        btnAtender = Estilos.boton("Atender Cliente", Estilos.COLOR_EXITO);
        btnEliminar = Estilos.boton("Eliminar por ID", Estilos.COLOR_ERROR);
        btnDeshacer = Estilos.boton("Deshacer", Estilos.COLOR_SECUNDARIO);
        btnMostrarHistorial = Estilos.boton("Mostrar Historial", new Color(155, 89, 182));

        panelBotones.add(btnAgregar);
        panelBotones.add(btnAtender);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnDeshacer);
        panelBotones.add(btnMostrarHistorial);

        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelTabla(String titulo, JTable tabla) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(Estilos.COLOR_TEXTO);

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(Estilos.scrollEstilizado(tabla), BorderLayout.CENTER);
        return panel;
    }
}
