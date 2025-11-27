package modelo;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

/**
 * Gestiona las colas de atención, historial y acciones realizadas sobre clientes.
 *
 * <p>Implementa una política de atención prioritaria: 2 clientes urgentes → 1 normal.
 * Mantiene colas separadas para urgentes y normales, un historial de atendidos y una pila
 * de acciones para permitir deshacer/ restaurar operaciones.
 *
 * @author SebasZ777-Uni
 * @since 1.0
 */
public class GestionAtencion {

    private Deque<Cliente> colaUrgentes;
    private Deque<Cliente> colaNormales;
    private LinkedList<Cliente> historial;
    private Stack<Accion> pilaAcciones;
    private int contadorUrgentes;
    private int contadorNormales;
    private int cicloPrioritario; // controla 2 urgentes → 1 normal

    public GestionAtencion() {
        colaUrgentes = new ArrayDeque<>();
        colaNormales = new ArrayDeque<>();
        historial = new LinkedList<>();
        pilaAcciones = new Stack<>();
        contadorUrgentes = 1;
        contadorNormales = 1;
        cicloPrioritario = 0;
    }

    // ------------------------------------------------------
    // Agregar cliente
    // ------------------------------------------------------
    /**
     * Crea y agrega un cliente a la cola correspondiente según la prioridad.
     * El identificador se genera con prefijo `U` para urgentes y `N` para normales.
     *
     * @param nombre nombre del cliente (no nulo)
     * @param tipo tipo de solicitud del cliente
     * @param prioridad prioridad asignada al cliente
     * @return el {@link Cliente} creado con su id
     * @since 1.0
     */
    public Cliente agregarCliente(String nombre, TipoSolicitud tipo, Prioridad prioridad) {
        String id;
        Cliente nuevo;
        LocalDateTime horaIngreso = LocalDateTime.now(); // <-- genera la hora aquí
        if (prioridad.equals(Prioridad.URGENTE)) {
            id = "U" + contadorUrgentes++;
            nuevo = new Cliente(id, nombre, tipo, prioridad, horaIngreso);
            colaUrgentes.add(nuevo);
        } else {
            id = "N" + contadorNormales++;
            nuevo = new Cliente(id, nombre, tipo, prioridad, horaIngreso);
            colaNormales.add(nuevo);
        }

        pilaAcciones.push(new Accion(TipoAccion.AGREGAR, nuevo));
        return nuevo;
    }

    // ------------------------------------------------------
    // Atender cliente (2 urgentes → 1 normal)
    // ------------------------------------------------------
    /**
     * Atiende al siguiente cliente según la política prioritaria (2 urgentes ⇒ 1 normal).
     * Registra el tiempo de atención simulado y añade el cliente al historial.
     *
     * @return el {@link Cliente} atendido o {@code null} si no hay clientes en espera
     * @since 1.0
     */
    public Cliente atenderCliente() {
        Cliente atendido = null;

        if (colaUrgentes.isEmpty() && colaNormales.isEmpty()) {
            return null;
        }

        if (!colaUrgentes.isEmpty() && cicloPrioritario < 2) {
            atendido = colaUrgentes.poll();
            cicloPrioritario++;
        } else if (!colaNormales.isEmpty()) {
            atendido = colaNormales.poll();
            cicloPrioritario = 0; // reinicia ciclo
        } else if (!colaUrgentes.isEmpty()) {
            atendido = colaUrgentes.poll();
        }

        if (atendido != null) {
            double tiempoSimulado = 5 + Math.random() * 15; // 5–20 minutos
            atendido.setTiempoAtencion(tiempoSimulado);
            historial.add(atendido);
            pilaAcciones.push(new Accion(TipoAccion.ATENDER, atendido));
        }

        return atendido;
    }

    // ------------------------------------------------------
    // Eliminar cliente por ID
    // ------------------------------------------------------
    /**
     * Elimina un cliente de las colas por su identificador.
     * Si se encuentra, registra la acción en la pila de acciones.
     *
     * @param id identificador del cliente a eliminar (no nulo)
     * @return {@code true} si el cliente fue encontrado y eliminado; {@code false} en caso contrario
     * @since 1.0
     */
    public boolean eliminarCliente(String id) {
        Optional<Cliente> encontrado = buscarEnColas(id);
        if (encontrado.isPresent()) {
            Cliente c = encontrado.get();

            if (c.getPrioridad().equals(Prioridad.URGENTE)) {
                colaUrgentes.remove(c);
            } else if (c.getPrioridad().equals(Prioridad.NORMAL)) {
                colaNormales.remove(c);
            }

            pilaAcciones.push(new Accion(TipoAccion.ELIMINAR, c));
            return true;
        }
        return false;
    }

    // ------------------------------------------------------
    // Buscar en colas (privado)
    // ------------------------------------------------------
    private Optional<Cliente> buscarEnColas(String id) {
        return Stream.concat(colaUrgentes.stream(), colaNormales.stream())
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst();
    }

// ------------------------------------------------------
// BÚSQUEDAS GENERALES
// ------------------------------------------------------

    /**
     * Busca clientes por su identificador en colas e historial.
     *
     * @param id identificador a buscar
     * @return lista de clientes que coinciden con el id (puede estar vacía)
     * @since 1.0
     */
    public List<Cliente> buscarPorId(String id) {
        return Stream.of(
                        colaUrgentes.stream(),
                        colaNormales.stream(),
                        historial.stream()
                ).flatMap(s -> s)
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .toList();
    }

    /**
     * Busca clientes por prioridad entre colas e historial.
     *
     * @param prioridad prioridad a filtrar
     * @return lista de clientes que tienen la prioridad indicada
     * @since 1.0
     */
    public List<Cliente> buscarPorPrioridad(Prioridad prioridad) {
        return Stream.of(
                        colaUrgentes.stream(),
                        colaNormales.stream(),
                        historial.stream()
                ).flatMap(s -> s)
                .filter(c -> c.getPrioridad().equals(prioridad))
                .toList();
    }

    /**
     * Busca clientes por tipo de solicitud entre colas e historial.
     *
     * @param tipo tipo de solicitud a filtrar
     * @return lista de clientes que coinciden con el tipo
     * @since 1.0
     */
    public List<Cliente> buscarPorTipo(TipoSolicitud tipo) {
        return Stream.of(
                        colaUrgentes.stream(),
                        colaNormales.stream(),
                        historial.stream()
                ).flatMap(s -> s)
                .filter(c -> c.getTipoSolicitud().equals(tipo))
                .toList();
    }

    /**
     * Deshace la última acción registrada en la pila (AGREGAR, ELIMINAR o ATENDER).
     * El comportamiento exacto depende del tipo de acción.
     *
     * @since 1.0
     */
    public void deshacerUltimaAccion() {
        if (pilaAcciones.isEmpty()) return;

        // Miramos la última acción sin borrarla todavía
        Accion ultima = pilaAcciones.peek();
        Cliente c = ultima.getCliente();

        switch (ultima.getTipoAccion()) {
            case AGREGAR:
                if (c.getPrioridad() == Prioridad.URGENTE) {
                    colaUrgentes.remove(c);
                } else {
                    colaNormales.remove(c);
                }
                break;

            case ELIMINAR:
                if (c.getPrioridad() == Prioridad.URGENTE) {
                    colaUrgentes.addFirst(c);
                } else {
                    colaNormales.addFirst(c);
                }
                break;

            case ATENDER:
                historial.remove(c);
                if (c.getPrioridad() == Prioridad.URGENTE) {
                    colaUrgentes.addFirst(c);
                } else {
                    colaNormales.addFirst(c);
                }
                break;
        }

        //  Ahora sí registramos la nueva acción sin borrar la anterior
        pilaAcciones.push(new Accion(TipoAccion.RESTAURAR, c));
    }



    // ------------------------------------------------------
    // Estadísticas y contadores
    // ------------------------------------------------------
    /**
     * Devuelve el total de clientes en espera (urgentes + normales).
     *
     * @return número total de clientes en espera
     * @since 1.0
     */
    public int getTotalEnEspera() {
        return colaUrgentes.size() + colaNormales.size();
    }

    /**
     * Devuelve el número total de clientes atendidos (historial).
     *
     * @return total de atendidos
     * @since 1.0
     */
    public int getTotalAtendidos() {
        return historial.size();
    }

    /**
     * Calcula el promedio del tiempo de atención de los clientes del historial.
     *
     * @return promedio en minutos (0 si no hay historial)
     * @since 1.0
     */
    public double getPromedioTiempoAtencion() {
        if (historial.isEmpty()) return 0;
        return historial.stream()
                .mapToDouble(Cliente::getTiempoAtencion)
                .average()
                .orElse(0);
    }

    /**
     * Cuenta cuántos clientes atendidos del historial corresponden al tipo dado.
     *
     * @param tipo tipo de solicitud a contabilizar
     * @return contador de clientes del tipo especificado
     * @since 1.0
     */
    public long contarPorTipo(TipoSolicitud tipo) {
        return historial.stream()
                .filter(c -> c.getTipoSolicitud().equals(tipo))
                .count();
    }

    /**
     * Cuenta cuántos clientes atendidos del historial tienen la prioridad indicada.
     *
     * @param prioridad prioridad a contabilizar
     * @return contador de clientes con la prioridad indicada
     * @since 1.0
     */
    public long contarPorPrioridad(Prioridad prioridad) {
        return historial.stream()
                .filter(c -> c.getPrioridad().equals(prioridad))
                .count();
    }




    // ------------------------------------------------------
    // Getters
    // ------------------------------------------------------
    /**
     * Devuelve la cola de urgentes.
     *
     * @return {@link Queue} con clientes urgentes en espera
     */
    public Queue<Cliente> getColaUrgentes() {
        return colaUrgentes;
    }

    /**
     * Devuelve la cola de normales.
     *
     * @return {@link Queue} con clientes normales en espera
     */
    public Queue<Cliente> getColaNormales() {
        return colaNormales;
    }

    /**
     * Devuelve el historial de clientes atendidos.
     *
     * @return {@link LinkedList} con los clientes atendidos
     */
    public LinkedList<Cliente> getHistorial() {
        return historial;
    }

    /**
     * Devuelve la pila de acciones registradas.
     *
     * @return {@link Stack} con las {@link Accion} registradas
     */
    public Stack<Accion> getPilaAcciones() {
        return pilaAcciones;
    }
}
