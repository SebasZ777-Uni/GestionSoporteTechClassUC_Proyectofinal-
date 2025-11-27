# Sistema de Atención TechClassUC

Una aplicación de escritorio Java (Swing) para gestionar la atención de clientes en un entorno de soporte: colas con prioridad, historial de atenciones y paneles de estadísticas. Diseñada con patrón MVC y preparada para generar documentación JavaDoc.

---

## Descripción resumida

TechClassUC es una pequeña plataforma de escritorio destinada a coordinar la atención de clientes/solicitudes en centros de soporte. Permite el registro de clientes, clasificación por prioridad y tipo de solicitud, atención siguiendo reglas de prioridad, almacenamiento de un historial de atenciones y visualización de estadísticas operativas.

---

## Características principales

- Interfaz gráfica basada en Java Swing (ventana principal, paneles y diálogos).
- Sistema de colas con prioridad (p. ej. URGENTE / NORMAL) y política de servicio (2 urgentes → 1 normal).
- Registro y visualización de historial de atenciones.
- Panel de estadísticas en tiempo real (contadores, promedios y distribuciones básicas).
- Arquitectura en capas (MVC): separación clara entre modelo, vista y controlador.
- Documentación API generable con JavaDoc y preparada para publicación (docs/api).

---

## Tecnologías

- Java 17
- Java Swing (UI)
- Estructura MVC (paquetes `modelo`, `vista`, `controlador`)
- Herramientas: `javac`, `javadoc`, Git/GitHub
- Opcional: GitHub Actions para CI y despliegue de JavaDoc (workflow en `.github/workflows/javadoc.yml`).

---

## Visión de la arquitectura (paquetes)

Basado en la estructura de `src/` del repositorio:

- `src/app`
  - `Main.java` — clase con el punto de entrada de la aplicación.
- `src/controlador`
  - `ControladorAtencion.java` — orquesta la interacción entre la vista y el modelo.
- `src/modelo`
  - `Accion.java`, `Cliente.java`, `GestionAtencion.java`, `Prioridad.java`, `TipoAccion.java`, `TipoSolicitud.java` — entidades, reglas de negocio y manejo de colas/historial.
- `src/vista`
  - `VentanaPrincipal.java`, `PanelClientes.java`, `PanelEstadisticas.java`, `DialogoHistorial.java`, `Estilos.java` — componentes Swing y composición de la interfaz.

Nota: la persistencia (si existe) y detalles adicionales deben comprobarse en las clases del paquete `modelo`.

---

## Requisitos

- Java 17 instalado y `JAVA_HOME` configurado.
- Windows (las instrucciones de terminal incluidas usan PowerShell, pero el proyecto también funciona en otros OS con los comandos adecuados).
- Git (para clonar el repositorio).

---

## Clonar, compilar y ejecutar (Windows — PowerShell)

1) Clonar el repositorio:

```powershell
git clone https://github.com/<usuario>/<repositorio>.git
cd <repositorio>
```

2) Compilar todo (PowerShell):

```powershell
# Compila todos los .java encontrados en src en el directorio out
javac -d out -sourcepath src $(dir /b /s src\*.java)
```

3) Ejecutar la aplicación:

```powershell
java -cp out app.Main
```

4) Generar la documentación JavaDoc localmente:

```powershell
javadoc -d docs/api -sourcepath src -subpackages app,controlador,modelo,vista
```

Después de ejecutar el comando anterior, verifique que exista `docs/api/index.html`.

---

## Sistema de colas y prioridad (cómo funciona)

Resumen lógico del comportamiento implementado en `GestionAtencion` y clases relacionadas:

- Se mantienen colas separadas por `Prioridad` (por ejemplo `URGENTE` y `NORMAL`).
- Política de atención implementada (configurable en código): por cada 2 atenciones de clientes urgentes se procesa 1 cliente normal. En términos prácticos:
  - Ciclo de atención: Urgente → Urgente → Normal → repetir.
  - Si una de las colas está vacía, el sistema atiende desde la cola disponible.
- La selección del siguiente cliente tiene en cuenta también el `TipoSolicitud` y `TipoAccion` para posibles reglas de negocio adicionales.
- Cada atención se registra en el historial con marca temporal para permitir métricas de espera y servicio.

Consejos de inspección:
- Revise `src/modelo/GestionAtencion.java` para ver la implementación exacta de la política (colocación en cola, turno, y registro en historial).

---

## Estadísticas y reporting

Las estadísticas se exponen en `PanelEstadisticas` y contienen (según la implementación):

- Contadores de atenciones por prioridad (URGENTE, NORMAL).
- Tiempo promedio de espera (si se registran timestamps al encolar y al atender).
- Distribución por `TipoSolicitud` y `TipoAccion`.
- Historial consultable en `DialogoHistorial` con listados de atenciones pasadas.

Si se necesita exportación, añada un método en `GestionAtencion` para escribir el historial a CSV/JSON.

---

## Ejemplo de uso mínimo

1. Ejecutar la app (`app.Main`).
2. En el panel de clientes crear un cliente nuevo seleccionando prioridad y tipo de solicitud.
3. Pulsar "Atender" para procesar el siguiente cliente según la política de prioridad.
4. Consultar estadísticas y abrir el diálogo de historial para revisar atenciones.

---

## Capturas de pantalla (placeholders)

- Ventana principal: `docs/screenshots/main_window.png`
- Panel clientes: `docs/screenshots/panel_clientes.png`
- Panel estadísticas: `docs/screenshots/panel_estadisticas.png`
- Diálogo historial: `docs/screenshots/dialogo_historial.png`

---

## UML

Diagrama de clases (placeholder): `docs/uml/diagrama-clases.png`

---

## Documentación JavaDoc

El proyecto incluye soporte completo para documentación JavaDoc, permitiendo generar y publicar la API de clases del sistema.  
La documentación cubre los paquetes principales del modelo MVC:

- `app` — Punto de entrada de la aplicación.
- `controlador` — Lógica que conecta la interfaz gráfica con las operaciones del modelo.
- `modelo` — Clases de negocio, gestión de colas, historial, tipos y reglas de prioridad.
- `vista` — Componentes gráficos basados en Java Swing.

### 🛠️ Cómo generar la documentación

Desde el directorio raíz del proyecto, ejecute:

```bash

javadoc -d docs/api -sourcepath src -subpackages app,controlador,modelo,vista
```
---

## Contribución

Siga `CONTRIBUTING.md` para normas de colaboración. Resumen mínimo:

- Ramas: `feature/descripcion`, `fix/descripcion`.
- Commits: use formato `type(scope): mensaje` (ej. `feat(modelo): añadir gestión de prioridad`).
- Cree PR a `main` desde una rama feature y use la checklist en el PR description.

Checklist obligatorio en la descripción del PR:

- [ ] JavaDoc añadido/actualizado para las clases listadas.
- [ ] `docs/api` generado y verificado localmente.
- [ ] `README.md`, `CONTRIBUTING.md`, `JAVADOC_STYLE.md`, `CHANGELOG.md` añadidos/actualizados.
- [ ] Workflow `.github/workflows/javadoc.yml` incluido.
- [ ] Compilación local verificada (`javac`) y arranque básico de la app (`Main`).
- [ ] Commit messages siguen convención `type(scope): message`.

---

## Changelog

Consulte `CHANGELOG.md` para el historial de versiones (formato *Keep a Changelog*). Añada entradas para cada release mayor o cambio relevante.
 [Unreleased]

 JavaDoc básico añadido a las clases del paquete modelo.

 [1.0.0] - 2025-11-13

 Versión inicial del proyecto.

---

## Licencia

Proyecto con licencia MIT. Incluya un archivo `LICENSE` en la raíz. Ejemplo de cabecera de licencia:

```
MIT License

Copyright (c) 2025 Sebastián Polo Palacios

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
```

---

## Autor

Sebastián Polo Palacios (SebasZ777-Uni)
Lucas Hernández 

- Email: spolop@correo.unicordoba.edu.co
- GitHub: `https://github.com/SebasZ777-Uni`

---

## Comandos útiles (resumen rápido)

```powershell
# Compilar
javac -d out -sourcepath src $(dir /b /s src\*.java)

# Ejecutar
java -cp out app.Main

# Generar JavaDoc
javadoc -d docs/api -sourcepath src -subpackages app,controlador,modelo,vista
```

---

Fin del README.md