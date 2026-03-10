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

## 3. Requisitos No Funcionales (Resumen)

| ID | Requisito | Descripción |
|---|---|---|
| RNF-01 | Rendimiento | La app debe cargar resultados de búsqueda en menos de 3 segundos con conexión estable |

---
