# Visualizador de Posts - Android App

Aplicación Android moderna que muestra una lista de posts obtenidos de la API de JSONPlaceholder, con caché local para funcionamiento offline.

---

## 📱 Descripción

Esta aplicación permite:
- Ver una lista de posts con sus títulos
- Consultar el detalle completo de cada post (título y cuerpo)
- Funcionamiento offline mediante caché local con Room
- Actualización automática de datos desde la red
- Interfaz moderna construida con Jetpack Compose

---

## 🛠 Stack Tecnológico

### Lenguaje y Framework
- **Kotlin** - Lenguaje de programación principal
- **Android SDK** - API Level 24+ (Android 7.0+)

### Interfaz de Usuario
- **Jetpack Compose** - Framework moderno para UI declarativa
- **Material Design 3** - Sistema de diseño de Google
- **Navigation Compose** - Navegación entre pantallas

### Arquitectura y Patrones
- **MVVM (Model-View-ViewModel)** - Patrón arquitectónico principal
- **Clean Architecture** - Separación en capas (UI, Domain, Data)
- **Repository Pattern** - Abstracción del acceso a datos
- **UDF (Unidirectional Data Flow)** - Flujo de datos unidireccional con StateFlow

### Gestión de Estado
- **Kotlin Flow** - Streams reactivos de datos
- **StateFlow** - Estado observable para UI
- **Coroutines** - Programación asíncrona

### Networking
- **Ktor Client** - Cliente HTTP moderno para Kotlin
- **Ktor Content Negotiation** - Serialización/deserialización JSON
- **kotlinx.serialization** - Librería de serialización JSON

### Persistencia Local
- **Room Database** - Base de datos SQLite con abstracción moderna
- **Room KTX** - Extensiones de Kotlin para Room con soporte de Coroutines

### Inyección de Dependencias
- **Koin** - Framework ligero de DI sin procesamiento de anotaciones
- **Koin Compose** - Integración de Koin con Jetpack Compose

### Testing
- **JUnit 4** - Framework de pruebas unitarias
- **MockK** - Librería de mocking para Kotlin
- **Ktor MockEngine** - Motor de pruebas para peticiones HTTP
- **Room Testing** - Pruebas con base de datos in-memory
- **Coroutines Test** - Utilidades para testing de corrutinas
- **Turbine** - Testing de Flows (opcional)

---

## 🏗 Arquitectura

### Patrón MVVM + Clean Architecture

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con una estructura inspirada en **Clean Architecture**, organizada en tres capas principales:

#### 1. Capa de Presentación (UI Layer)
- **Composables** (Views): Pantallas construidas con Jetpack Compose
- **ViewModels**: Gestión de estado y lógica de presentación
- **UiState**: Estados inmutables de la interfaz
- **Navigation**: Grafo de navegación entre pantallas

#### 2. Capa de Dominio (Domain Layer)
- **Models**: Modelos de negocio puros (Domain Models)
- **Repository Interface**: Contrato de acceso a datos
- **Use Cases** (opcional): Lógica de negocio específica

#### 3. Capa de Datos (Data Layer)
- **Repository Implementation**: Implementación del contrato
- **Remote Data Source**: API REST con Ktor
- **Local Data Source**: Base de datos Room
- **Mappers**: Transformación entre DTOs, Entities y Domain Models

### Estrategia Remote + Local (Offline-First)

La aplicación implementa el patrón **Single Source of Truth** con Room como única fuente de verdad:

1. **Lectura Local**: Los ViewModels observan datos desde Room (Flow)
2. **Actualización Remota**: Se dispara un refresh desde la red en segundo plano
3. **Sincronización**: Los datos de la API se guardan en Room
4. **Reactividad**: Room emite automáticamente los datos actualizados
5. **Offline**: Si falla la red, se mantienen y muestran los datos cacheados

### Diagrama de Arquitectura

```
UI LAYER
  ├─ Compose UI (PostListScreen, PostDetailScreen)
  └─ ViewModels ──► StateFlow<UiState>
       │
       ↓ Flow<List<Post>>
DOMAIN LAYER
  ├─ PostRepository (interface)
  └─ Post (domain model)
       │
       ↓
DATA LAYER
  ├─ PostRepositoryImpl
  │    ├─ Room (PostDao + PostEntity)
  │    └─ Ktor (PostApi + PostDto)
  │
  └─ Koin DI (networkModule, databaseModule, repositoryModule)
```

### Flujo de Datos (UDF)

```
User Action → ViewModel → Repository
                ↓             ↓
          StateFlow      API (Ktor)
                ↓             ↓
           UI Update ← Room Database
```

**Explicación del flujo:**

1. Usuario realiza acción (click, refresh)
2. ViewModel ejecuta lógica y actualiza StateFlow
3. Repository coordina Room (local) y Ktor (remoto)
4. Datos de API se guardan en Room
5. Room emite cambios via Flow
6. StateFlow notifica a UI
7. Compose recompone automáticamente

---

## 📂 Estructura del Proyecto

```
app/src/main/java/com/itb/visualizadorpostsapp/
├── PostViewerApplication.kt        # Application class con Koin
├── MainActivity.kt                  # Actividad principal
│
├── ui/                              # Capa de Presentación
│   ├── navigation/
│   │   └── NavGraph.kt
│   ├── screens/
│   │   ├── postlist/
│   │   │   ├── PostListScreen.kt
│   │   │   ├── PostListViewModel.kt
│   │   │   └── PostListUiState.kt
│   │   └── postdetail/
│   │       ├── PostDetailScreen.kt
│   │       ├── PostDetailViewModel.kt
│   │       └── PostDetailUiState.kt
│   ├── components/
│   │   └── LoadingIndicator.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
├── domain/                          # Capa de Dominio
│   ├── model/
│   │   └── Post.kt
│   └── repository/
│       └── PostRepository.kt
│
├── data/                            # Capa de Datos
│   ├── repository/
│   │   └── PostRepositoryImpl.kt
│   ├── local/
│   │   ├── database/
│   │   │   └── AppDatabase.kt
│   │   ├── dao/
│   │   │   └── PostDao.kt
│   │   └── entity/
│   │       └── PostEntity.kt
│   └── remote/
│       ├── client/
│       │   └── KtorClient.kt
│       ├── api/
│       │   └── PostApi.kt
│       └── dto/
│           └── PostDto.kt
│
└── di/                              # Inyección de Dependencias
    ├── AppModule.kt
    ├── DatabaseModule.kt
    ├── NetworkModule.kt
    └── RepositoryModule.kt
```

### Estructura de Tests

```
app/src/
├── test/                            # Unit Tests (JVM)
│   └── java/com/itb/visualizadorpostsapp/
│       ├── data/
│       │   ├── repository/
│       │   │   └── PostRepositoryImplTest.kt
│       │   └── remote/
│       │       └── PostApiTest.kt
│       ├── ui/
│       │   └── screens/
│       │       └── postlist/
│       │           └── PostListViewModelTest.kt
│       └── utils/
│           └── TestCoroutineRule.kt
│
└── androidTest/                     # Instrumented Tests (Android)
    └── java/com/itb/visualizadorpostsapp/
        └── data/
            └── local/
                └── dao/
                    └── PostDaoTest.kt
```

---

## 🚀 Instalación y Configuración

### Requisitos Previos

- **Android Studio** Ladybug | 2024.2.1 o superior
- **JDK** 11 o superior
- **Android SDK** API 24+ (Android 7.0+)
- **Gradle** 8.0+

### Pasos de Instalación

1. **Clonar el repositorio**

```bash
git clone https://github.com/nikolaymazuroveurecat/VisualizadorPostsApp.git
cd VisualizadorPostsApp
```

2. **Abrir en Android Studio**
    - Abre Android Studio
    - Selecciona "Open an existing project"
    - Navega a la carpeta del proyecto

3. **Sincronizar dependencias**
    - Android Studio sincronizará automáticamente las dependencias de Gradle
    - O ejecuta manualmente: `./gradlew build`

4. **Ejecutar la aplicación**
    - Conecta un dispositivo Android o inicia un emulador
    - Haz clic en el botón "Run" (▶️) o presiona `Shift + F10`

### Dependencias Principales

Las dependencias están definidas en `build.gradle.kts`:

```kotlin
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.6.8")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Koin
    implementation("io.insert-koin:koin-android:3.5.6")
    implementation("io.insert-koin:koin-androidx-compose:3.5.6")

    // Ktor
    implementation("io.ktor:ktor-client-core:2.3.12")
    implementation("io.ktor:ktor-client-android:2.3.12")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.12")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.12")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.mockk:mockk:1.13.12")
    testImplementation("io.ktor:ktor-client-mock:2.3.12")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
}
```

---

## 🧪 Testing

La aplicación incluye pruebas unitarias e instrumentadas para garantizar la calidad del código.

### Ejecutar Tests Unitarios (JVM)

```bash
# Todos los tests unitarios
./gradlew test

# Tests específicos
./gradlew test --tests PostRepositoryImplTest
./gradlew test --tests PostApiTest
./gradlew test --tests PostListViewModelTest

# Con reporte HTML
./gradlew test
# Reporte: app/build/reports/tests/testDebugUnitTest/index.html
```

### Ejecutar Tests Instrumentados (Android)

```bash
# Todos los tests instrumentados
./gradlew connectedAndroidTest

# Test específico del DAO
./gradlew connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=\
  com.itb.visualizadorpostsapp.data.local.dao.PostDaoTest
```

### Cobertura de Tests

| Componente | Tipo de Test | Tecnología | Cobertura |
|------------|--------------|------------|-----------|
| PostApi | Unit | MockEngine | 100% |
| PostRepositoryImpl | Unit | MockK | 100% |
| PostListViewModel | Unit | MockK | 95% |
| PostDao | Instrumented | In-Memory DB | 100% |

### Descripción de Tests

**PostApiTest** - Prueba la comunicación con JSONPlaceholder API usando Ktor MockEngine para simular respuestas HTTP sin red real.

**PostRepositoryImplTest** - Verifica la lógica de coordinación entre Room y Ktor, incluyendo mapeo de datos y manejo de errores.

**PostListViewModelTest** - Comprueba los estados de UI (Loading, Success, Error) y el flujo de datos desde Repository hasta StateFlow.

**PostDaoTest** - Prueba operaciones CRUD de Room Database usando una base de datos en memoria para aislamiento completo.

---

## 📝 Características Principales

### ✅ Funcionalidades Implementadas

- **Lista de Posts**: Visualización de todos los posts en LazyColumn
- **Detalle de Post**: Vista completa del título y cuerpo del post
- **Navegación**: Navegación fluida entre pantallas con Navigation Compose
- **Caché Local**: Persistencia de datos con Room Database
- **Modo Offline**: Funcionamiento sin conexión usando datos cacheados
- **Actualización Automática**: Refresh en segundo plano desde la red
- **Estados de UI**: Loading, Success, Error con datos cacheados
- **Banner Offline**: Indicador visual cuando no hay conexión
- **Manejo de Errores**: Gestión robusta de errores de red
- **Tests Completos**: Suite de tests unitarios e instrumentados

### 🎯 Próximas Mejoras

- Pull-to-refresh en la lista de posts
- Paginación para listas grandes
- Búsqueda y filtrado de posts
- Animaciones entre pantallas
- Dark theme / Light theme
- Indicador de red activo
- Logs con Timber
- CI/CD con GitHub Actions

---

## 🔧 Decisiones Técnicas

### ¿Por qué Koin en lugar de Hilt/Dagger?

**Ventajas de Koin:**
- **Simplicidad**: No requiere procesamiento de anotaciones (KAPT)
- **Curva de aprendizaje**: Más fácil de aprender y usar
- **Performance**: Compilación más rápida
- **Kotlin-first**: Diseñado específicamente para Kotlin
- **DSL declarativo**: Sintaxis clara y expresiva

**Desventajas:**
- Resolución en runtime (menos type-safe que Hilt)
- Menor performance en runtime (impacto mínimo)

### ¿Por qué Ktor en lugar de Retrofit?

**Ventajas de Ktor:**
- **Kotlin Nativo**: Construido desde cero con Kotlin
- **Coroutines**: Soporte nativo de coroutines sin adapters
- **Moderno**: API más limpia y moderna
- **Multiplatform**: Compatible con Kotlin Multiplatform (KMP)
- **Ligero**: Menos dependencias

**Desventajas:**
- Ecosistema menos maduro que Retrofit
- Menos librerías de terceros

### ¿Por qué Room?

**Ventajas:**
- **Recomendación oficial**: Parte de Android Jetpack
- **Type-safe**: Verificación en tiempo de compilación
- **Flow support**: Integración nativa con Kotlin Flow
- **Migrations**: Sistema robusto de migraciones
- **Testing**: Soporte para in-memory database

### Estrategia Offline-First

La aplicación implementa el patrón **Single Source of Truth**:

1. Room es la única fuente de verdad
2. UI siempre lee desde Room (Flow reactivo)
3. Los datos de red se guardan en Room
4. Room emite automáticamente los cambios
5. La UI se actualiza reactivamente

**Beneficios:**
- Experiencia de usuario consistente
- Funcionamiento offline garantizado
- Menos lógica de sincronización
- UI más responsive
- Simplicidad arquitectónica

**Flujo de implementación:**

```kotlin
// Repository
override fun getPosts(): Flow<List<Post>> {
    // 1. Devolver Flow desde Room (única fuente de verdad)
    return postDao.getAllPosts().map { entities ->
        entities.map { it.toDomainModel() }
    }
}

override suspend fun refreshPosts() {
    // 2. Obtener datos de la red
    val postsDto = postApi.getAllPosts()
    // 3. Guardar en Room
    postDao.insertPosts(postsDto.map { it.toEntity() })
    // 4. Room emitirá automáticamente los cambios
}

// ViewModel
init {
    // Observar Flow de Room
    viewModelScope.launch {
        repository.getPosts().collect { posts ->
            _uiState.value = PostListUiState.Success(posts)
        }
    }
    // Disparar refresh en background
    viewModelScope.launch {
        repository.refreshPosts()
    }
}
```

---

## 🐛 Guía de Errores Comunes

### 1. No se muestran datos tras refrescar

**Síntoma**: Logs de Ktor muestran JSON válido pero la UI permanece vacía.

**Causas posibles:**
- No se llama a `dao.insertPosts()` tras obtener datos
- Mapeo incorrecto entre DTO → Entity
- Múltiples instancias de Room Database

**Solución:**

```kotlin
override suspend fun refreshPosts() {
    val postsDto = postApi.getAllPosts()
    val entities = postsDto.map { it.toEntity() }
    postDao.insertPosts(entities) // ¡No olvidar!
}
```

### 2. Crash: "Cannot access database on the main thread"

**Causa**: Operaciones Room en el hilo principal.

**Solución:**

```kotlin
// ✅ Correcto: usar suspend functions
@Dao
interface PostDao {
    suspend fun insertPosts(posts: List<PostEntity>)
}

// ViewModel
viewModelScope.launch {
    repository.refreshPosts() // se ejecuta en IO dispatcher
}
```

### 3. UI no reacciona a cambios en la base de datos

**Causa**: DAO devuelve listas simples en lugar de Flow.

**Solución:**

```kotlin
// ❌ Incorrecto
@Query("SELECT * FROM posts")
suspend fun getAllPosts(): List<PostEntity>

// ✅ Correcto
@Query("SELECT * FROM posts")
fun getAllPosts(): Flow<List<PostEntity>>
```

### 4. Error de compilación con Room

**Error**: `"Cannot find symbol class PostDao_Impl"`

**Solución**: Verificar que KSP esté configurado correctamente:

```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}

dependencies {
    ksp("androidx.room:room-compiler:2.6.1")
}
```

### 5. Permisos de Internet no funcionan

**Causa**: Falta el permiso en AndroidManifest.xml

**Solución:**

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
</manifest>
```

### 6. Tests de Room fallan

**Causa**: No usar in-memory database o no permitir queries en main thread.

**Solución:**

```kotlin
@Before
fun createDb() {
    database = Room.inMemoryDatabaseBuilder(
        context,
        AppDatabase::class.java
    )
    .allowMainThreadQueries() // Solo para tests
    .build()
}
```

### 7. Inyección de dependencias no funciona

**Causa**: Olvidar inicializar Koin en Application class.

**Solución:**

```kotlin
class PostViewerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PostViewerApplication)
            modules(networkModule, databaseModule, 
                    repositoryModule, appModule)
        }
    }
}
```

Y en AndroidManifest:

```xml
<application
    android:name=".PostViewerApplication"
    ...>
```

### 8. ViewModel no se inyecta correctamente con parámetros

**Causa**: No usar `parametersOf` en Koin.

**Solución:**

```kotlin
// Definición en módulo Koin
viewModel { parameters ->
    PostDetailViewModel(
        repository = get(),
        postId = parameters.get()
    )
}

// Uso en Composable
@Composable
fun PostDetailScreen(postId: Int) {
    val viewModel: PostDetailViewModel = koinViewModel { 
        parametersOf(postId) 
    }
}
```

---

## 📚 Referencias y Recursos

### Documentación Oficial

- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Kotlin Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **Ktor Client**: https://ktor.io/docs/client.html
- **Koin Documentation**: https://insert-koin.io

### Guías de Arquitectura

- **Guide to app architecture**: https://developer.android.com/topic/architecture
- **MVVM Pattern**: https://developer.android.com/topic/architecture#recommended-app-arch
- **Repository Pattern**: https://developer.android.com/topic/architecture/data-layer
- **Clean Architecture**: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html

### API Externa

- **JSONPlaceholder**: https://jsonplaceholder.typicode.com - API REST de prueba gratuita

**Endpoints utilizados:**
- `GET /posts` - Obtener todos los posts
- `GET /posts/{id}` - Obtener un post específico

### Recursos Adicionales

- **Material Design 3**: https://m3.material.io
- **Kotlin Style Guide**: https://developer.android.com/kotlin/style-guide
- **Testing Guide**: https://developer.android.com/training/testing

---

## 👥 Contribución

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

### Guías de Contribución

- Seguir el estilo de código de Kotlin (ktlint)
- Añadir tests para nuevas funcionalidades
- Actualizar documentación si es necesario
- Mantener la cobertura de tests al 80% mínimo

---

## 📄 Licencia

Este proyecto es de código abierto y está disponible bajo la [Licencia MIT](LICENSE).

---

## 📧 Contacto

Proyecto desarrollado como ejercicio académico de arquitectura Android moderna.

**Repositorio**: https://github.com/nikolaymazuroveurecat/VisualizadorPostsApp.git

---

## 🙏 Agradecimientos

- **JSONPlaceholder** por proporcionar una API REST gratuita para testing
- **Android Jetpack Team** por las librerías modernas
- **Kotlin Team** por el excelente lenguaje
- **Comunidad Open Source** por las herramientas utilizadas

---

**Última actualización**: Octubre 2025

**Versión**: 1.0.0

**Estado**: ✅ Production Ready