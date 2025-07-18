# 📺 TV Streaming App Android - Guia Completo de Desenvolvimento

## 📋 Visão Geral do Projeto

Sistema de streaming multiplataforma para Android TV e dispositivos móveis, desenvolvido com arquitetura moderna e preparado para white-label. O aplicativo oferece uma experiência completa de streaming com autenticação via MAC address, interface responsiva e player de vídeo otimizado.

**Plataforma:** Android (Mobile & TV)  
**SDK Mínimo:** Android 6.0 (API 23)  
**SDK Target:** Android 14 (API 34)  
**Linguagem:** Kotlin  
**Status:** Em Desenvolvimento Ativo

## 🚀 Características Principais

### Core Features
- ✅ **Autenticação MAC Address**: Sistema seguro de identificação de dispositivos
- ✅ **Interface Responsiva**: Layouts otimizados para TV e Mobile
- ✅ **Player Adaptativo**: Streaming HLS/DASH com ExoPlayer
- ✅ **Navegação TV**: Suporte completo para controle remoto
- ✅ **Offline First**: Cache inteligente e sincronização
- ✅ **White Label**: Totalmente customizável por cliente

### Arquitetura Técnica
- ✅ **MVVM + Clean Architecture**: Separação clara de responsabilidades
- ✅ **Jetpack Compose**: UI moderna e declarativa
- ✅ **Coroutines + Flow**: Programação assíncrona eficiente
- ✅ **Hilt**: Injeção de dependências
- ✅ **Room + DataStore**: Persistência de dados
- ✅ **Retrofit + OkHttp**: Comunicação com APIs

## 🏗️ Arquitetura do Sistema

```
tvstreaming-app/
├── app/                          # Módulo principal do aplicativo
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tvstreaming/app/
│   │   │   │   ├── MainActivity.kt          # Activity principal
│   │   │   │   ├── TVStreamingApplication.kt # Application class
│   │   │   │   ├── core/                    # Núcleo do aplicativo
│   │   │   │   │   ├── api/                 # Serviços de API
│   │   │   │   │   ├── auth/                # Autenticação
│   │   │   │   │   ├── di/                  # Injeção de dependências
│   │   │   │   │   └── storage/             # Armazenamento local
│   │   │   │   ├── ui/                      # Interface do usuário
│   │   │   │   │   ├── screens/             # Telas (Compose)
│   │   │   │   │   ├── components/          # Componentes reutilizáveis
│   │   │   │   │   ├── navigation/          # Navegação
│   │   │   │   │   └── theme/               # Temas e estilos
│   │   │   │   ├── models/                  # Modelos de dados
│   │   │   │   ├── utils/                   # Utilitários
│   │   │   │   └── services/                # Serviços em background
│   │   │   └── res/                         # Recursos (layouts, strings, etc)
│   │   └── androidTest/                     # Testes instrumentados
│   └── build.gradle.kts                     # Configuração do módulo
├── mock-api-server/              # Servidor mock para desenvolvimento
│   ├── server.js                 # Express server
│   ├── data/                     # Dados mock
│   └── README.md                 # Documentação da API
├── build.gradle.kts              # Configuração raiz
├── settings.gradle.kts           # Configuração de módulos
└── gradle.properties             # Propriedades do projeto
```

## 🛠️ Configuração do Ambiente de Desenvolvimento

### Pré-requisitos

1. **Android Studio Koala | 2024.1.1** ou superior
   - Download: [developer.android.com/studio](https://developer.android.com/studio)

2. **JDK 17** ou superior
   ```bash
   java -version  # Verificar versão
   ```

3. **Android SDK**
   - SDK Tools 35.0.1+
   - Platform SDK 34 (Android 14)
   - Build Tools 35.0.1

4. **Dispositivos de Teste**
   - Emulador Android TV (API 31+)
   - Emulador Mobile (API 26+)
   - Dispositivo físico (recomendado para TV)

### Setup Inicial

```bash
# 1. Clonar o repositório
git clone https://github.com/seu-usuario/TVStreamingApp.git
cd TVStreamingApp

# 2. Configurar local.properties (se necessário)
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# 3. Build inicial
./gradlew clean build

# 4. Instalar no dispositivo/emulador
./gradlew installDebug

# 5. Executar testes
./gradlew test
./gradlew connectedAndroidTest
```

### Configuração do Mock API Server

```bash
# 1. Navegar para o diretório do servidor
cd mock-api-server

# 2. Instalar dependências
npm install

# 3. Iniciar o servidor
npm start  # Porta 3000

# 4. Modo desenvolvimento (com hot reload)
npm run dev
```

## 💻 Desenvolvimento

### 1. Sistema de Autenticação MAC

O sistema de autenticação identifica dispositivos únicos através do MAC address, permitindo login automático e gerenciamento de dispositivos.

```kotlin
// core/auth/MacAddressManager.kt
@Singleton
class MacAddressManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getMacAddress(): String? {
        return try {
            // Para Android TV
            if (isAndroidTV()) {
                getEthernetMacAddress() ?: getWifiMacAddress()
            } else {
                // Para Mobile
                getAndroidId() // MAC address não disponível em Android 6+
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun isAndroidTV(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }
    
    private fun getAndroidId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

// core/auth/AuthRepository.kt
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val macManager: MacAddressManager,
    private val securePrefs: SecurePreferences
) {
    suspend fun authenticateDevice(): Result<AuthResponse> {
        val deviceId = macManager.getMacAddress() ?: return Result.failure(
            Exception("Unable to get device ID")
        )
        
        return try {
            val response = apiService.authenticateDevice(
                DeviceAuthRequest(
                    deviceId = deviceId,
                    deviceType = if (macManager.isAndroidTV()) "android_tv" else "android_mobile",
                    platform = "android",
                    appVersion = BuildConfig.VERSION_NAME
                )
            )
            
            if (response.isSuccessful) {
                response.body()?.let { authResponse ->
                    // Salvar token seguramente
                    securePrefs.saveAuthToken(authResponse.token)
                    Result.success(authResponse)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Auth failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 2. Interface Responsiva com Jetpack Compose

Sistema de UI adaptativo que detecta automaticamente o tipo de dispositivo e ajusta a interface.

```kotlin
// ui/screens/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCategory: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isTV = LocalConfiguration.current.isTelevision()
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is HomeUiState.Loading -> LoadingScreen()
            is HomeUiState.Success -> {
                if (isTV) {
                    TVHomeContent(
                        categories = uiState.categories,
                        onCategoryClick = onNavigateToCategory
                    )
                } else {
                    MobileHomeContent(
                        categories = uiState.categories,
                        onCategoryClick = onNavigateToCategory
                    )
                }
            }
            is HomeUiState.Error -> ErrorScreen(
                message = uiState.message,
                onRetry = { viewModel.loadCategories() }
            )
        }
    }
}

// ui/components/TVHomeContent.kt
@Composable
fun TVHomeContent(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit
) {
    TvLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(48.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        items(categories) { category ->
            TvCard(
                onClick = { onCategoryClick(category.id) },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = category.iconUrl,
                        contentDescription = category.name,
                        modifier = Modifier.size(120.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    Column {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = category.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ui/navigation/TVNavigationHandler.kt
@Composable
fun TVNavigationHandler(
    navController: NavHostController
) {
    BackHandler {
        if (!navController.popBackStack()) {
            // Exibir diálogo de saída
        }
    }
    
    // Detectar teclas do controle remoto
    val focusManager = LocalFocusManager.current
    
    LaunchedEffect(Unit) {
        // Configurar navegação por D-pad
    }
}
```

### 3. Player de Vídeo com ExoPlayer

Player completo com suporte a streaming adaptativo, legendas e controles customizados.

```kotlin
// ui/player/VideoPlayerScreen.kt
@Composable
fun VideoPlayerScreen(
    contentId: String,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setLoadControl(
                DefaultLoadControl.Builder()
                    .setBufferDurationsMs(
                        MIN_BUFFER_MS,
                        MAX_BUFFER_MS,
                        PLAYBACK_BUFFER_MS,
                        REBUFFER_MS
                    )
                    .build()
            )
            .build()
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    DisposableEffect(lifecycle, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        
        lifecycle.addObserver(observer)
        
        onDispose {
            lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }
    
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PlayerUiState.Ready -> {
                val mediaItem = MediaItem.Builder()
                    .setUri(state.content.streamUrl)
                    .setSubtitleConfigurations(
                        state.content.subtitles.map { subtitle ->
                            MediaItem.SubtitleConfiguration.Builder(subtitle.uri)
                                .setMimeType(MimeTypes.TEXT_VTT)
                                .setLanguage(subtitle.language)
                                .build()
                        }
                    )
                    .build()
                
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                
                // Restaurar posição se estiver continuando
                state.content.lastPosition?.let { position ->
                    exoPlayer.seekTo(position)
                }
            }
            else -> {}
        }
    }
    
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false // Usar controles customizados
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    
    // Controles customizados
    PlayerControls(
        player = exoPlayer,
        onBack = { viewModel.saveProgress() }
    )
}

// ui/player/PlayerControls.kt
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerControls(
    player: ExoPlayer,
    onBack: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    
    // Auto-hide dos controles
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }
    
    // Detectar interação do usuário
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { showControls = true }
    ) {
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                // Barra superior
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }
                
                // Controles centrais
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    IconButton(
                        onClick = { player.seekBack() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Replay10,
                            contentDescription = "Voltar 10s",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                player.pause()
                            } else {
                                player.play()
                            }
                            isPlaying = !isPlaying
                        },
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pausar" else "Play",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    
                    IconButton(
                        onClick = { player.seekForward() },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            Icons.Default.Forward10,
                            contentDescription = "Avançar 10s",
                            tint = Color.White,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                // Barra de progresso
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    VideoProgressBar(player = player)
                }
            }
        }
    }
}
```

### 4. Integração com API

Sistema robusto de comunicação com backend, incluindo interceptors para autenticação e tratamento de erros.

```kotlin
// core/di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        networkMonitor: NetworkMonitor
    ): OkHttpClient {
        return OkHttpClient.Builder()
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
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor { chain ->
                if (!networkMonitor.isConnected()) {
                    throw NoNetworkException()
                }
                chain.proceed(chain.request())
            }
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val baseUrl = if (BuildConfig.DEBUG) {
            "http://10.0.2.2:3000/api/" // Emulador Android
        } else {
            BuildConfig.API_BASE_URL
        }
        
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}

// core/api/ApiService.kt
interface ApiService {
    
    @POST("auth/device")
    suspend fun authenticateDevice(
        @Body request: DeviceAuthRequest
    ): Response<AuthResponse>
    
    @POST("auth/code")
    suspend fun authenticateWithCode(
        @Body request: CodeAuthRequest
    ): Response<AuthResponse>
    
    @GET("content/categories")
    suspend fun getCategories(): Response<List<Category>>
    
    @GET("content/{categoryId}")
    suspend fun getCategoryContent(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ContentListResponse>
    
    @GET("content/details/{contentId}")
    suspend fun getContentDetails(
        @Path("contentId") contentId: String
    ): Response<ContentDetails>
    
    @GET("user/subscription")
    suspend fun getSubscriptionStatus(): Response<SubscriptionStatus>
    
    @POST("payment/check")
    suspend fun checkPaymentStatus(
        @Body request: PaymentCheckRequest
    ): Response<PaymentStatus>
}

// core/api/interceptors/AuthInterceptor.kt
class AuthInterceptor @Inject constructor(
    private val securePrefs: SecurePreferences
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = securePrefs.getAuthToken()
        
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(request)
        
        // Renovar token se necessário
        if (response.code == 401) {
            // Implementar lógica de renovação de token
        }
        
        return response
    }
}
```

### 5. Sistema de Cache e Offline

Implementação de cache inteligente com Room e sincronização automática.

```kotlin
// core/storage/database/AppDatabase.kt
@Database(
    entities = [
        ContentEntity::class,
        CategoryEntity::class,
        WatchProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun categoryDao(): CategoryDao
    abstract fun progressDao(): WatchProgressDao
}

// core/storage/repositories/ContentRepository.kt
@Singleton
class ContentRepository @Inject constructor(
    private val apiService: ApiService,
    private val contentDao: ContentDao,
    private val networkMonitor: NetworkMonitor
) {
    fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        
        // Emitir dados do cache primeiro
        val cachedCategories = contentDao.getAllCategories()
        if (cachedCategories.isNotEmpty()) {
            emit(Resource.Success(cachedCategories.toDomainModel()))
        }
        
        // Buscar dados atualizados se online
        if (networkMonitor.isConnected()) {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        // Atualizar cache
                        contentDao.insertCategories(categories.toEntity())
                        emit(Resource.Success(categories))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.message ?: "Unknown error"))
            }
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun getCategoryContent(
        categoryId: String,
        forceRefresh: Boolean = false
    ): Resource<List<Content>> {
        return if (forceRefresh || shouldRefreshContent(categoryId)) {
            fetchFromNetwork(categoryId)
        } else {
            fetchFromCache(categoryId)
        }
    }
    
    private suspend fun shouldRefreshContent(categoryId: String): Boolean {
        val lastUpdate = contentDao.getLastUpdateTime(categoryId)
        return System.currentTimeMillis() - lastUpdate > CACHE_VALIDITY_DURATION
    }
}
```

## 🧪 Testes

### Executar Testes Unitários

```bash
# Todos os testes unitários
./gradlew test

# Testes de um módulo específico
./gradlew :app:test

# Com relatório de cobertura
./gradlew test jacocoTestReport
```

### Executar Testes Instrumentados

```bash
# Necessita de dispositivo/emulador conectado
./gradlew connectedAndroidTest

# Teste específico
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.tvstreaming.app.AuthTest
```

### Exemplo de Teste

```kotlin
// Teste unitário do AuthRepository
@ExperimentalCoroutinesApi
class AuthRepositoryTest {
    
    @get:Rule
    val coroutineRule = MainCoroutineRule()
    
    @Mock
    private lateinit var apiService: ApiService
    
    @Mock
    private lateinit var macManager: MacAddressManager
    
    @Mock
    private lateinit var securePrefs: SecurePreferences
    
    private lateinit var authRepository: AuthRepository
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(apiService, macManager, securePrefs)
    }
    
    @Test
    fun `authenticate device success`() = runTest {
        // Given
        val deviceId = "AA:BB:CC:DD:EE:FF"
        val token = "valid_token"
        val authResponse = AuthResponse(token = token, expiresIn = 3600)
        
        whenever(macManager.getMacAddress()).thenReturn(deviceId)
        whenever(apiService.authenticateDevice(any())).thenReturn(
            Response.success(authResponse)
        )
        
        // When
        val result = authRepository.authenticateDevice()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(token, result.getOrNull()?.token)
        verify(securePrefs).saveAuthToken(token)
    }
}
```

## 📦 Build e Deploy

### Build de Debug

```bash
# Build APK debug
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Build de Release

```bash
# 1. Configurar keystore em local.properties
echo "RELEASE_STORE_FILE=/path/to/keystore.jks" >> local.properties
echo "RELEASE_STORE_PASSWORD=senha" >> local.properties
echo "RELEASE_KEY_ALIAS=alias" >> local.properties
echo "RELEASE_KEY_PASSWORD=senha" >> local.properties

# 2. Build APK release
./gradlew assembleRelease

# 3. Build Bundle (para Google Play)
./gradlew bundleRelease

# Output: app/build/outputs/bundle/release/app-release.aab
```

### Deploy para Google Play

```bash
# Usando fastlane (recomendado)
fastlane android deploy

# Ou manualmente via Play Console
# 1. Acessar https://play.google.com/console
# 2. Upload do .aab
# 3. Preencher informações
# 4. Enviar para revisão
```

## 🎨 White Label Configuration

### Estrutura de Configuração

```kotlin
// whitelabel/config/ClientConfig.kt
data class ClientConfig(
    val clientId: String,
    val appName: String,
    val packageSuffix: String,
    val theme: ThemeConfig,
    val features: FeatureFlags,
    val api: ApiConfig
)

// Exemplo de configuração
object ClientConfigs {
    val clients = mapOf(
        "client_a" to ClientConfig(
            clientId = "client_a",
            appName = "StreamFlix",
            packageSuffix = ".streamflix",
            theme = ThemeConfig(
                primaryColor = "#FF5722",
                secondaryColor = "#FFC107",
                logoRes = R.drawable.logo_streamflix
            ),
            features = FeatureFlags(
                liveTV = true,
                downloads = true,
                profiles = true,
                kids = true
            ),
            api = ApiConfig(
                baseUrl = "https://api.streamflix.com/",
                apiKey = "client_a_key"
            )
        )
    )
}
```

### Build Variants

```kotlin
// app/build.gradle.kts
android {
    productFlavors {
        create("clientA") {
            dimension = "client"
            applicationIdSuffix = ".clienta"
            versionNameSuffix = "-clientA"
            
            buildConfigField("String", "CLIENT_ID", "\"client_a\"")
            resValue("string", "app_name", "StreamFlix")
        }
        
        create("clientB") {
            dimension = "client"
            applicationIdSuffix = ".clientb"
            versionNameSuffix = "-clientB"
            
            buildConfigField("String", "CLIENT_ID", "\"client_b\"")
            resValue("string", "app_name", "CineMax")
        }
    }
}
```

## 🔧 Troubleshooting

### Problemas Comuns e Soluções

#### 1. Erro de Build: "SDK location not found"
```bash
# Criar local.properties
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
```

#### 2. Emulador não conecta ao mock server
```kotlin
// Para emulador, usar IP especial
const val BASE_URL = "http://10.0.2.2:3000/api"

// Para dispositivo físico, usar IP da máquina
const val BASE_URL = "http://192.168.1.100:3000/api"
```

#### 3. Navegação TV não funciona
```kotlin
// Verificar AndroidManifest.xml
<uses-feature
    android:name="android.software.leanback"
    android:required="false" />

<uses-feature
    android:name="android.hardware.touchscreen"
    android:required="false" />
```

#### 4. ExoPlayer crash em dispositivos antigos
```kotlin
// Usar LoadControl conservador
val loadControl = DefaultLoadControl.Builder()
    .setBufferDurationsMs(
        15000,  // Min buffer
        30000,  // Max buffer
        2500,   // Playback buffer
        5000    // Rebuffer
    )
    .build()
```

### Debug Tips

#### 1. Logs detalhados
```kotlin
// Em Application class
if (BuildConfig.DEBUG) {
    Timber.plant(Timber.DebugTree())
}

// Uso
Timber.d("Loading content for category: $categoryId")
```

#### 2. Interceptor para debug de rede
```kotlin
HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

#### 3. Layout Inspector para Compose
- Tools → Layout Inspector
- Selecionar processo do app
- Navegar pela árvore de composables

#### 4. Database Inspector
- View → Tool Windows → App Inspection
- Selecionar Database Inspector
- Visualizar e editar dados do Room

## 📊 Performance e Otimizações

### 1. Otimizações de Memória

```kotlin
// Limpar recursos não utilizados
class MainActivity : ComponentActivity() {
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_UI_HIDDEN -> {
                // Limpar cache de imagens
                Glide.get(this).clearMemory()
            }
        }
    }
}
```

### 2. Otimizações de Rede

```kotlin
// Cache de requisições
val cacheSize = 10 * 1024 * 1024L // 10 MB
val cache = Cache(context.cacheDir, cacheSize)

OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor { chain ->
        var request = chain.request()
        request = if (hasNetwork())
            request.newBuilder()
                .header("Cache-Control", "public, max-age=" + 5)
                .build()
        else
            request.newBuilder()
                .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                .build()
        chain.proceed(request)
    }
```

### 3. Otimizações de UI

```kotlin
// Lazy loading de imagens
@Composable
fun ContentThumbnail(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        modifier = Modifier.fillMaxSize()
    )
}
```

## 🔒 Segurança

### 1. Armazenamento Seguro

```kotlin
// Usar EncryptedSharedPreferences
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### 2. Comunicação Segura

```kotlin
// Certificate Pinning
val certificatePinner = CertificatePinner.Builder()
    .add("api.exemplo.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### 3. Ofuscação de Código

```
# proguard-rules.pro
-keep class com.tvstreaming.app.models.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
```

## 📱 Requisitos de Sistema

### Android Mobile
- **Mínimo**: Android 6.0 (API 23)
- **Recomendado**: Android 10.0+ (API 29+)
- **RAM**: 2GB mínimo, 4GB recomendado
- **Armazenamento**: 100MB para o app + cache

### Android TV
- **Mínimo**: Android TV 7.0 (API 24)
- **Recomendado**: Android TV 9.0+ (API 28+)
- **RAM**: 1GB mínimo, 2GB recomendado
- **Controle remoto**: D-pad compatível

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

### Padrões de Código

- Seguir [Kotlin Style Guide](https://developer.android.com/kotlin/style-guide)
- Usar ktlint para formatação: `./gradlew ktlintFormat`
- Escrever testes para novas features
- Documentar funções públicas

## 📞 Suporte e Contatos

- **Documentação Completa**: [Link para Wiki]
- **Reportar Bugs**: [GitHub Issues]
- **Email**: suporte@tvstreaming.com
- **Discord**: [Link para servidor]

## 📄 Licença

Este projeto está sob licença proprietária. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

**Última atualização**: Janeiro 2025  
**Versão**: 1.0.0  
**Status**: ✅ Em Desenvolvimento Ativo