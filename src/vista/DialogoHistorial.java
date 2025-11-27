package vista;

import modelo.*;
import vista.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Stack;
import java.util.stream.Stream;

/**
 * Diálogo modal que muestra el historial completo de clientes y la pila de acciones.
 *
 * <p>Proporciona filtros por ID, prioridad y tipo, y una vista del registro de acciones
 * (pila) gestionada por {@link modelo.GestionAtencion}.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class DialogoHistorial extends JDialog {

    private final GestionAtencion gestion;
    private final JTable tablaHistorial;
    private final DefaultTableModel modelo;
    private final JTextArea areaAcciones;

    // ------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------
    /**
     * Crea el diálogo modal que muestra historial y registro de acciones.
     *
     * @param vista ventana padre
     * @param gestion instancia de {@link GestionAtencion} con los datos
     * @since 1.0
     */
    public DialogoHistorial(JFrame vista, GestionAtencion gestion) {
        super(vista, "Historial General del Sistema", true);
        this.gestion = gestion;

        setSize(1100, 550);
        setLocationRelativeTo(vista);
        setLayout(new BorderLayout(10, 10));

        // ------------------------------------------------------------
        // TABLA DE CLIENTES (con hora de registro)
        // ------------------------------------------------------------
        String[] columnas = {"ID", "Nombre", "Tipo", "Prioridad", "Estado", "Hora Registro", "Tiempo (min)"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaHistorial = new JTable(modelo);
        tablaHistorial.setRowHeight(26);
        tablaHistorial.getTableHeader().setReorderingAllowed(false);
        Estilos.estilizarTabla(tablaHistorial);

        JScrollPane scrollTabla = new JScrollPane(tablaHistorial);

        // ------------------------------------------------------------
        // Panel de filtros
        // ------------------------------------------------------------
        JTextField txtBuscarId = new JTextField(10);
        JComboBox<Prioridad> comboPrioridad = new JComboBox<>(Prioridad.values());
        JComboBox<TipoSolicitud> comboTipo = new JComboBox<>(TipoSolicitud.values());

        JButton btnBuscarId = Estilos.boton("Buscar por ID", Estilos.COLOR_PRIMARIO);
        JButton btnBuscarPrioridad = Estilos.boton("Buscar por Prioridad", Estilos.COLOR_SECUNDARIO);
        JButton btnBuscarTipo = Estilos.boton("Buscar por Tipo", Estilos.COLOR_EXITO);
        JButton btnReiniciar = Estilos.boton("Mostrar Todos", Estilos.COLOR_NEUTRO);

        JPanel panelBusqueda = new JPanel(new FlowLayout());
        panelBusqueda.add(new JLabel("ID:"));
        panelBusqueda.add(txtBuscarId);
        panelBusqueda.add(btnBuscarId);
        panelBusqueda.add(new JLabel("Prioridad:"));
        panelBusqueda.add(comboPrioridad);
        panelBusqueda.add(btnBuscarPrioridad);
        panelBusqueda.add(new JLabel("Tipo:"));
        panelBusqueda.add(comboTipo);
        panelBusqueda.add(btnBuscarTipo);
        panelBusqueda.add(btnReiniciar);

        // ------------------------------------------------------------
        // Eventos de búsqueda
        // ------------------------------------------------------------
        btnBuscarId.addActionListener(e -> {
            String id = txtBuscarId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un ID para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            List<Cliente> resultados = gestion.buscarPorId(id);
            actualizarTabla(resultados);
        });

        btnBuscarPrioridad.addActionListener(e -> {
            Prioridad p = (Prioridad) comboPrioridad.getSelectedItem();
            List<Cliente> resultados = gestion.buscarPorPrioridad(p);
            actualizarTabla(resultados);
        });

        btnBuscarTipo.addActionListener(e -> {
            TipoSolicitud t = (TipoSolicitud) comboTipo.getSelectedItem();
            List<Cliente> resultados = gestion.buscarPorTipo(t);
            actualizarTabla(resultados);
        });

        btnReiniciar.addActionListener(e -> mostrarTodos());

        // ------------------------------------------------------------
        // HISTORIAL DE ACCIONES
        // ------------------------------------------------------------
        areaAcciones = new JTextArea(10, 40);
        areaAcciones.setEditable(false);
        areaAcciones.setFont(new Font("Consolas", Font.PLAIN, 13));
        areaAcciones.setText("=== HISTORIAL DE ACCIONES ===\n\n");
        cargarAcciones();

        JScrollPane scrollAcciones = new JScrollPane(areaAcciones);
        scrollAcciones.setBorder(BorderFactory.createTitledBorder("Registro de Acciones (Pila)"));

        // ------------------------------------------------------------
        // ENSAMBLAR
        // ------------------------------------------------------------
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        panelCentro.add(panelBusqueda, BorderLayout.NORTH);
        panelCentro.add(scrollTabla, BorderLayout.CENTER);

        add(panelCentro, BorderLayout.CENTER);
        add(scrollAcciones, BorderLayout.SOUTH);

        mostrarTodos(); // carga inicial
    }

    // ------------------------------------------------------------
    // Cargar clientes en la tabla
    // ------------------------------------------------------------
    private void actualizarTabla(List<Cliente> lista) {
        modelo.setRowCount(0);
        for (Cliente c : lista) {
            String estado = "Atendido";
            String tiempo = String.format("%.1f", c.getTiempoAtencion());
            if (gestion.getColaUrgentes().contains(c))
                estado = "En Espera (Urgente)";
            else if (gestion.getColaNormales().contains(c))
                estado = "En Espera (Normal)";
            else if (c.getTiempoAtencion() == 0)
                tiempo = "-";

            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getTipoSolicitud(),
                    c.getPrioridad(), estado,
                    c.getHoraIngreso() != null ? c.getHoraIngreso().toString() : "-", // ⏰ Nueva columna
                    tiempo
            });
        }
    }

    // ------------------------------------------------------------
    // Mostrar todos los clientes
    // ------------------------------------------------------------
    private void mostrarTodos() {
        List<Cliente> todos = Stream.of(
                gestion.getColaUrgentes().stream(),
                gestion.getColaNormales().stream(),
                gestion.getHistorial().stream()
        ).flatMap(s -> s).toList();
        actualizarTabla(todos);
    }

    // ------------------------------------------------------------
    // Cargar pila de acciones
    // ------------------------------------------------------------
    private void cargarAcciones() {
        Stack<Accion> acciones = gestion.getPilaAcciones();
        for (int i = acciones.size() - 1; i >= 0; i--) {
            areaAcciones.append(acciones.get(i).toString() + "\n");
        }
    }
}
