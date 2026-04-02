# Proyecto - Desarrollo de aplicaciones para dispositivos móviles
**Integrantes**
- Vanessa Durán Mona 2359394
- Alejandra Osorio Giraldo 2266128
- David Mármol Otero 2266370
- Alejandro Garzón Mayorga 2266088

# Especificación de Requisitos de Software

## CineLog — Aplicación Móvil de Seguimiento de Películas y Series
**Norma:** IEEE 830
**Versión:** 1.0
**Fecha:** 2026
**Docente:** Miguel Askar


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
| **Rol** | Usuario |
| **Descripción** | El sistema debe permitir que un nuevo usuario cree una cuenta mediante nombre, edad, correo electrónico y contraseña usando Firebase Authentication. |
| **Prioridad** | Alta |
| **Entradas** | Nombre, edad, correo electrónico, contraseña |
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
| **Rol** | Usuario |
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
| **Rol** | Usuario |
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
| **Rol** | Usuario |
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
| **Rol** | Usuario |
| **Descripción** | El sistema debe mostrar información detallada de un ítem seleccionado, incluyendo datos obtenidos de la API de TMDB. |
| **Prioridad** | Alta |
| **Entradas** | Selección de un ítem desde los resultados de búsqueda o desde una lista personal |
| **Proceso** | El sistema consulta el endpoint de detalle en TMDB y renderiza la información en pantalla. |
| **Salida** | Pantalla de detalle con: poster, título, sinopsis, género(s), año, calificación global y botones para agregar a cualquiera de sus listas personales (ya visto, favoritas, ver más tarde). |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-05-1 | El usuario selecciona un ítem de la lista | Se navega a la pantalla de detalle con toda la información cargada |
| CA-05-2 | El ítem ya está en alguna lista personal | El botón correspondiente aparece resaltado/activo |
| CA-05-3 | La imagen del poster no está disponible | Se muestra un placeholder en lugar del poster |

---

### RF-06 — Gestión de Listas Personales

| Campo | Descripción |
|---|---|
| **ID** | RF-06 |
| **Nombre** | Agregar y eliminar contenido de listas personales |
| **Rol** | Usuario |
| **Descripción** | El sistema debe permitir al usuario organizar películas y series en tres listas: **Ver más tarde**, **Favoritas** y **Ya visto**. Un mismo ítem puede estar en más de una lista. |
| **Prioridad** | Alta |
| **Entradas** | Acción del usuario sobre los botones de lista en la pantalla de detalle |
| **Proceso** | El sistema guarda o elimina el ítem en la base de datos en la nube (Firestore) asociado al usuario autenticado. |
| **Salida** | El ítem aparece o desaparece de la lista correspondiente. El botón refleja el estado actual. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-06-1 | El usuario agrega un ítem a "Ver más tarde" | El ítem aparece en la lista "Ver más tarde" y el botón queda activo |
| CA-06-2 | El usuario agrega el mismo ítem a "Favoritas" | El ítem aparece en ambas listas simultáneamente |
| CA-06-3 | El usuario elimina un ítem de una lista | El ítem desaparece de esa lista sin afectar las otras |
| CA-06-4 | El usuario cierra y reabre la app | Las listas persisten con los mismos ítems |

---

### RF-07 — Registro de Reseña y Calificación

| Campo | Descripción |
|---|---|
| **ID** | RF-07 |
| **Nombre** | Registrar reseña personal y calificación |
| **Rol** | Usuario |
| **Descripción** | Cuando un usuario marca un ítem como "Ya visto", el sistema debe permitirle registrar una calificación del 1 al 5 y una reseña personal opcional. |
| **Prioridad** | Alta |
| **Entradas** | Calificación (1–5 estrellas), texto de reseña (opcional), fecha (automática) |
| **Proceso** | El sistema guarda la reseña y calificación en Firestore asociada al ítem y al usuario. |
| **Salida** | La reseña y calificación quedan visibles en la pantalla de detalle del ítem. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-07-1 | El usuario agrega un ítem a "Ya visto" con calificación y reseña | Los datos se guardan y se muestran en el detalle del ítem |
| CA-07-2 | El usuario agrega un ítem a "Ya visto" sin escribir reseña | Solo se guarda la calificación; el campo reseña queda vacío |
| CA-07-3 | El usuario edita una reseña existente | Los cambios se guardan y se reflejan de inmediato |
| CA-07-4 | El usuario elimina el ítem de "Ya visto" | La reseña y calificación asociadas también se eliminan |

---

### RF-08 — Pantalla de Mis Listas

| Campo | Descripción |
|---|---|
| **ID** | RF-08 |
| **Nombre** | Visualizar listas personales del usuario |
| **Rol** | Usuario |
| **Descripción** | El sistema debe mostrar una pantalla donde el usuario pueda navegar entre sus tres listas y ver los ítems guardados en cada una, mediante secciones. |
| **Prioridad** | Alta |
| **Entradas** | Navegación del usuario hacia la sección "Mis Listas" |
| **Proceso** | El sistema consulta Firestore y carga los ítems de cada lista para el usuario autenticado. |
| **Salida** | Pantalla con secciones para cada lista, mostrando poster de cada ítem. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-08-1 | El usuario tiene ítems en sus listas | Se muestran correctamente agrupados por lista |
| CA-08-2 | Una lista está vacía | Se muestra un estado vacío con mensaje ilustrativo |
| CA-08-3 | El usuario presiona un ítem de la lista | Navega al detalle de ese ítem |

---

### RF-09 — Pantalla de Perfil de Usuario

| Campo | Descripción |
|---|---|
| **ID** | RF-09 |
| **Nombre** | Ver y gestionar perfil de usuario |
| **Rol** | Usuario |
| **Descripción** | El sistema debe mostrar al usuario su foto de perfil, nombre, y permitirle acceder a las opciones de gestión de cuenta desde una pantalla centralizada. |
| **Prioridad** | Media |
| **Entradas** | Navegación del usuario hacia la sección "Mi cuenta" |
| **Proceso** | El sistema recupera los datos del usuario autenticado desde Firestore y renderiza las opciones disponibles. |
| **Salida** | Nombre del usuario, foto de perfil y opciones de navegación: Mis reseñas, Editar perfil, Estadísticas y Cerrar sesión. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-09-1 | El usuario navega a "Mi cuenta" | Se muestra su nombre, foto de perfil y las cuatro opciones del menú |
| CA-09-2 | El usuario pulsa "Editar perfil" | Se navega a la pantalla de edición de perfil |
| CA-09-3 | El usuario pulsa "Mis reseñas" | Se navega a la pantalla con sus reseñas |
| CA-09-4 | El usuario pulsa "Estadísticas" | Se navega a la pantalla de estadísticas |
| CA-09-5 | El usuario pulsa "Cerrar sesión" | La sesión se cierra y se redirige a la pantalla de inicio/login |

---

### RF-10 — Pantalla de Estadísticas

| Campo | Descripción |
|---|---|
| **ID** | RF-10 |
| **Nombre** | Ver estadísticas personales de actividad |
| **Rol** | Usuario |
| **Descripción** | El sistema debe mostrar al usuario estadísticas derivadas de su actividad en la app, incluyendo historial reciente, géneros más vistos y cantidad de ítems por lista. |
| **Prioridad** | Media |
| **Entradas** | Navegación del usuario hacia la sección "Estadísticas" desde el perfil |
| **Proceso** | El sistema calcula las estadísticas a partir de los datos almacenados en Firestore asociados al usuario autenticado, los géneros más vistos se calcularán con los ítems marcados como "Vistos", se toman todas las películas/series en la lista Vistas y se cuentan los géneros que más se repiten entre ellas. |
| **Salida** | Historial de películas/series recientes, géneros más vistos (etiquetas) y conteo de ítems en las listas: Vistas, Ver más tarde y Favoritas. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-10-1 | El usuario tiene actividad registrada | Se muestran las últimas películas vistas, géneros y conteos correctos por lista |
| CA-10-2 | El usuario no tiene actividad aún | Las estadísticas muestran ceros y el historial aparece vacío, sin errores |
| CA-10-3 | El usuario ha marcado ítems como favoritos | El contador de "Favoritas" refleja el número correcto |

---

### RF-11 — Pantalla Home con Tendencias

| Campo | Descripción |
|---|---|
| **ID** | RF-11 |
| **Nombre** | Mostrar contenido en tendencia desde TMDB |
| **Rol** | Usuario |
| **Descripción** | La pantalla principal debe mostrar películas y series en tendencia, obtenidas desde la API de TMDB, para que el usuario descubra nuevo contenido. |
| **Prioridad** | Media |
| **Entradas** | Apertura de la app por usuario autenticado |
| **Proceso** | El sistema consulta el endpoint de tendencias de TMDB y renderiza los resultados. |
| **Salida** | Carrusel de contenido en tendencia con poster. |

**Criterios de Aceptación:**

| ID | Criterio | Resultado Esperado |
|---|---|---|
| CA-11-1 | El usuario abre la app con conexión a internet | Se carga el contenido en tendencia correctamente |
| CA-11-2 | El usuario no tiene conexión a internet | Se muestra un mensaje de error y opción de reintentar |
| CA-11-3 | El usuario presiona un ítem del Home | Navega a la pantalla de detalle de ese ítem |

---

## 3. Requisitos No Funcionales

| ID | Requisito | Descripción |
|---|---|---|
| RNF-01 | Rendimiento | La app debe cargar resultados de búsqueda en menos de 3 segundos con conexión estable |
| RNF-02 | Usabilidad | La navegación debe ser intuitiva y seguir los lineamientos de Material Design 3 |
| RNF-03 | Persistencia | Los datos deben persistir entre sesiones usando Firestore |
| RNF-04 | Seguridad | Las contraseñas son gestionadas exclusivamente por Firebase Auth |
| RNF-05 | Compatibilidad | La aplicación debe funcionar en Android 8.0 (API 26) o superior |
| RNF-06 | Disponibilidad | La aplicación debe estar disponible siempre que exista conexión a internet y los servicios de Firebase estén activos |
| RNF-07 | Escalabilidad | El sistema debe permitir agregar nuevas funcionalidades sin afectar las existentes |
| RNF-08 | Mantenibilidad | El código debe estar estructurado siguiendo arquitectura MVVM |
| RNF-09 | Interfaz | La interfaz debe adaptarse a diferentes tamaños de pantalla |
| RNF-10 | Confiabilidad | La aplicación no debe cerrarse inesperadamente durante el uso normal |
