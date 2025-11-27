package app;
import controlador.ControladorAtencion;
import modelo.GestionAtencion;
import vista.VentanaPrincipal;

/**
 * Punto de entrada de la aplicación GUI.
 *
 * <p>Inicia el modelo, la vista y el controlador y arranca el bucle de eventos Swing.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class Main {
    /**
     * Método principal que inicia la interfaz gráfica.
     *
     * @param args argumentos de línea de comandos (no usados)
     * @since 1.0
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            GestionAtencion modelo = new GestionAtencion();
            VentanaPrincipal vista = new VentanaPrincipal();
            new ControladorAtencion(modelo, vista);
            vista.setVisible(true);
        });
    }
}
