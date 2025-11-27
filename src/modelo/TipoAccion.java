package modelo;

/**
 * Tipos de acciones que se pueden registrar en la gestión de atenciones.
 *
 * <ul>
 *   <li>{@code AGREGAR} - cuando se añade un cliente a una cola</li>
 *   <li>{@code ELIMINAR} - cuando se elimina un cliente de las colas</li>
 *   <li>{@code ATENDER} - cuando un cliente es atendido</li>
 *   <li>{@code RESTAURAR} - cuando se restaura una acción deshecha</li>
 * </ul>
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public enum TipoAccion {
    AGREGAR,
    ELIMINAR,
    ATENDER,
    RESTAURAR
}
