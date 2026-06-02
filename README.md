# Proyecto - Desarrollo de aplicaciones para dispositivos móviles
## Integrantes
- Vanessa Durán Mona 2359394
- Alejandra Osorio Giraldo 2266128
- David Mármol Otero 2266370
- Alejandro Garzón Mayorga 2266088

---

## 🎬 CineLog

### ¿Qué hace exactamente?
CineLog es una aplicación que permite al usuario buscar cualquier película o serie, ver su información detallada, y organizarlas en listas personales. También lleva un registro de lo que ya vio con su propia reseña y calificación.

---

### Funcionalidades concretas

**Autenticación**
- Registro e inicio de sesión con Firebase Auth
- Perfil básico del usuario (nombre, edad, correo)

**Exploración**
- Búsqueda de películas/series en tiempo real con TMDB
- Ver detalle: sinopsis, director, trailer (YouTube), año, duración

**Listas personales**
- Ver más tarde
- Favoritas
- Ya visto

**Registro personal**
- El usuario puede dejar su calificación (1-5 estrellas) y una reseña corta para cualquier película o serie.

**Estadísticas simples**
- Cuántas películas/series ha visto
- Cantidad de peliculas/series en cada lista
- Géneros más vistos

**Reseñas**
- Visualización, edición o eliminación de reseñas

**Perfil del usuario**
- Permite la edición de datos del usuario, al igual que la contraseña
- Recuperación de la contraseña

---

### Stack técnico

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | MVVM |
| API | TMDB API |
| Base de datos | Firestore |
| Auth | Firebase Auth |
| Navegación | Navigation Compose |

---

## Pasos para agregar la API Key

### Paso 1 — Obtener la API Key de TMDB

1. Ve a https://www.themoviedb.org y crea una cuenta gratuita.
2. Una vez dentro, ve a **Configuración → API**.
3. Solicita una API Key, selecciona **Developer** y completa el formulario indicando **Uso personal**.
4. TMDB te proporcionará:
   - API Key
   - Access Token

> Para este proyecto solo necesitas la **API Key**.

---

### Paso 2 — Guardar la API Key de forma segura

No agregues la API Key directamente en el código fuente.

Guárdala en el archivo `local.properties` ubicado en la raíz del proyecto (créalo si no existe). Este archivo es ignorado por Git de forma predeterminada.

```properties
TMDB_API_KEY=aqui_va_tu_api_key
```

---
### Pantallas

1. **Login / Registro**
2. **Home** — tendencias del momento (con TMDB)
3. **Búsqueda** — resultados en tiempo real
4. **Detalle** — poster, info, trailer, botones para agregar a lista
5. **Mis Listas** — ver más tarde / favoritas / vistas
6. **Reseña** — formulario al marcar como visto
7. **Perfil** — stats personales
8. **Estadísticas** - generos más vistos y cantidad de items por listas
