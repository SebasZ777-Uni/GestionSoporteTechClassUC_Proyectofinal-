package controlador;
import modelo.*;
import vista.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Stream;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Controlador principal que coordina la interacción entre la capa de modelo
 * ({@link modelo.GestionAtencion}) y la interfaz de usuario (clases en {@code vista}).
 *
 * <p>Se encarga de enlazar eventos de los componentes Swing con la lógica de gestión
 * de clientes: agregar, atender, eliminar y deshacer acciones. También prepara
 * y actualiza las tablas y paneles de estadísticas.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class ControladorAtencion {

    private GestionAtencion gestion;
    private VentanaPrincipal vista;
    private String tipoGrafico = "Pastel"; // por defecto

    /**
     * Crea un controlador y enlaza la vista con el modelo.
     *
     * @param gestion instancia de {@link GestionAtencion} que contiene la lógica de negocio
     * @param vista instancia de {@link VentanaPrincipal} que representa la UI
     * @since 1.0
     */
    public ControladorAtencion(GestionAtencion gestion, VentanaPrincipal vista) {
        this.gestion = gestion;
        this.vista = vista;
        inicializarEventos();
        actualizarTablasYEstadisticas();
    }

    // ------------------------------------------------------------
    // Inicialización de eventos
    // ------------------------------------------------------------
    private void inicializarEventos() {

        vista.panelClientes.btnAgregar.addActionListener(e -> agregarCliente());
        vista.panelClientes.btnAtender.addActionListener(e -> atenderCliente());
        vista.panelClientes.btnEliminar.addActionListener(e -> eliminarClienteSeleccionado());
        vista.panelClientes.btnDeshacer.addActionListener(e -> deshacerAccion());
        vista.panelClientes.btnMostrarHistorial.addActionListener(e -> {
            new DialogoHistorial(vista, gestion).setVisible(true);
        });
        // Combo para cambiar el tipo de gráfico
        vista.panelEstadisticas.cbTipoGrafico.addActionListener(e -> {
            tipoGrafico = vista.panelEstadisticas.cbTipoGrafico.getSelectedItem().toString();
            dibujarGrafico();
        });
    }

    // ------------------------------------------------------------
    // Agregar cliente
    // ------------------------------------------------------------
    private void agregarCliente() {
        try {
            String nombre = null;
            do {
                nombre = JOptionPane.showInputDialog(vista, "Ingrese el nombre del cliente:");
                if (nombre == null) return; // Cancelar
                if (nombre.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Debe ingresar un nombre.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } while (nombre.trim().isEmpty());

            TipoSolicitud tipo = (TipoSolicitud) JOptionPane.showInputDialog(
                    vista, "Seleccione el tipo de solicitud:",
                    "Tipo de Solicitud", JOptionPane.QUESTION_MESSAGE,
                    null, TipoSolicitud.values(), TipoSolicitud.SOPORTE
            );
            if (tipo == null) return;

            Prioridad prioridad = (Prioridad) JOptionPane.showInputDialog(
                    vista, "Seleccione la prioridad:",
                    "Prioridad", JOptionPane.QUESTION_MESSAGE,
                    null, Prioridad.values(), Prioridad.NORMAL
            );
            if (prioridad == null) return;
            LocalDateTime horaIngreso = LocalDateTime.now();
            Cliente c = gestion.agregarCliente(nombre.trim(), tipo, prioridad);
            JOptionPane.showMessageDialog(vista, "Cliente agregado con ID: " + c.getId());
            actualizarTablasYEstadisticas();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al agregar cliente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------------------------------------------------
    // Atender cliente
    // ------------------------------------------------------------
    private void atenderCliente() {
        Cliente c = gestion.atenderCliente();
        if (c == null) {
            JOptionPane.showMessageDialog(vista, "No hay clientes en espera.");
            return;
        }
        JOptionPane.showMessageDialog(vista, "Cliente atendido:\n" + c.getNombre() +
                " (" + c.getId() + ")");
        actualizarTablasYEstadisticas();
    }

    // ------------------------------------------------------------
    // Eliminar cliente seleccionado en la tabla
    // ------------------------------------------------------------
    private void eliminarClienteSeleccionado() {
        JTable tabla = vista.panelClientes.tablaEspera;
        int fila = tabla.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un cliente en la tabla de espera para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = tabla.getValueAt(fila, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(vista,
                "¿Seguro que desea eliminar al cliente con ID " + id + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = gestion.eliminarCliente(id);
            if (eliminado) {
                JOptionPane.showMessageDialog(vista, "Cliente eliminado correctamente.");
                actualizarTablasYEstadisticas();
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró el cliente con ID " + id,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ------------------------------------------------------------
    // Metodo el boton deshacer
    // ------------------------------------------------------------
    private void deshacerAccion() {
        if (gestion.getPilaAcciones().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay acciones para deshacer.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Accion ultima = gestion.getPilaAcciones().peek();

        // Si la última acción ya fue una restauración, no seguimos
        if (ultima.getTipoAccion() == TipoAccion.RESTAURAR) {
            JOptionPane.showMessageDialog(vista, "No hay más acciones que restaurar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ejecutar la reversión
        gestion.deshacerUltimaAccion();
        actualizarTablasYEstadisticas();

        JOptionPane.showMessageDialog(vista,
                "Se ha deshecho la última acción: " + ultima.getTipoAccion() +
                        " → " + ultima.getCliente().getNombre() +
                        " (" + ultima.getCliente().getId() + ")",
                "Deshacer", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarTabla(DefaultTableModel modelo, List<Cliente> lista, GestionAtencion gestion) {
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
                    c.getPrioridad(), estado, tiempo
            });
        }
    }


    // ------------------------------------------------------------
    // Actualizar tablas y estadísticas
    // ------------------------------------------------------------
    private void actualizarTablasYEstadisticas() {
        DefaultTableModel modeloEspera = (DefaultTableModel) vista.panelClientes.tablaEspera.getModel();
        DefaultTableModel modeloAtendidos = (DefaultTableModel) vista.panelClientes.tablaAtendidos.getModel();
        modeloEspera.setRowCount(0);
        modeloAtendidos.setRowCount(0);
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        // ------------------------------------------------------------
        // Llenar tabla de espera (urgentes + normales)
        // ------------------------------------------------------------
        for (Cliente c : gestion.getColaUrgentes()) {
            modeloEspera.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getTipoSolicitud(),
                    c.getPrioridad(), "-", c.getHoraIngreso().format(formato)
            });
        }
        for (Cliente c : gestion.getColaNormales()) {
            modeloEspera.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getTipoSolicitud(),
                    c.getPrioridad(), "-", c.getHoraIngreso().format(formato)
            });
        }

        // ------------------------------------------------------------
        // Llenar tabla de atendidos (historial)
        // ------------------------------------------------------------
        for (Cliente c : gestion.getHistorial()) {
            modeloAtendidos.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getTipoSolicitud(),
                    c.getPrioridad(), String.format("%.1f", c.getTiempoAtencion()),
                    c.getHoraIngreso().format(formato)
            });
        }

        // ------------------------------------------------------------
        // Actualizar etiquetas resumen
        // ------------------------------------------------------------
        vista.panelEstadisticas.lblTotalEspera.setText("Total en espera: " + gestion.getTotalEnEspera());
        vista.panelEstadisticas.lblTotalAtendidos.setText("Total atendidos: " + gestion.getTotalAtendidos());
        vista.panelEstadisticas.lblPromedio.setText(
                "Promedio de atención: " + String.format("%.1f min", gestion.getPromedioTiempoAtencion())
        );

        // ------------------------------------------------------------
        // Preparar datos para los gráficos
        // ------------------------------------------------------------
        Map<String, Integer> tipos = new HashMap<>();
        tipos.put("SOPORTE", 0);
        tipos.put("MANTENIMIENTO", 0);
        tipos.put("RECLAMO", 0);
        // ------------------------------------------------------------
        // Calcular totales para gráficos (espera + atendidos)
        // ------------------------------------------------------------
        int urgentes = 0;
        int normales = 0;
        // Contar clientes en espera (urgentes + normales)
        for (Cliente c : gestion.getColaUrgentes()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
            urgentes++;
        }
        for (Cliente c : gestion.getColaNormales()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
            normales++;
        }

        // Contar clientes atendidos
        for (Cliente c : gestion.getHistorial()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
            if (c.getPrioridad() == Prioridad.URGENTE) {
                urgentes++;
            } else {
                normales++;
            }
        }

        // ------------------------------------------------------------
        // Actualizar panel de estadísticas (envía todo)
        // ------------------------------------------------------------
        vista.panelEstadisticas.actualizarDatos(
                gestion.getTotalEnEspera(),
                gestion.getTotalAtendidos(),
                tipos,
                urgentes,
                normales
        );
    }


    // ------------------------------------------------------------
// Actualizar gráficos en el panel de estadísticas
// ------------------------------------------------------------
    private void dibujarGrafico() {
        // ------------------------------------------------------------
        // Inicializar mapa de tipos
        // ------------------------------------------------------------
        Map<String, Integer> tipos = new HashMap<>();
        tipos.put("SOPORTE", 0);
        tipos.put("MANTENIMIENTO", 0);
        tipos.put("RECLAMO", 0);

        // Contar clientes en espera (urgentes + normales)
        for (Cliente c : gestion.getColaUrgentes()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
        }
        for (Cliente c : gestion.getColaNormales()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
        }

        // Contar clientes atendidos
        for (Cliente c : gestion.getHistorial()) {
            String tipo = c.getTipoSolicitud().name();
            tipos.put(tipo, tipos.get(tipo) + 1);
        }

        // ------------------------------------------------------------
        // Totales generales
        // ------------------------------------------------------------
        int totalEspera = gestion.getTotalEnEspera();
        int totalAtendidos = gestion.getTotalAtendidos();

        int urgentes = gestion.getColaUrgentes().size();
        int normales = gestion.getColaNormales().size();

        // ------------------------------------------------------------
        // Pasar los datos al panel (PanelEstadisticas)
        // ------------------------------------------------------------
        vista.panelEstadisticas.actualizarDatos(
                totalEspera,
                totalAtendidos,
                tipos,
                urgentes,
                normales
        );
    }

}