package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa un cliente que solicita atención.
 *
 * <p>Contiene información básica como identificador, nombre, tipo de solicitud,
 * prioridad, tiempo de atención (simulado) y la hora de ingreso al sistema.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class Cliente {

    private String id;
    private String nombre;
    private TipoSolicitud tipoSolicitud;
    private Prioridad prioridad;
    private double tiempoAtencion; // tiempo simulado
    private LocalDateTime horaIngreso; // NUEVO

    /**
     * Crea un cliente nuevo con la hora de ingreso actual.
     *
     * @param id identificador único del cliente (ej. "U1" o "N2")
     * @param nombre nombre del cliente
     * @param tipoSolicitud tipo de solicitud del cliente
     * @param prioridad prioridad asignada al cliente
     * @param horaIngreso parámetro reservado (se captura la hora actual internamente)
     * @since 1.0
     */
    public Cliente(String id, String nombre, TipoSolicitud tipoSolicitud, Prioridad prioridad, LocalDateTime horaIngreso) {
        this.id = id;
        this.nombre = nombre;
        this.tipoSolicitud = tipoSolicitud;
        this.prioridad = prioridad;
        this.tiempoAtencion = 0;
        // utilizar la hora de ingreso proporcionada por el llamador
        this.horaIngreso = horaIngreso != null ? horaIngreso : LocalDateTime.now();
    }

    // Getters y Setters
    /**
     * Devuelve el identificador del cliente.
     *
     * @return id del cliente
     */
    public String getId() { return id; }

    /**
     * Devuelve el nombre del cliente.
     *
     * @return nombre del cliente
     */
    public String getNombre() { return nombre; }

    /**
     * Devuelve el tipo de solicitud del cliente.
     *
     * @return tipo de solicitud ({@link TipoSolicitud})
     */
    public TipoSolicitud getTipoSolicitud() { return tipoSolicitud; }

    /**
     * Devuelve la prioridad del cliente.
     *
     * @return prioridad ({@link Prioridad})
     */
    public Prioridad getPrioridad() { return prioridad; }

    /**
     * Devuelve el tiempo de atención simulado (en minutos).
     *
     * @return tiempo de atención en minutos
     */
    public double getTiempoAtencion() { return tiempoAtencion; }

    /**
     * Establece el tiempo de atención (simulado) en minutos.
     *
     * @param tiempoAtencion tiempo en minutos
     */
    public void setTiempoAtencion(double tiempoAtencion) { this.tiempoAtencion = tiempoAtencion; }

    /**
     * Devuelve la hora de ingreso del cliente al sistema.
     *
     * @return {@link LocalDateTime} de ingreso
     */
    public LocalDateTime getHoraIngreso() { return horaIngreso; } // NUEVO

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Cliente{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipoSolicitud=" + tipoSolicitud +
                ", prioridad=" + prioridad +
                ", tiempoAtencion=" + String.format("%.2f", tiempoAtencion) +
                ", horaIngreso=" + horaIngreso.format(formato) +
                '}';
    }
}
