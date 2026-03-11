# Especificación de Requisitos de Software
## CineLog — Aplicación Móvil de Seguimiento de Películas y Series
**Norma:** IEEE 830
**Versión:** 1.0
**Fecha:** 2026
**Docente:** Miguel Askar

---

## 1. Introducción

### 1.1 Propósito
Este documento describe los requisitos funcionales del sistema para la aplicación móvil **CineLog**, desarrollada como proyecto final del curso de Desarrollo de Aplicaciones para Dispositivos Móviles. Su propósito es definir el comportamiento esperado del sistema y servir como referencia para el desarrollo, diseño y pruebas.

### 1.2 Alcance
CineLog es una aplicación móvil Android que permite a los usuarios buscar películas y series, organizarlas en listas personales y registrar sus reseñas y calificaciones. Consume la API pública de TMDB para obtener información actualizada del catálogo audiovisual.

### 1.3 Definiciones
| Término | Descripción |
|---|---|
| TMDB | The Movie Database, API pública de contenido audiovisual |
| Watchlist | Lista personalizada de contenido pendiente por ver |
| Usuario autenticado | Usuario que ha iniciado sesión en la aplicación |
| Ítem | Película o serie dentro de la aplicación |

---

## 2. Requisitos Funcionales

### RF-01 — Registro de Usuario

| Campo | Descripción |
|---|---|
| **ID** | RF-01 |
| **Nombre** | Registro de nuevo usuario |
| **Descripción** | El sistema debe permitir que un nuevo usuario cree una cuenta mediante correo electrónico y contraseña usando Firebase Authentication. |
| **Prioridad** | Alta |
| **Entradas** | Nombre, correo electrónico, contraseña |
| **Proceso** | El sistema valida el formato del correo y la fortaleza de la contraseña, luego crea la cuenta en Firebase Auth y almacena el perfil básico. |
| **Salida** | Cuenta creada y sesión iniciada automáticamente. El usuario es redirigido a la pantalla principal (Home). |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-01-1 | El usuario ingresa un correo con formato válido y una contraseña de mínimo 6 caracteres | La cuenta se crea exitosamente y el usuario accede al Home |
| CA-01-2 | El usuario ingresa un correo ya registrado | El sistema muestra el mensaje: *"Este correo ya está registrado"* |
| CA-01-3 | El usuario deja campos vacíos | El sistema muestra errores de validación en los campos correspondientes |
| CA-01-4 | El usuario ingresa una contraseña menor a 6 caracteres | El sistema muestra el mensaje: *"La contraseña debe tener al menos 6 caracteres"* |

---

### RF-02 — Inicio de Sesión

| Campo | Descripción |
|---|---|
| **ID** | RF-02 |
| **Nombre** | Inicio de sesión de usuario existente |
| **Descripción** | El sistema debe permitir que un usuario registrado inicie sesión con su correo y contraseña. |
| **Prioridad** | Alta |
| **Entradas** | Correo electrónico, contraseña |
| **Proceso** | Firebase Auth valida las credenciales. Si son correctas, se carga la sesión del usuario. |
| **Salida** | Sesión iniciada. El usuario es redirigido a la pantalla principal. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-02-1 | El usuario ingresa credenciales correctas | Accede exitosamente al Home |
| CA-02-2 | El usuario ingresa una contraseña incorrecta | El sistema muestra: *"Correo o contraseña incorrectos"* |
| CA-02-3 | El usuario ingresa un correo no registrado | El sistema muestra: *"Correo o contraseña incorrectos"* |
| CA-02-4 | El usuario deja campos vacíos | El sistema muestra errores de validación antes de intentar el login |

---

### RF-03 — Cierre de Sesión

| Campo | Descripción |
|---|---|
| **ID** | RF-03 |
| **Nombre** | Cierre de sesión |
| **Descripción** | El sistema debe permitir que el usuario autenticado cierre su sesión activa. |
| **Prioridad** | Media |
| **Entradas** | Acción del usuario desde la pantalla de Perfil |
| **Proceso** | Firebase Auth cierra la sesión y se limpian los datos en memoria. |
| **Salida** | El usuario es redirigido a la pantalla de Login. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-03-1 | El usuario presiona "Cerrar sesión" | Se cierra la sesión y se muestra la pantalla de Login |
| CA-03-2 | El usuario intenta navegar hacia atrás luego de cerrar sesión | El sistema no permite regresar al Home sin autenticación |

---

### RF-04 — Búsqueda de Películas y Series

| Campo | Descripción |
|---|---|
| **ID** | RF-04 |
| **Nombre** | Búsqueda de contenido en TMDB |
| **Descripción** | El sistema debe permitir al usuario buscar películas y series por título, consultando la API de TMDB en tiempo real. |
| **Prioridad** | Alta |
| **Entradas** | Texto ingresado en la barra de búsqueda |
| **Proceso** | El sistema realiza una petición a TMDB con el texto ingresado y muestra los resultados con poster, título y año. |
| **Salida** | Lista de resultados relevantes con poster, título y año de lanzamiento. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-04-1 | El usuario escribe un título existente (ej: "Inception") | Se muestran resultados relevantes con poster y año |
| CA-04-2 | El usuario escribe un título que no existe | El sistema muestra: *"No se encontraron resultados"* |
| CA-04-3 | El dispositivo no tiene conexión a internet | El sistema muestra un mensaje de error de conectividad |
| CA-04-4 | El usuario borra el texto de búsqueda | La lista de resultados se limpia |

---

### RF-05 — Visualización de Detalle de Contenido

| Campo | Descripción |
|---|---|
| **ID** | RF-05 |
| **Nombre** | Ver detalle de película o serie |
| **Descripción** | El sistema debe mostrar información detallada de un ítem seleccionado, incluyendo datos obtenidos de la API de TMDB. |
| **Prioridad** | Alta |
| **Entradas** | Selección de un ítem desde los resultados de búsqueda o desde una lista personal |
| **Proceso** | El sistema consulta el endpoint de detalle en TMDB y renderiza la información en pantalla. |
| **Salida** | Pantalla de detalle con: poster, título, sinopsis, género(s), año, calificación global y botones para agregar a listas. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-05-1 | El usuario selecciona un ítem de la lista | Se navega a la pantalla de detalle con toda la información cargada |
| CA-05-2 | El ítem ya está en alguna lista personal | El botón correspondiente aparece resaltado/activo |
| CA-05-3 | La imagen del poster no está disponible | Se muestra un placeholder en lugar del poster |
