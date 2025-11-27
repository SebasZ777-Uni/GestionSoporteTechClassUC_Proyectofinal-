package vista;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Utilidades de estilo y componentes reutilizables para la UI (colores, fuentes y helpers).
 *
 * <p>Contiene constantes públicas con la paleta de colores y métodos estáticos para crear
 * componentes estilizados coherentes con la apariencia de la aplicación.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class Estilos {

    /** Color de fondo principal de la interfaz. */
    public static final Color COLOR_FONDO = new Color(245, 247, 250);
    /** Color primario (botones principales). */
    public static final Color COLOR_PRIMARIO = new Color(46, 134, 222);
    /** Color secundario (textos y acentos). */
    public static final Color COLOR_SECUNDARIO = new Color(52, 73, 94);
    /** Color usado para indicar éxito/confirmación. */
    public static final Color COLOR_EXITO = new Color(39, 174, 96);
    /** Color usado para indicar errores. */
    public static final Color COLOR_ERROR = new Color(231, 76, 60);
    /** Color de texto general. */
    public static final Color COLOR_TEXTO = new Color(33, 37, 41);
    /** Color neutro adicional. */
    public static final Color COLOR_NEUTRO = new Color(215, 235, 125);
    /** Fuente principal para títulos. */
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    /** Fuente normal para etiquetas y botones. */
    public static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);

    // ------------------------------------------------------------
    // Aplica estilo visual rojo/negro a una JTable
    // ------------------------------------------------------------
    /**
     * Aplica estilos visuales consistentes a una tabla: colores, encabezado y renderers.
     *
     * @param tabla la {@link JTable} a estilizar (no nulo)
     * @since 1.0
     */
    public static void estilizarTabla(JTable tabla) {
        // Fondo general
        tabla.setBackground(new Color(20, 20, 20));  // negro suave
        tabla.setForeground(Color.WHITE);            // texto blanco
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Color de selección
        tabla.setSelectionBackground(new Color(255, 45, 45)); // rojo fuerte
        tabla.setSelectionForeground(Color.WHITE);

        // Líneas y bordes
        tabla.setGridColor(new Color(60, 60, 60));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setRowHeight(28);

        // Encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(120, 0, 0)); // rojo oscuro
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.RED));

        // Alternar color de filas (efecto zebra)
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(30, 30, 30) : new Color(40, 40, 40));
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
    }

    /**
     * Crea un botón estilizado con el texto y color especificados.
     *
     * @param texto texto del botón
     * @param color color de fondo del botón
     * @return un {@link JButton} con estilo aplicado
     * @since 1.0
     */
    public static JButton boton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(FUENTE_NORMAL);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        return btn;
    }

    /**
     * Envuelve una tabla en un {@link JScrollPane} con margen interno consistente.
     *
     * @param tabla la tabla a envolver
     * @return JScrollPane que contiene la tabla
     * @since 1.0
     */
    public static JScrollPane scrollEstilizado(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        return scroll;
    }

    /**
     * Genera una tabla no editable con las columnas especificadas.
     *
     * @param columnas nombres de columnas
     * @return instancia de {@link JTable}
     * @since 1.0
     */
    public static JTable tablaModelo(String[] columnas) {
        return new JTable(new javax.swing.table.DefaultTableModel(null, columnas) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        });
    }
}
