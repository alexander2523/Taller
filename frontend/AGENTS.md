# Guía de React para el frontend

Estas instrucciones aplican a `frontend/`. La aplicación usa React 18, JSX y Vite; los componentes se encuentran en `src/` y las llamadas HTTP compartidas en `src/services/api.js`.

## Componentes y organización

- Escribe componentes funcionales y usa Hooks; no agregues componentes de clase.
- Mantén cada componente enfocado. Si una vista acumula formularios, tablas o lógica de varios módulos, separa piezas reutilizables en archivos propios dentro de `src/` y conserva `App.jsx` como punto de composición.
- Nombra componentes en `PascalCase` y funciones, variables y Hooks en `camelCase`. Los Hooks personalizados comienzan con `use`.
- Mantén la interfaz y los mensajes en español, siguiendo el vocabulario existente del taller.

## Estado y efectos

- Usa estado local para datos que solo necesita un componente. Eleva el estado únicamente cuando varios componentes deban compartirlo.
- Trata el estado como inmutable: crea arreglos y objetos nuevos al actualizarlo; no modifiques directamente valores de `useState`.
- Usa `useEffect` para sincronizar con sistemas externos, como la API o el navegador. Declara todas las dependencias utilizadas y limpia suscripciones, temporizadores y recursos (por ejemplo, URLs creadas con `URL.createObjectURL`).
- En cargas asíncronas, evita actualizar el estado tras desmontar el componente o después de que una solicitud anterior haya quedado obsoleta.
- No uses efectos para calcular valores que puedan derivarse durante el renderizado o dentro del manejador de una acción.

## Formularios, API y seguridad

- Usa componentes controlados cuando el valor del campo se refleje en el estado. Mantén etiquetas asociadas a sus controles y botones con el tipo explícito adecuado.
- Centraliza solicitudes en `src/services/api.js`; maneja estados de carga y errores junto a la acción que los produce. No dupliques lógica de autenticación en componentes.
- No insertes contenido no confiable con `dangerouslySetInnerHTML`, ni guardes secretos en el código o en variables `VITE_*`: esas variables se incluyen en el bundle del cliente.
- Conserva límites de validación de archivos y mensajes claros para las operaciones de subida.

## Interfaz y verificación

- Reutiliza Bootstrap y estilos de `src/style.css`; prioriza HTML semántico, navegación por teclado y estados visibles de error/carga.
- Ejecuta `pnpm run build` desde `frontend/` para comprobar que Vite compila. Actualmente no hay script de pruebas ni de lint; no afirmes que se ejecutaron.
