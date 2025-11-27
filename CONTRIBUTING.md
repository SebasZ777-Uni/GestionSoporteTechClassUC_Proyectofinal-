# Contribuir

Gracias por querer contribuir.

Convenciones:
- Ramas: `feature/descripcion`, `fix/descripción`.
- Commits: `type(scope): mensaje` (ej.: `docs(modelo): añadir JavaDoc para Cliente`).
- Abrir PR contra `main` desde `feature/*`.

Checklist en PR:
- [ ] Compila localmente (`javac`).
- [ ] JavaDoc generado y verificado si aplica.
- [ ] Tests (si los hay) pasan.
- [ ] Descripción clara del cambio.

Formato de issues/PRs:
- Título claro y corto.
- Descripción con motivo y solución propuesta.


Branch naming recomendado
-------------------------
- feature/<breve-descripción>
- fix/<breve-descripción>
- docs/<breve-descripción>

Commit message (convenzión)
--------------------------
Formato: `type(scope): mensaje breve`
Tipos recomendados:
- feat: nueva funcionalidad
- fix: corrección de bugs
- docs: documentación
- refactor: refactorización
- test: pruebas
- chore: tareas de mantenimiento

Checklist obligatorio en la descripción del PR
------------------------------------------------
- [ ] JavaDoc añadido/actualizado para las clases listadas.
- [ ] `docs/api` generado y verificado localmente.
- [ ] `README.md`, `CONTRIBUTING.md`, `JAVADOC_STYLE.md`, `CHANGELOG.md` añadidos/actualizados.
- [ ] Workflow `.github/workflows/javadoc.yml` incluido.
- [ ] Compilación local verificada (`javac`) y arranque básico de la app (`Main`).
- [ ] Commit messages siguen convención `type(scope): message`.
