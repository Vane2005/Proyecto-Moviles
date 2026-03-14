# Proyecto - Desarrollo de aplicaciones para dispositivos móviles
## Integrantes
- Vanessa Durán Mona 2359394
- Alejandra Osorio Giraldo 2266128
- David Mármol Otero 2266370
- Alejandro Garzón Mayorga 2266088

---

## 🎬 CineLog

### ¿Qué hace exactamente?
El usuario puede buscar cualquier película o serie, ver su info detallada, y organizarlas en listas personales. También lleva un registro de lo que ya vio con su propia reseña y calificación.

---

### Funcionalidades concretas

**Autenticación**
- Registro e inicio de sesión con Firebase Auth
- Perfil básico del usuario (foto, nombre, correo)

**Exploración**
- Búsqueda de películas/series en tiempo real con TMDB
- Ver detalle: sinopsis, reparto, trailer (YouTube), género, año, rating global

**Listas personales**
- Ver más tarde
- Favoritas
- Ya visto

**Registro personal**
- Al marcar como "ya visto", el usuario puede dejar su calificación (1-5 estrellas) y una reseña corta
- Fecha en que la vio

**Estadísticas simples**
- Cuántas películas/series ha visto
- Géneros favoritos (basado en lo que marcó)
- Un pequeño "wrapped" personal

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

### Pantallas

1. **Splash / Onboarding**
2. **Login / Registro**
3. **Home** — tendencias del momento (con TMDB)
4. **Búsqueda** — resultados en tiempo real
5. **Detalle** — poster, info, trailer, botones para agregar a lista
6. **Mis Listas** — ver más tarde / favoritas / vistas
7. **Reseña** — formulario al marcar como visto
8. **Perfil** — stats personales
