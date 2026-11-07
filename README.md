# Sistema de gestión para taller mecánico

Estructura inicial implementada: `backend/` Spring Boot 3 + Java 17, `frontend/` React + Bootstrap, `database.sql` y Mermaid en `diagramas/`.

## Arranque local

1. Crear la base ejecutando `database.sql` en MySQL 8.
2. Configurar `DB_URL`, `DB_USER`, `DB_PASSWORD` y ejecutar `cd backend && mvn spring-boot:run`.
3. Ejecutar `cd frontend && npm install && npm run start`; configurar `VITE_API_URL` si la API no está en `http://localhost:8080/api`.
4. `UPLOAD_DIR` establece la carpeta de imágenes. La API aplica 15 MB por archivo y registra excepciones no controladas en `app-error.log`.
5. Para crear la cuenta inicial, establecer `BOOTSTRAP_ADMIN_EMAIL` y `BOOTSTRAP_ADMIN_PASSWORD` antes de iniciar Spring Boot. La contraseña debe tener de 10 a 72 caracteres, mayúscula, minúscula y número.

Los identificadores se generan en Java como UUIDv7 y se almacenan como `BINARY(16)`; la base no genera IDs. Todas las operaciones de desactivación usan `activo`.

## Consideraciones de seguridad

El registro guarda hashes SHA-512, nunca contraseñas descifrables con AES. AES es cifrado reversible y no debe usarse para almacenar contraseñas. Spring Security marca los hashes digest como legado/no seguros; para producción, cambie a un hash adaptativo y use HTTPS. El autocompletado postal consulta Zippopotam.us en `/MX/{código postal}`; el proveedor puede sustituirse con `POSTAL_API_URL`. Fotos de taller y vehículos quedan bajo `UPLOAD_DIR` y se muestran por endpoints autenticados. La interfaz incluye registro, asignación de roles, configuración del taller, selector postal, vistas previas, ordenamiento y paginación.

## Despliegue con Docker

El `docker-compose.yml` sirve como despliegue autocontenido o entorno de prueba en un servidor que tenga Docker Compose. Para producción de un solo servidor, coloca un proxy inverso delante del puerto web y habilita HTTPS; no publiques los puertos de MySQL ni de la API directamente.

1. Instala Docker Engine con Compose en un servidor Linux y apunta el DNS de tu dominio a ese servidor.
2. Copia `.env.example` a `.env`, cambia cada secreto por uno largo y único, y establece `APP_CORS_ALLOWED_ORIGINS` con el origen HTTPS exacto del frontend. No subas `.env` al repositorio.
3. Ejecuta `docker compose up -d --build`. El frontend queda en el puerto `HTTP_PORT` (8080 por defecto) y el proxy de Nginx reenvía `/api` a la API. Configura tu proxy HTTPS para enviar tráfico a ese puerto.
4. El esquema de `database.sql` se importa automáticamente solo cuando MySQL inicializa un volumen vacío. `ddl-auto=validate` comprueba el esquema al arrancar la API. Cambios posteriores requieren respaldar la base y aplicar migraciones manualmente.
5. El volumen `mysql_data` conserva la base y `uploads_data` conserva las imágenes. Respalda ambos de forma independiente; los volúmenes no reemplazan los respaldos. Para servidores separados o plataformas administradas, usa MySQL administrado y almacenamiento persistente/objetos para las fotos, y construye el frontend con `VITE_API_URL` apuntando al endpoint HTTPS de la API.
6. Comprueba que puedes iniciar sesión con la cuenta bootstrap y subir y volver a abrir una foto después de reiniciar los servicios. Conserva los logs con `docker compose logs -f api`.

La cuenta bootstrap solo se crea si el correo todavía no existe. Guarda la contraseña en un gestor de secretos y elimina esas variables del entorno después del primer inicio si tu plataforma permite hacerlo. La autenticación actual usa SHA-512 con sal, que Spring considera un algoritmo legado; migra a BCrypt o Argon2 y define una estrategia para restablecer las contraseñas antes de exponer el servicio a usuarios reales. HTTPS es obligatorio porque el frontend usa autenticación HTTP Basic.

## Publicación en Vercel + Railway

Para publicar en internet con frontend estático en Vercel y API/MySQL en Railway:

1. Sube el repositorio a GitHub sin `.env` ni datos de producción.
2. En Railway crea un proyecto con un servicio MySQL y otro servicio conectado al repositorio. Configura el directorio raíz del servicio API como `backend`; Railway detectará su `Dockerfile`. Genera un dominio público para el servicio API.
3. En las variables del servicio API configura `PORT=8080`, `DB_URL` como `jdbc:mysql://${{MySQL.MYSQLHOST}}:${{MySQL.MYSQLPORT}}/${{MySQL.MYSQLDATABASE}}?serverTimezone=UTC`; `DB_USER` como `${{MySQL.MYSQLUSER}}`; y `DB_PASSWORD` como `${{MySQL.MYSQLPASSWORD}}` (ajusta `MySQL` al nombre del servicio de base). Agrega también `BOOTSTRAP_ADMIN_EMAIL`, `BOOTSTRAP_ADMIN_PASSWORD`, `APP_CORS_ALLOWED_ORIGINS` con el origen HTTPS final de Vercel y `UPLOAD_DIR=/data/uploads`. Añade un volumen persistente al servicio API en `/data` y configura `RAILWAY_RUN_UID=0` para permitir escritura en ese volumen con el Dockerfile actual. Railway mantiene MySQL privado por defecto: no habilites acceso público a la base.
4. Inicializa el esquema de MySQL en la base indicada por `MYSQLDATABASE`: conéctate a ella y ejecuta `database.sql` sin las líneas iniciales `CREATE DATABASE` y `USE taller_mecanico`, para que las tablas se creen en la base asignada por Railway. `ddl-auto=validate` hará fallar el arranque si las tablas no están.
5. En Vercel importa el repositorio y fija **Root Directory** en `frontend`. Usa `pnpm run build` y salida `dist` (también están declarados en `frontend/vercel.json`). En Environment Variables agrega `VITE_API_URL=https://TU-DOMINIO-API.up.railway.app/api` para Production y vuelve a desplegar.
6. Copia el dominio de producción que Vercel asignó y configura `APP_CORS_ALLOWED_ORIGINS` en Railway con ese origen exacto, por ejemplo `https://mi-taller.vercel.app` (sin `/` al final). Redepliega la API. Agrega el dominio propio de Vercel allí también si lo usas.
7. Prueba registro/inicio de sesión, acciones de cada rol, subida y lectura de imágenes, y reinicio/redeploy de API. Activa respaldos del volumen de Railway y prepara también respaldos lógicos de MySQL.

Vercel compila las variables `VITE_*` dentro del JavaScript público: nunca pongas contraseñas o secretos allí. No uses una URL privada de Railway como `MYSQLHOST` desde el frontend; solo la API debe acceder a MySQL. Los dominios temporales de Preview de Vercel no estarán permitidos por CORS hasta agregarlos explícitamente. Consulta los límites y precios actuales de cada plataforma antes de elegir un plan.
