JAVADOC_STYLE
=============

Resumen de convenciones JavaDoc (español)

- Todas las clases públicas deben tener JavaDoc con: resumen, autor y @since.
- Métodos públicos/protegidos deben documentar: descripción breve, @param por cada parámetro,
  @return cuando correspondan, @throws si lanzan excepciones.
- Tono: descriptivo y conciso (1–3 frases en la descripción), ejemplos breves con {@code ...}.
- Longitud mínima de la descripción: 20 caracteres.
- Etiquetas obligatorias: @author, @since.
- Enlazar a tipos del proyecto con {@link paquete.Tipo} o {@link Tipo}.

Ejemplos
--------
/**
 * Calcula la media de una lista de valores.
 *
 * @param valores array de valores numéricos
 * @return promedio de los valores
 * @since 1.0
 */
public double calcularPromedio(double[] valores) { ... }

Notas
-----
- Mantener las descripciones en español (las etiquetas JavaDoc técnicas permanecen en su forma estándar).
- Evitar incluir información de implementación interna en el JavaDoc público.

