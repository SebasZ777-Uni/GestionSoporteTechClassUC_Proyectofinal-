package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una acción realizada sobre un cliente (agregar, eliminar, atender, restaurar).
 *
 * <p>Cada instancia registra el tipo de acción, el cliente afectado y la fecha/hora en que se creó.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class Accion {

    private TipoAccion tipoAccion;
    private Cliente cliente;
    private LocalDateTime fechaHora;

    /**
     * Crea una nueva acción asociada a un cliente; la fecha/hora se establece a la hora actual.
     *
     * @param tipoAccion tipo de acción realizada
     * @param cliente cliente afectado por la acción
     * @since 1.0
     */
    public Accion(TipoAccion tipoAccion, Cliente cliente) {
        this.tipoAccion = tipoAccion;
        this.cliente = cliente;
        this.fechaHora = LocalDateTime.now();
    }

    /**
     * Devuelve el tipo de acción.
     *
     * @return el {@link TipoAccion} de esta acción
     */
    public TipoAccion getTipoAccion() {
        return tipoAccion;
    }

    /**
     * Devuelve el cliente afectado por la acción.
     *
     * @return el {@link Cliente} asociado
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Devuelve la fecha y hora en que se creó la acción.
     *
     * @return {@link LocalDateTime} de creación
     */
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[" + fechaHora.format(formato) + "] " +
                tipoAccion + " → " + cliente.getNombre() +
                " (" + cliente.getId() + ")";
    }
}
