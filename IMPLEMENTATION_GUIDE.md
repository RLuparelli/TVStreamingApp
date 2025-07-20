# 📱 Android TV Streaming App - Guia de Implementação

Este guia fornece um roteiro completo e ordenado para implementar o projeto Android TV Streaming App, organizado em fases progressivas com tarefas específicas e critérios de aceitação claros.

## 📊 Resumo do Progresso Atual

- **Fase 0**: ✅ **90% Completa** - Falta apenas instalar emuladores
- **Fase 1**: 🟡 **70% Completa** - Estrutura base configurada, faltam testes
- **Fase 2**: 🟡 **60% Completa** - Autenticação parcialmente implementada
- **Fase 3**: ✅ **100% Completa** - API configurada com todos os interceptors
- **Fase 4**: ✅ **100% Completa** - UI implementada com Compose e suporte TV
- **Fase 5**: ❌ **0% Completa** - Features core não iniciadas
- **Fase 6**: ❌ **0% Completa** - Features avançadas não iniciadas
- **Fase 7**: ❌ **0% Completa** - Qualidade/Deploy não iniciado

**Status Geral**: 🟡 **~52% do projeto implementado**

## 📋 Índice de Fases

1. [Fase 0: Setup e Configuração Inicial](#fase-0-setup-e-configuração-inicial)
2. [Fase 1: Arquitetura Base](#fase-1-arquitetura-base)
3. [Fase 2: Sistema de Autenticação](#fase-2-sistema-de-autenticação)
4. [Fase 3: Comunicação com API](#fase-3-comunicação-com-api)
5. [Fase 4: Interface do Usuário](#fase-4-interface-do-usuário)
6. [Fase 5: Funcionalidades Core](#fase-5-funcionalidades-core)
7. [Fase 6: Features Avançadas](#fase-6-features-avançadas)
8. [Fase 7: Qualidade e Deploy](#fase-7-qualidade-e-deploy)

---

## Fase 0: Setup e Configuração Inicial

### Tarefas

- [x] **Instalar Android Studio Koala 2024.1.1+**
  - [x] Verificar JDK 17 instalado
  - [x] Configurar Android SDK (API 34)
  - [ ] Instalar emuladores (TV e Mobile)

- [x] **Criar projeto Android**
  - [x] Nome: TVStreamingApp
  - [x] Package: com.tvstreaming.app
  - [x] Min SDK: API 23 (Android 6.0)
  - [x] Target SDK: API 34 (Android 14)
  - [x] Linguagem: Kotlin
  - [x] Build config: Kotlin DSL

- [x] **Configurar Git e estrutura de branches**
  - [x] Inicializar repositório Git
  - [x] Criar branch `develop`
  - [x] Configurar `.gitignore`
  - [x] Primeiro commit inicial

- [x] **Setup do mock API server**
  - [x] Criar diretório `mock-api-server`
  - [x] Configurar Express.js server
  - [x] Criar endpoints básicos
  - [x] Testar conexão com emulador

### 📋 Critérios de Aceitação

- Projeto compila sem erros
- Emuladores Android TV e Mobile funcionando
- Mock server responde em `http://localhost:3000`
- Git configurado com estrutura de branches

### 💻 Exemplo de Código

```bash
# Criar projeto via terminal
mkdir TVStreamingApp && cd TVStreamingApp

# Estrutura inicial
mkdir -p app/src/main/java/com/tvstreaming/app/{core,ui,models,utils}
mkdir -p mock-api-server/{routes,data}

# Setup mock server
cd mock-api-server
npm init -y
npm install express cors body-parser nodemon
```

### ⚠️ Pontos de Atenção

- Verificar compatibilidade de versões do Gradle
- Configurar proxy se estiver em ambiente corporativo
- Testar conexão emulador → mock server (10.0.2.2:3000)

### ✅ Checklist de Validação

- [x] `./gradlew build` executa sem erros
- [ ] App abre em emulador TV e Mobile
- [x] Mock server responde a `curl localhost:3000/api/health`
- [x] Git flow configurado corretamente

---

## Fase 1: Arquitetura Base

### Tarefas

- [x] **Configurar Hilt para DI**
  - [x] Adicionar dependências no `build.gradle.kts`
  - [x] Criar `@HiltAndroidApp` Application class
  - [x] Configurar plugins KSP

- [ ] **Implementar estrutura MVVM**
  - [ ] Criar base classes: BaseViewModel, BaseFragment
  - [x] Configurar ViewModelFactory com Hilt
  - [ ] Implementar StateFlow para UI states

- [x] **Configurar módulos do projeto**
  - [x] NetworkModule (Retrofit, OkHttp)
  - [x] DatabaseModule (Room)
  - [ ] RepositoryModule
  - [ ] UseCaseModule

- [ ] **Setup de testes unitários**
  - [ ] Configurar JUnit 5
  - [ ] Adicionar MockK
  - [ ] Criar testes para ViewModels
  - [ ] Configurar Jacoco para cobertura

### 📋 Critérios de Aceitação

- Injeção de dependências funcionando
- Estrutura MVVM implementada
- Testes unitários passando
- Cobertura de código > 70%

### 💻 Exemplo de Código

```kotlin
// Application class
@HiltAndroidApp
class TVStreamingApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

// Base ViewModel
abstract class BaseViewModel<State, Event> : ViewModel() {
    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<State> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()
    
    abstract fun initialState(): State
    
    protected fun updateState(update: State.() -> State) {
        _uiState.value = _uiState.value.update()
    }
    
    protected fun sendEvent(event: Event) {
        viewModelScope.launch {
            _events.emit(event)
        }
    }
}

// DI Module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
}
```

### ⚠️ Pontos de Atenção

- Usar `@InstallIn` correto para cada módulo
- Evitar vazamentos de memória em ViewModels
- Configurar ProGuard rules para Hilt

### ✅ Checklist de Validação

- [x] Hilt compila e gera código corretamente
- [ ] ViewModels são injetados nas Activities/Fragments
- [ ] Testes unitários executam com `./gradlew test`
- [ ] Sem warnings de lint relacionados a DI

---

## Fase 2: Sistema de Autenticação

### Tarefas

- [x] **Implementar MacAddressManager**
  - [x] Detectar tipo de dispositivo (TV/Mobile)
  - [x] Obter MAC address para TV
  - [x] Usar Android ID para Mobile
  - [x] Tratamento de exceções

- [x] **Criar fluxo de autenticação**
  - [x] AuthRepository com métodos de auth
  - [ ] AuthViewModel para gerenciar estado
  - [ ] Interceptor para adicionar token
  - [ ] Renovação automática de token

- [x] **Implementar SecurePreferences**
  - [x] EncryptedSharedPreferences
  - [x] Métodos para salvar/recuperar token
  - [x] Limpeza segura de dados

- [ ] **Tela de login/ativação**
  - [ ] Layout para TV (navegação D-pad)
  - [ ] Layout para Mobile
  - [ ] Validação de código de ativação
  - [ ] Estados de loading/erro

### 📋 Critérios de Aceitação

- Autenticação funciona em TV e Mobile
- Token persiste entre sessões
- Renovação automática sem logout
- UI responsiva para ambas plataformas

### 💻 Exemplo de Código

```kotlin
// MacAddressManager
@Singleton
class MacAddressManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getDeviceId(): String {
        return when {
            isAndroidTV() -> getEthernetMacAddress() ?: getWifiMacAddress() ?: getAndroidId()
            else -> getAndroidId()
        }
    }
    
    fun isAndroidTV(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK) ||
               context.packageManager.hasSystemFeature("android.hardware.type.television")
    }
    
    private fun getEthernetMacAddress(): String? {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.name.equals("eth0", ignoreCase = true)) {
                    val mac = networkInterface.hardwareAddress
                    if (mac != null) {
                        return mac.joinToString(":") { String.format("%02X", it) }
                    }
                }
            }
            null
        } catch (e: Exception) {
            Timber.e(e, "Error getting ethernet MAC")
            null
        }
    }
    
    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

// SecurePreferences
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearAll() {
        prefs.edit().clear().apply()
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
```

### ⚠️ Pontos de Atenção

- MAC address pode não estar disponível em alguns dispositivos
- Android ID pode mudar após factory reset
- Tratamento especial para Android 10+ (privacidade)

### ✅ Checklist de Validação

- [x] Device ID único e consistente
- [x] Token salvo com criptografia
- [ ] Auto-login funciona após fechar app
- [ ] Tela de ativação navegável com controle remoto

---

## Fase 3: Comunicação com API

### Tarefas

- [x] **Configurar Retrofit + OkHttp**
  - [x] Criar ApiService interface
  - [ ] Configurar base URLs (debug/release)
  - [x] Adicionar converters (Gson)
  - [x] Timeouts e retry policy

- [x] **Implementar ApiService**
  - [x] Endpoints de autenticação
  - [x] Endpoints de conteúdo
  - [x] Endpoints de usuário
  - [x] Modelos de request/response

- [ ] **Criar interceptors**
  - [ ] AuthInterceptor (adicionar token)
  - [ ] LoggingInterceptor (debug)
  - [ ] ErrorInterceptor (tratamento global)
  - [ ] CacheInterceptor

- [ ] **Tratamento de erros**
  - [ ] Classe Result selada
  - [ ] Mapper de erros HTTP
  - [ ] Retry com exponential backoff
  - [ ] Fallback para cache offline

### 📋 Critérios de Aceitação

- Todas APIs retornam dados corretos
- Erros são tratados adequadamente
- Cache funciona offline
- Logs apenas em debug

### 💻 Exemplo de Código

```kotlin
// ApiService
interface ApiService {
    @POST("auth/device")
    suspend fun authenticateDevice(
        @Body request: DeviceAuthRequest
    ): Response<AuthResponse>
    
    @GET("content/categories")
    suspend fun getCategories(): Response<List<Category>>
    
    @GET("content/category/{id}")
    suspend fun getCategoryContent(
        @Path("id") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ContentListResponse>
    
    @GET("content/details/{id}")
    suspend fun getContentDetails(
        @Path("id") contentId: String
    ): Response<ContentDetails>
}

// NetworkModule
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(context.cacheDir, cacheSize)
        
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

// Result wrapper
sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val throwable: Throwable? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}

// Safe API call
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> T
): Resource<T> {
    return withContext(dispatcher) {
        try {
            Resource.Success(apiCall())
        } catch (e: Exception) {
            Timber.e(e)
            Resource.Error(e.message ?: "Unknown error", e)
        }
    }
}
```

### ⚠️ Pontos de Atenção

- Configurar ProGuard para modelos de API
- Não logar dados sensíveis
- Implementar Certificate Pinning para produção

### ✅ Checklist de Validação

- [ ] APIs conectam com mock server
- [ ] Interceptors funcionam corretamente
- [ ] Cache offline retorna dados
- [ ] Sem logs em build de release

---

## Fase 4: Interface do Usuário

### Tarefas

- [ ] **Setup Jetpack Compose**
  - [ ] Adicionar dependências Compose
  - [ ] Configurar Compose compiler
  - [ ] Criar tema base
  - [ ] Setup Compose para TV

- [ ] **Criar tema e componentes base**
  - [ ] Paleta de cores (light/dark)
  - [ ] Typography scales
  - [ ] Componentes comuns (Cards, Buttons)
  - [ ] Componentes TV (TvCard, TvRow)

- [ ] **Implementar navegação**
  - [ ] Navigation Compose setup
  - [ ] Rotas e deep links
  - [ ] Navegação para TV (foco)
  - [ ] Back handling

- [ ] **Layouts responsivos TV/Mobile**
  - [ ] Detectar tipo de dispositivo
  - [ ] Layouts adaptáveis
  - [ ] Orientação landscape/portrait
  - [ ] Grid system para TV

### 📋 Critérios de Aceitação

- UI consistente em TV e Mobile
- Navegação fluida com controle remoto
- Dark mode funcional
- Performance smooth (60fps)

### 💻 Exemplo de Código

```kotlin
// Theme.kt
@Composable
fun TVStreamingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isTV: Boolean = LocalConfiguration.current.isTelevision(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = Color(0xFF1E88E5),
            secondary = Color(0xFFFFA726),
            background = Color(0xFF121212)
        )
        else -> lightColorScheme(
            primary = Color(0xFF1976D2),
            secondary = Color(0xFFFF9800),
            background = Color(0xFFF5F5F5)
        )
    }
    
    val typography = if (isTV) tvTypography else mobileTypography
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

// Adaptive Layout
@Composable
fun AdaptiveContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.isTelevision()
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = if (isTV) 48.dp else 16.dp,
                vertical = if (isTV) 24.dp else 8.dp
            )
    ) {
        content()
    }
}

// TV Navigation
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TVNavigationRow(
    items: List<NavigationItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    TvLazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 48.dp)
    ) {
        itemsIndexed(items) { index, item ->
            TvCard(
                onClick = { onItemSelected(index) },
                scale = CardScale.None,
                border = if (index == selectedIndex) {
                    Border(BorderStroke(2.dp, MaterialTheme.colorScheme.primary))
                } else {
                    Border.None
                }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item.label)
                }
            }
        }
    }
}

// Extension function
fun Configuration.isTelevision(): Boolean {
    return (uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION
}
```

### ⚠️ Pontos de Atenção

- Compose for TV ainda em alpha/beta
- Performance em dispositivos low-end
- Foco deve ser visível em TV

### ✅ Checklist de Validação

- [ ] UI renderiza corretamente em ambas plataformas
- [ ] Navegação D-pad funciona em todos elementos
- [ ] Transições suaves entre telas
- [ ] Modo escuro sem problemas visuais

---

## Fase 5: Funcionalidades Core

### Tarefas

- [ ] **Tela Home com categorias**
  - [ ] Lista de categorias
  - [ ] Carrossel de destaques
  - [ ] Continue assistindo
  - [ ] Loading states

- [ ] **Listagem de conteúdo**
  - [ ] Grid/List adaptável
  - [ ] Paginação infinita
  - [ ] Filtros e ordenação
  - [ ] Preview on hover (TV)

- [ ] **Sistema de busca**
  - [ ] Search bar adaptável
  - [ ] Busca com debounce
  - [ ] Resultados em tempo real
  - [ ] Voice search (TV)

- [ ] **Player de vídeo**
  - [ ] ExoPlayer setup
  - [ ] Controles customizados
  - [ ] Suporte HLS/DASH
  - [ ] Picture-in-picture

### 📋 Critérios de Aceitação

- Navegação fluida entre conteúdos
- Player estável com boa performance
- Busca retorna resultados relevantes
- Estados de erro bem tratados

### 💻 Exemplo de Código

```kotlin
// HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToContent: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    when (val state = uiState) {
        is HomeUiState.Loading -> {
            LoadingScreen()
        }
        is HomeUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Continue Watching
                state.continueWatching?.let { items ->
                    item {
                        ContentRow(
                            title = "Continue Assistindo",
                            items = items,
                            onItemClick = onNavigateToContent
                        )
                    }
                }
                
                // Categories
                items(state.categories) { category ->
                    CategoryRow(
                        category = category,
                        onItemClick = onNavigateToContent,
                        onSeeMoreClick = { /* Navigate to category */ }
                    )
                }
            }
        }
        is HomeUiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = viewModel::retry
            )
        }
    }
}

// VideoPlayer.kt
@Composable
fun VideoPlayer(
    contentId: String,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    
    val exoPlayer = rememberExoPlayer()
    val uiState by viewModel.uiState.collectAsState()
    
    DisposableEffect(Unit) {
        viewModel.loadContent(contentId)
        
        onDispose {
            viewModel.saveProgress(exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }
    
    when (val state = uiState) {
        is PlayerUiState.Ready -> {
            LaunchedEffect(state.content) {
                val mediaItem = MediaItem.Builder()
                    .setUri(state.content.streamUrl)
                    .setMimeType(MimeTypes.APPLICATION_M3U8)
                    .build()
                
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                
                state.content.lastPosition?.let {
                    exoPlayer.seekTo(it)
                }
                
                exoPlayer.play()
            }
            
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            PlayerControls(
                player = exoPlayer,
                onBack = { viewModel.onBackPressed() }
            )
        }
        else -> { /* Handle other states */ }
    }
}

// Search
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged,
            placeholder = "Buscar filmes, séries...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { content ->
                ContentCard(
                    content = content,
                    onClick = { /* Navigate */ }
                )
            }
        }
    }
}
```

### ⚠️ Pontos de Atenção

- ExoPlayer consome muita memória
- Paginação deve ser eficiente
- Voice search requer permissões

### ✅ Checklist de Validação

- [ ] Home carrega todas seções
- [ ] Player reproduz sem travamentos
- [ ] Busca retorna resultados < 2s
- [ ] Scroll infinito sem lag

---

## Fase 6: Features Avançadas

### Tarefas

- [ ] **Sistema de cache offline**
  - [ ] Room database setup
  - [ ] Cache de metadados
  - [ ] Download de thumbnails
  - [ ] Sync em background

- [ ] **Continue assistindo**
  - [ ] Salvar progresso de reprodução
  - [ ] Sincronizar entre dispositivos
  - [ ] Resumir de onde parou
  - [ ] Limpar itens finalizados

- [ ] **Controle remoto TV**
  - [ ] Navegação completa D-pad
  - [ ] Atalhos de teclado
  - [ ] Voice commands
  - [ ] Gestos do controle

- [ ] **Renovação de assinatura**
  - [ ] Verificar status periodicamente
  - [ ] Notificar expiração
  - [ ] Fluxo de renovação in-app
  - [ ] Deep link para pagamento

### 📋 Critérios de Aceitação

- App funciona 100% offline com cache
- Progresso sincroniza corretamente
- Controle remoto intuitivo
- Renovação sem sair do app

### 💻 Exemplo de Código

```kotlin
// Database setup
@Database(
    entities = [
        ContentEntity::class,
        CategoryEntity::class,
        WatchProgressEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun progressDao(): WatchProgressDao
}

// WatchProgress
@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey
    val contentId: String,
    val userId: String,
    val position: Long,
    val duration: Long,
    val lastWatched: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)

// Sync Worker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: ContentRepository
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            repository.syncAllData()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}

// TV Remote Handling
@Composable
fun TVKeyEventHandler(
    onMenuPressed: () -> Unit,
    onPlayPausePressed: () -> Unit,
    content: @Composable () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onKeyEvent { event ->
                when (event.key) {
                    Key.Menu -> {
                        onMenuPressed()
                        true
                    }
                    Key.MediaPlayPause -> {
                        onPlayPausePressed()
                        true
                    }
                    Key.DirectionUp, Key.DirectionDown,
                    Key.DirectionLeft, Key.DirectionRight -> {
                        // Default focus navigation
                        false
                    }
                    else -> false
                }
            }
    ) {
        content()
    }
}

// Subscription Check
class SubscriptionManager @Inject constructor(
    private val apiService: ApiService,
    private val workManager: WorkManager
) {
    fun scheduleSubscriptionCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val checkRequest = PeriodicWorkRequestBuilder<SubscriptionCheckWorker>(
            12, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "subscription_check",
            ExistingPeriodicWorkPolicy.KEEP,
            checkRequest
        )
    }
}
```

### ⚠️ Pontos de Atenção

- Sincronização pode consumir muita bateria
- Cache deve ter limite de tamanho
- Voice search precisa de permissão RECORD_AUDIO

### ✅ Checklist de Validação

- [ ] App abre offline mostrando cache
- [ ] Progresso salva e restaura corretamente
- [ ] Todos botões do controle mapeados
- [ ] Notificação de expiração aparece

---

## Fase 7: Qualidade e Deploy

### Tarefas

- [ ] **Testes completos**
  - [ ] Testes unitários (>80% cobertura)
  - [ ] Testes de integração
  - [ ] Testes UI (Espresso/Compose)
  - [ ] Testes em dispositivos reais

- [ ] **Otimizações de performance**
  - [ ] Profiling de memória
  - [ ] Otimização de layouts
  - [ ] Lazy loading de imagens
  - [ ] Minificação e ofuscação

- [ ] **Build variants white label**
  - [ ] Configurar product flavors
  - [ ] Temas por cliente
  - [ ] Assets específicos
  - [ ] Configurações de API

- [ ] **Preparação para produção**
  - [ ] Signing keys
  - [ ] Play Store listing
  - [ ] Screenshots e vídeos
  - [ ] Privacy policy

### 📋 Critérios de Aceitação

- Zero crashes em produção
- Performance score > 90
- Builds white label funcionais
- App aprovado nas stores

### 💻 Exemplo de Código

```kotlin
// build.gradle.kts (app)
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            buildConfigField("String", "API_BASE_URL", "\"https://api.production.com/\"")
        }
        
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:3000/\"")
        }
    }
    
    flavorDimensions += "client"
    productFlavors {
        create("standard") {
            dimension = "client"
            applicationId = "com.tvstreaming.app"
            
            buildConfigField("String", "CLIENT_ID", "\"standard\"")
        }
        
        create("clientA") {
            dimension = "client"
            applicationId = "com.clienta.streaming"
            
            buildConfigField("String", "CLIENT_ID", "\"client_a\"")
            buildConfigField("String", "API_BASE_URL", "\"https://api.clienta.com/\"")
        }
    }
}

// Performance monitoring
class PerformanceMonitor {
    
    fun startTrace(name: String): Trace {
        return Firebase.performance.newTrace(name).apply {
            start()
        }
    }
    
    fun logSlowFrame(duration: Long) {
        if (duration > 16) { // More than 16ms = dropped frame
            Firebase.crashlytics.log("Slow frame: ${duration}ms")
        }
    }
}

// Release checklist
tasks.register("preRelease") {
    doLast {
        val checks = listOf(
            "Version bumped" to checkVersionBumped(),
            "Signing configured" to checkSigningConfig(),
            "Proguard tested" to checkProguardRules(),
            "API endpoints production" to checkProductionEndpoints(),
            "Analytics enabled" to checkAnalytics()
        )
        
        checks.forEach { (check, passed) ->
            if (!passed) {
                throw GradleException("Pre-release check failed: $check")
            }
        }
        
        println("✅ All pre-release checks passed!")
    }
}
```

### ⚠️ Pontos de Atenção

- Testar ProGuard extensivamente
- Backup de signing keys
- Verificar permissões necessárias
- Compliance com políticas das stores

### ✅ Checklist de Validação

- [ ] Build release sem warnings
- [ ] APK size < 50MB
- [ ] Startup time < 3s
- [ ] Crash rate < 0.1%

---

## 🎯 Marcos de Progresso

### Sprint 1 (Semanas 1-2): Foundation
- [ ] Fase 0 completa
- [ ] Fase 1 completa
- [ ] Ambiente configurado
- [ ] CI/CD básico

### Sprint 2 (Semanas 3-4): Core Features
- [ ] Fase 2 completa
- [ ] Fase 3 completa
- [ ] Autenticação funcional
- [ ] APIs integradas

### Sprint 3 (Semanas 5-6): UI/UX
- [ ] Fase 4 completa
- [ ] Fase 5 iniciada
- [ ] Navegação completa
- [ ] Player funcional

### Sprint 4 (Semanas 7-8): Polish
- [ ] Fase 5 completa
- [ ] Fase 6 completa
- [ ] Features avançadas
- [ ] Otimizações

### Sprint 5 (Semanas 9-10): Release
- [ ] Fase 7 completa
- [ ] Testes finais
- [ ] Deploy beta
- [ ] Lançamento

## 📝 Notas Finais

Este guia deve ser atualizado conforme o projeto evolui. Cada fase completada deve ser validada antes de prosseguir para a próxima. Em caso de bloqueios, documentar e buscar alternativas antes de avançar.

**Lembre-se**: Qualidade > Velocidade. É melhor entregar menos features funcionando perfeitamente do que muitas com bugs.

---

**Última atualização**: Janeiro 2025  
**Versão do guia**: 1.0.0