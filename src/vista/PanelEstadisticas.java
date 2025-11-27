package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.HashMap;

/**
 * Panel que muestra estadísticas visuales del sistema (gráficos y métricas).
 *
 * <p>Incluye métodos para actualizar los datos y dibujar gráficos de pastel/barras.
 * Es utilizado por {@link controlador.ControladorAtencion} para representar la
 * información actual de colas y atendidos.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class PanelEstadisticas extends JPanel {

    /** Etiqueta que muestra el total en espera. */
    public JLabel lblTotalEspera, lblTotalAtendidos, lblPromedio;
    /** Paneles donde se dibujan los gráficos. */
    public JPanel panelGraficoPrincipal, panelGraficoTipos, panelGraficoUrgentes;
    /** Combo para elegir tipo de gráfico (Pastel/Barras). */
    public JComboBox<String> cbTipoGrafico;

    private int totalEspera = 0;
    private int totalAtendidos = 0;
    private Map<String, Integer> tiposSolicitudes = new HashMap<>();
    private int urgentes = 0;
    private int normales = 0;

    /**
     * Construye el panel de estadísticas con gráficos y controles.
     *
     * @since 1.0
     */
    public PanelEstadisticas() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Estilos.COLOR_FONDO);

        // ------------------------------------------------------------
        // Título + ComboBox
        // ------------------------------------------------------------
        JPanel barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Estadísticas del Sistema", SwingConstants.CENTER);
        titulo.setFont(Estilos.FUENTE_TITULO);
        titulo.setForeground(Estilos.COLOR_SECUNDARIO);

        cbTipoGrafico = new JComboBox<>(new String[]{"Pastel", "Barras"});
        cbTipoGrafico.setFont(Estilos.FUENTE_NORMAL);
        cbTipoGrafico.setBackground(Color.WHITE);
        cbTipoGrafico.setPreferredSize(new Dimension(130, 30));

        JPanel panelCombo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelCombo.setOpaque(false);
        panelCombo.add(new JLabel("Tipo de gráfico:"));
        panelCombo.add(cbTipoGrafico);

        barraSuperior.add(titulo, BorderLayout.CENTER);
        barraSuperior.add(panelCombo, BorderLayout.EAST);

        add(barraSuperior, BorderLayout.NORTH);

        // ------------------------------------------------------------
        // Panel lateral con datos
        // ------------------------------------------------------------
        JPanel info = new JPanel(new GridLayout(3, 1, 10, 10));
        info.setOpaque(false);

        lblTotalEspera = new JLabel("Total en espera: 0");
        lblTotalAtendidos = new JLabel("Total atendidos: 0");
        lblPromedio = new JLabel("Promedio de atención: 0 min");

        for (JLabel lbl : new JLabel[]{lblTotalEspera, lblTotalAtendidos, lblPromedio}) {
            lbl.setFont(Estilos.FUENTE_NORMAL);
            lbl.setForeground(Estilos.COLOR_TEXTO);
            info.add(lbl);
        }

        add(info, BorderLayout.WEST);

        // ------------------------------------------------------------
        // Panel central con los gráficos (3)
        // ------------------------------------------------------------
        JPanel panelCentral = new JPanel(new GridLayout(3, 1, 10, 10));
        panelCentral.setOpaque(false);

        // Gráfico 1: Espera vs Atendidos
        panelGraficoPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoPrincipal((Graphics2D) g);
            }
        };
        panelGraficoPrincipal.setBackground(Color.WHITE);
        panelGraficoPrincipal.setBorder(BorderFactory.createTitledBorder("Gráfico: En espera vs Atendidos"));
        panelCentral.add(panelGraficoPrincipal);

        // Gráfico 2: Tipos de solicitud
        panelGraficoTipos = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoTipos((Graphics2D) g);
            }
        };
        panelGraficoTipos.setBackground(Color.WHITE);
        panelGraficoTipos.setBorder(BorderFactory.createTitledBorder("Gráfico: Tipos de Solicitud"));
        panelCentral.add(panelGraficoTipos);

        // Gráfico 3: Urgentes vs Normales
        panelGraficoUrgentes = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoUrgentes((Graphics2D) g);
            }
        };
        panelGraficoUrgentes.setBackground(Color.WHITE);
        panelGraficoUrgentes.setBorder(BorderFactory.createTitledBorder("Gráfico: Urgentes vs Normales"));
        panelCentral.add(panelGraficoUrgentes);

        add(panelCentral, BorderLayout.CENTER);
    }

    // ------------------------------------------------------------
    // Actualizar datos generales
    // ------------------------------------------------------------
    public void actualizarDatos(int espera, int atendidos, Map<String, Integer> tipos, int urgentes, int normales) {
        this.totalEspera = espera;
        this.totalAtendidos = atendidos;
        this.tiposSolicitudes = tipos;
        this.urgentes = urgentes;
        this.normales = normales;

        lblTotalEspera.setText("Total en espera: " + espera);
        lblTotalAtendidos.setText("Total atendidos: " + atendidos);

        panelGraficoPrincipal.repaint();
        panelGraficoTipos.repaint();
        panelGraficoUrgentes.repaint();
    }

    // ------------------------------------------------------------
    // Gráfico principal: Espera vs Atendidos
    // ------------------------------------------------------------
    // ------------------------------------------------------------
// Gráfico principal: Espera vs Atendidos (con leyenda)
// ------------------------------------------------------------
    private void dibujarGraficoPrincipal(Graphics2D g2) {
        int total = totalEspera + totalAtendidos;
        if (total == 0) return;

        int w = panelGraficoPrincipal.getWidth();
        int h = panelGraficoPrincipal.getHeight();
        String tipo = (String) cbTipoGrafico.getSelectedItem();

        // Colores base
        Color colorEspera = new Color(255, 99, 132); // rojo
        Color colorAtendidos = new Color(54, 162, 235); // azul

        if ("Pastel".equals(tipo)) {
            double angEspera = 360.0 * totalEspera / total;
            double angAtendidos = 360.0 * totalAtendidos / total;
            int size = Math.min(w, h) - 120;
            int x = (w - size) / 2;
            int y = (h - size) / 2;

            // Dibujar sectores
            g2.setColor(colorEspera);
            g2.fill(new Arc2D.Double(x, y, size, size, 0, angEspera, Arc2D.PIE));

            g2.setColor(colorAtendidos);
            g2.fill(new Arc2D.Double(x, y, size, size, angEspera, angAtendidos, Arc2D.PIE));

            DecimalFormat df = new DecimalFormat("#0.0");
            double pEspera = (100.0 * totalEspera / total);
            double pAtendidos = (100.0 * totalAtendidos / total);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

            double midEspera = Math.toRadians(angEspera / 2);
            double midAtendidos = Math.toRadians(angEspera + angAtendidos / 2);

            int cx = w / 2, cy = h / 2, r = size / 3;
            g2.drawString(df.format(pEspera) + "%", (int) (cx + r * Math.cos(midEspera)) - 15,
                    (int) (cy - r * Math.sin(midEspera)));
            g2.drawString(df.format(pAtendidos) + "%", (int) (cx + r * Math.cos(midAtendidos)) - 15,
                    (int) (cy - r * Math.sin(midAtendidos)));

            // -----------------------------
            // LEYENDA DE COLORES
            // -----------------------------
            int legendY = y + size + 40;
            int rectSize = 18;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Espera
            g2.setColor(colorEspera);
            g2.fillRect(x + 40, legendY, rectSize, rectSize);
            g2.setColor(Color.BLACK);
            g2.drawString("En espera (" + totalEspera + ")", x + 65, legendY + 14);

            // Atendidos
            g2.setColor(colorAtendidos);
            g2.fillRect(x + 220, legendY, rectSize, rectSize);
            g2.setColor(Color.BLACK);
            g2.drawString("Atendidos (" + totalAtendidos + ")", x + 245, legendY + 14);

        } else {
            // ------------------------------------------------------------
            // Gráfico de Barras con Leyenda
            // ------------------------------------------------------------
            int barWidth = 120;
            int maxBarHeight = h - 150;
            int baseY = h - 50;
            int spacing = 60;
            int xStart = (w - 2 * barWidth - spacing) / 2;

            int max = Math.max(totalEspera, totalAtendidos);
            int[] valores = {totalEspera, totalAtendidos};
            String[] nombres = {"En espera", "Atendidos"};
            Color[] colores = {colorEspera, colorAtendidos};

            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));

            for (int i = 0; i < 2; i++) {
                int altura = (int) (maxBarHeight * (valores[i] / (double) max));
                int x = xStart + i * (barWidth + spacing);

                // Barras
                g2.setColor(colores[i]);
                g2.fillRoundRect(x, baseY - altura, barWidth, altura, 15, 15);

                // Valor arriba
                g2.setColor(Color.BLACK);
                String numero = String.valueOf(valores[i]);
                int numWidth = g2.getFontMetrics().stringWidth(numero);
                g2.drawString(numero, x + (barWidth - numWidth) / 2, baseY - altura - 8);

                // Nombre debajo
                int textoWidth = g2.getFontMetrics().stringWidth(nombres[i]);
                g2.drawString(nombres[i], x + (barWidth - textoWidth) / 2, baseY + 25);
            }

            // -----------------------------
            // LEYENDA ABAJO
            // -----------------------------
            int legendY = baseY + 60;
            int rectSize = 18;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            // Espera
            g2.setColor(colorEspera);
            g2.fillRect(xStart, legendY, rectSize, rectSize);
            g2.setColor(Color.BLACK);
            g2.drawString("En espera", xStart + 25, legendY + 14);

            // Atendidos
            g2.setColor(colorAtendidos);
            g2.fillRect(xStart + 150, legendY, rectSize, rectSize);
            g2.setColor(Color.BLACK);
            g2.drawString("Atendidos", xStart + 175, legendY + 14);
        }
    }


    // ------------------------------------------------------------
    // Gráfico de Tipos de Solicitud
    // ------------------------------------------------------------
    private void dibujarGraficoTipos(Graphics2D g2) {
        if (tiposSolicitudes == null || tiposSolicitudes.isEmpty()) return;

        String tipoGrafico = (String) cbTipoGrafico.getSelectedItem();

        if ("Pastel".equals(tipoGrafico)) {
            int total = tiposSolicitudes.values().stream().mapToInt(Integer::intValue).sum();
            if (total == 0) return;

            int w = panelGraficoTipos.getWidth();
            int h = panelGraficoTipos.getHeight();
            int size = Math.min(w, h) - 120;
            int x = (w - size) / 2 - 60;
            int y = (h - size) / 2;

            double angInicio = 0;
            Color[] colores = {
                    new Color(255, 159, 64),
                    new Color(75, 192, 192),
                    new Color(153, 102, 255),
                    new Color(255, 205, 86),
                    new Color(201, 203, 207)
            };

            DecimalFormat df = new DecimalFormat("#0.0");
            int cx = x + size / 2, cy = y + size / 2;
            int r = size / 3;

            int i = 0;
            for (Map.Entry<String, Integer> e : tiposSolicitudes.entrySet()) {
                double ang = 360.0 * e.getValue() / total;
                g2.setColor(colores[i % colores.length]);
                g2.fill(new Arc2D.Double(x, y, size, size, angInicio, ang, Arc2D.PIE));

                // Porcentaje dentro del pastel
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                double angMedio = angInicio + ang / 2;
                g2.drawString(df.format(100.0 * e.getValue() / total) + "%",
                        (int) (cx + r * Math.cos(Math.toRadians(angMedio))) - 15,
                        (int) (cy - r * Math.sin(Math.toRadians(angMedio))));

                angInicio += ang;
                i++;
            }

            // === LEYENDA a la derecha ===
            int leyendaX = x + size + 50;
            int leyendaY = y + 30;
            i = 0;
            g2.setFont(Estilos.FUENTE_NORMAL);

            for (Map.Entry<String, Integer> e : tiposSolicitudes.entrySet()) {
                g2.setColor(colores[i % colores.length]);
                g2.fillRect(leyendaX, leyendaY + (i * 25), 18, 18);
                g2.setColor(Color.BLACK);
                g2.drawRect(leyendaX, leyendaY + (i * 25), 18, 18);
                g2.drawString(e.getKey(), leyendaX + 25, leyendaY + 14 + (i * 25));
                i++;
            }

        } else {
            // (gráfico de barras sin cambios)
            int w = panelGraficoTipos.getWidth();
            int h = panelGraficoTipos.getHeight();
            int n = tiposSolicitudes.size();
            int maxBarWidth = 100;
            int spacing = 15;
            int totalWidth = n * maxBarWidth + (n - 1) * spacing;
            int startX = (w - totalWidth) / 2;
            int baseY = h - 50;
            int maxBarHeight = h - 150;
            int max = tiposSolicitudes.values().stream().max(Integer::compare).orElse(1);

            Color[] colores = {
                    new Color(255, 159, 64),
                    new Color(75, 192, 192),
                    new Color(153, 102, 255),
                    new Color(255, 205, 86),
                    new Color(201, 203, 207)
            };

            int i = 0;
            for (Map.Entry<String, Integer> e : tiposSolicitudes.entrySet()) {
                int valor = e.getValue();
                int altura = (int) (maxBarHeight * (valor / (double) max));
                int x = startX + i * (maxBarWidth + spacing);

                g2.setColor(colores[i % colores.length]);
                g2.fillRect(x, baseY - altura, maxBarWidth, altura);

                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(valor), x + 35, baseY - altura - 5);
                g2.drawString(e.getKey(), x + 10, baseY + 20);
                i++;
            }
        }
    }



    // ------------------------------------------------------------
    // Gráfico de Urgentes vs Normales
    // ------------------------------------------------------------
    private void dibujarGraficoUrgentes(Graphics2D g2) {
        int total = urgentes + normales;
        if (total == 0) return;

        int w = panelGraficoUrgentes.getWidth();
        int h = panelGraficoUrgentes.getHeight();
        String tipo = (String) cbTipoGrafico.getSelectedItem();

        if ("Pastel".equals(tipo)) {
            double angUrgentes = 360.0 * urgentes / total;
            double angNormales = 360.0 * normales / total;
            int size = Math.min(w, h) - 120;
            int x = (w - size) / 2 - 60;
            int y = (h - size) / 2;

            Color colorUrg = new Color(255, 99, 132);
            Color colorNorm = new Color(54, 162, 235);

            g2.setColor(colorUrg);
            g2.fill(new Arc2D.Double(x, y, size, size, 0, angUrgentes, Arc2D.PIE));

            g2.setColor(colorNorm);
            g2.fill(new Arc2D.Double(x, y, size, size, angUrgentes, angNormales, Arc2D.PIE));

            DecimalFormat df = new DecimalFormat("#0.0");
            double pUrgentes = (100.0 * urgentes / total);
            double pNormales = (100.0 * normales / total);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));

            int cx = w / 2 - 60, cy = h / 2, r = size / 3;
            g2.drawString(df.format(pUrgentes) + "%", (int) (cx + r * Math.cos(Math.toRadians(angUrgentes / 2))) - 15,
                    (int) (cy - r * Math.sin(Math.toRadians(angUrgentes / 2))));
            g2.drawString(df.format(pNormales) + "%",
                    (int) (cx + r * Math.cos(Math.toRadians(angUrgentes + angNormales / 2))) - 15,
                    (int) (cy - r * Math.sin(Math.toRadians(angUrgentes + angNormales / 2))));

            // === LEYENDA al lado derecho ===
            int leyendaX = x + size + 80;
            int leyendaY = y + 40;

            g2.setColor(colorUrg);
            g2.fillRect(leyendaX, leyendaY, 18, 18);
            g2.setColor(Color.BLACK);
            g2.drawRect(leyendaX, leyendaY, 18, 18);
            g2.drawString("Urgentes", leyendaX + 25, leyendaY + 14);

            g2.setColor(colorNorm);
            g2.fillRect(leyendaX, leyendaY + 25, 18, 18);
            g2.setColor(Color.BLACK);
            g2.drawRect(leyendaX, leyendaY + 25, 18, 18);
            g2.drawString("Normales", leyendaX + 25, leyendaY + 39);

        } else {
            // (modo barras igual)
            int barWidth = 120;
            int maxBarHeight = h - 150;
            int baseY = h - 50;
            int spacing = 60;
            int xStart = (w - 2 * barWidth - spacing) / 2;

            int[] valores = {urgentes, normales};
            String[] nombres = {"Urgentes", "Normales"};
            Color[] colores = {new Color(255, 99, 132), new Color(54, 162, 235)};
            int max = Math.max(urgentes, normales);

            for (int i = 0; i < 2; i++) {
                int altura = (int) (maxBarHeight * (valores[i] / (double) max));
                int x = xStart + i * (barWidth + spacing);

                g2.setColor(colores[i]);
                g2.fillRect(x, baseY - altura, barWidth, altura);

                g2.setColor(Color.BLACK);
                g2.drawString(String.valueOf(valores[i]), x + 35, baseY - altura - 5);
                g2.drawString(nombres[i], x + 20, baseY + 20);
            }
        }
    }


}