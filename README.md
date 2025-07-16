# 🎬 Streaming White Label - Guia de Desenvolvimento

## 📋 Visão Geral do Projeto

Sistema de streaming multiplataforma (Android/TV) com arquitetura modular white label, autenticação MAC e interface otimizada para controle remoto.

**Cronograma:** 15/07/2025 - 31/12/2025 (5 meses)  
**Status:** Em Desenvolvimento - Fase 1 (Core)

## 🎯 Objetivos Estratégicos

- **Goal Principal:** Lançar App Streaming White Label (31/12/2025)
- **Goal Fase 1:** Core funcional (31/08/2025)
- **Goal Fase 2:** Interface completa (15/10/2025)

## 🏗️ Arquitetura do Sistema

### Estrutura Modular
```
streaming-app/
├── core/                    # Núcleo do aplicativo
│   ├── auth/               # Sistema de autenticação
│   ├── api/                # Comunicação com backend
│   ├── player/             # Player de vídeo
│   └── storage/            # Armazenamento local
├── modules/                 # Módulos customizáveis
│   ├── ui/                 # Interface do usuário
│   ├── theme/              # Temas e estilos
│   └── config/             # Configurações white label
├── platforms/              # Código específico por plataforma
│   ├── android/            # App Android
│   └── tv/                 # App para TV
└── white-label/            # Configurações por cliente
    └── client-configs/     # JSONs de configuração
```

### Stack Tecnológico
- **Linguagem:** Kotlin
- **Arquitetura:** MVVM + Clean Architecture
- **UI:** Jetpack Compose + Leanback (TV)
- **Player:** ExoPlayer
- **Networking:** Retrofit + OkHttp + DNS over TLS
- **Storage:** Room + DataStore + Encrypted SharedPreferences
- **DI:** Hilt
- **Testes:** JUnit + Mockito + Espresso

## 🚀 Fases de Desenvolvimento

### Fase 1: Core (4-6 semanas) - Prioridade ALTA

#### 📋 Task 1.1: Setup Inicial do Projeto
**Objetivo:** Configurar ambiente e estrutura base
**Deadline:** 20/07/2025

**Critérios de Aceitação:**
- [ ] Android Studio configurado (SDK 21-34)
- [ ] Estrutura modular implementada
- [ ] Gradle com dependências principais
- [ ] Git configurado com .gitignore
- [ ] Hilt para DI configurado
- [ ] README inicial documentado

**Comandos Essenciais:**
```bash
# Inicializar projeto
./gradlew clean build

# Configurar Git
git init
git add .
git commit -m "Initial project setup"

# Configurar dependências
./gradlew dependencies
```

**Arquivos Críticos:**
- `build.gradle` (módulos)
- `settings.gradle`
- `gradle.properties`
- `proguard-rules.pro`

#### 🔐 Task 1.2: Sistema de Autenticação MAC
**Objetivo:** Implementar autenticação segura via MAC address
**Deadline:** 25/07/2025

**Critérios de Aceitação:**
- [ ] MAC Address Manager implementado
- [ ] Validação e formatação de MAC
- [ ] Owner Configuration Parser (JSON)
- [ ] Fluxo de primeiro acesso
- [ ] Tela de código/card
- [ ] Envio seguro para API
- [ ] Testes unitários

**Implementação:**
```kotlin
// core/auth/MacAddressManager.kt
class MacAddressManager @Inject constructor() {
    fun getMacAddress(): String
    fun validateMac(mac: String): Boolean
    fun formatMac(mac: String): String
}

// core/auth/OwnerConfigParser.kt
class OwnerConfigParser @Inject constructor() {
    fun parseOwnerConfig(json: String): OwnerConfig
    fun validateConfig(config: OwnerConfig): Boolean
}

// core/auth/AuthRepository.kt
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val macManager: MacAddressManager
) {
    suspend fun authenticateWithMac(ownerId: String): Result<AuthResponse>
    suspend fun authenticateWithCode(code: String): Result<AuthResponse>
}
```

#### 🌐 Task 1.3: Comunicação API com DNS over TLS
**Objetivo:** Configurar comunicação segura com backend
**Deadline:** 01/08/2025

**Critérios de Aceitação:**
- [ ] DNS over TLS configurado
- [ ] Retrofit + OkHttp implementado
- [ ] Endpoints de autenticação
- [ ] Endpoints de conteúdo
- [ ] Certificate pinning
- [ ] Retry policy e timeout
- [ ] Testes de integração

**Configuração:**
```kotlin
// core/api/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideDnsOverTls(): DnsOverTls {
        return DnsOverTls.Builder()
            .setHostname("dns.example.com")
            .setPort(853)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(dnsOverTls: DnsOverTls): OkHttpClient {
        return OkHttpClient.Builder()
            .dns(dnsOverTls)
            .certificatePinner(getCertificatePinner())
            .addInterceptor(loggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}

// core/api/ApiService.kt
interface ApiService {
    @POST("/auth/device")
    suspend fun authenticateDevice(@Body request: DeviceAuthRequest): Response<AuthResponse>
    
    @POST("/auth/code")
    suspend fun authenticateCode(@Body request: CodeAuthRequest): Response<AuthResponse>
    
    @GET("/content/categories")
    suspend fun getCategories(): Response<List<Category>>
    
    @GET("/content/{categoryId}")
    suspend fun getContent(@Path("categoryId") categoryId: String): Response<List<Content>>
}
```

#### 🗄️ Task 1.4: Storage Seguro
**Objetivo:** Implementar armazenamento local seguro
**Deadline:** 05/08/2025

**Critérios de Aceitação:**
- [ ] Encrypted SharedPreferences para credenciais
- [ ] Room database para cache
- [ ] DataStore para preferências
- [ ] Cache de thumbnails
- [ ] Storage de metadados
- [ ] Limpeza automática
- [ ] Testes de persistência

**Implementação:**
```kotlin
// core/storage/entities/
@Entity(tableName = "content")
data class ContentEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val categoryId: String,
    val duration: Long,
    val createdAt: Long
)

// core/storage/dao/
@Dao
interface ContentDao {
    @Query("SELECT * FROM content WHERE categoryId = :categoryId")
    suspend fun getContentByCategory(categoryId: String): List<ContentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: List<ContentEntity>)
    
    @Query("DELETE FROM content WHERE createdAt < :timestamp")
    suspend fun deleteOldContent(timestamp: Long)
}

// core/storage/preferences/
@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val encryptedPrefs = EncryptedSharedPreferences.create(
        "secure_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveCredentials(username: String, password: String)
    fun getCredentials(): Pair<String, String>?
    fun clearCredentials()
}
```

### Fase 2: Interface (4-6 semanas) - Prioridade ALTA

#### 📱 Task 2.1: Layout Responsivo TV/Mobile
**Objetivo:** Interface adaptativa para diferentes dispositivos
**Deadline:** 05/09/2025

**Critérios de Aceitação:**
- [ ] Jetpack Compose implementado
- [ ] Layouts específicos para TV (Leanback)
- [ ] Layouts responsivos para mobile
- [ ] Navegação adaptativa
- [ ] Componentes reutilizáveis
- [ ] Material Design 3
- [ ] Testes de UI em diferentes telas

**Estrutura UI:**
```kotlin
// modules/ui/components/
@Composable
fun ResponsiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (isTV: Boolean) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isTV = configuration.uiMode and Configuration.UI_MODE_TYPE_MASK == Configuration.UI_MODE_TYPE_TELEVISION
    
    content(isTV)
}

// modules/ui/screens/
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToCategory: (String) -> Unit
) {
    ResponsiveLayout { isTV ->
        if (isTV) {
            TVHomeLayout(viewModel, navigateToCategory)
        } else {
            MobileHomeLayout(viewModel, navigateToCategory)
        }
    }
}
```

#### 🎮 Task 2.2: Navegação por Controle Remoto
**Objetivo:** Otimizar navegação para TV
**Deadline:** 15/09/2025

**Critérios de Aceitação:**
- [ ] Mapeamento de teclas do controle
- [ ] Focus management para D-pad
- [ ] Navegação entre telas
- [ ] Atalhos para botões especiais
- [ ] Feedback visual para seleção
- [ ] Navegação por voz (opcional)
- [ ] Testes com diferentes controles

**Implementação:**
```kotlin
// modules/ui/navigation/
@Composable
fun TVNavigationHandler(
    onBackPressed: () -> Unit,
    onHomePressed: () -> Unit
) {
    DisposableEffect(Unit) {
        val keyListener = View.OnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        onBackPressed()
                        true
                    }
                    KeyEvent.KEYCODE_HOME -> {
                        onHomePressed()
                        true
                    }
                    else -> false
                }
            } else false
        }
        
        onDispose { }
    }
}

// modules/ui/focus/
@Composable
fun FocusableCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier
            .focusable()
            .onFocusChanged { isFocused = it.isFocused }
            .clickable { onClick() },
        border = if (isFocused) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        content()
    }
}
```

#### 🎬 Task 2.3: Desenvolver Telas Principais
**Objetivo:** Implementar todas as telas do app
**Deadline:** 25/09/2025

**Critérios de Aceitação:**
- [ ] Tela de login/autenticação
- [ ] Tela Home com menu principal
- [ ] Telas de categorias (TV, Filmes, Séries, Anime)
- [ ] Tela de listagem de conteúdo
- [ ] Tela de detalhes do conteúdo
- [ ] Tela de configurações
- [ ] Tela de renovação/pagamento
- [ ] Transições entre telas

**Estrutura de Telas:**
```kotlin
// modules/ui/screens/auth/
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is AuthUiState.Initial -> InitialAuthContent(viewModel)
            is AuthUiState.EnterCode -> CodeEntryContent(viewModel)
            is AuthUiState.Loading -> LoadingContent()
            is AuthUiState.Success -> LaunchedEffect(Unit) { onAuthSuccess() }
            is AuthUiState.Error -> ErrorContent(uiState.message, viewModel)
        }
    }
}

// modules/ui/screens/home/
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToCategory: (String) -> Unit
) {
    val categories by viewModel.categories.collectAsState()
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(categories) { category ->
            CategoryCard(
                category = category,
                onClick = { navigateToCategory(category.id) }
            )
        }
    }
}
```

#### ▶️ Task 2.4: Implementar Player de Vídeo
**Objetivo:** Player completo com ExoPlayer
**Deadline:** 10/10/2025

**Critérios de Aceitação:**
- [ ] ExoPlayer integrado
- [ ] Streaming adaptativo (HLS/DASH)
- [ ] Controles customizados
- [ ] Múltiplas qualidades
- [ ] Suporte a legendas
- [ ] Áudio multilíngue
- [ ] Continue assistindo
- [ ] Chromecast support
- [ ] Picture-in-Picture (mobile)
- [ ] Testes de reprodução

**Implementação:**
```kotlin
// core/player/
@Composable
fun VideoPlayerScreen(
    videoUrl: String,
    title: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                setMediaItem(MediaItem.fromUri(videoUrl))
                prepare()
            }
    }
    
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    controllerShowTimeoutMs = 3000
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Custom controls overlay
        PlayerControlsOverlay(
            player = exoPlayer,
            title = title,
            onBackPressed = onBackPressed
        )
    }
}

// core/player/PlayerRepository.kt
@Singleton
class PlayerRepository @Inject constructor(
    private val preferences: PlayerPreferences
) {
    fun saveWatchProgress(contentId: String, position: Long) {
        preferences.saveProgress(contentId, position)
    }
    
    fun getWatchProgress(contentId: String): Long {
        return preferences.getProgress(contentId)
    }
    
    fun markAsWatched(contentId: String) {
        preferences.markAsWatched(contentId)
    }
}
```

### Fase 3: Features (3-4 semanas) - Prioridade MÉDIA

#### 💳 Task 3.1: Sistema de Renovação
**Objetivo:** Implementar renovação de assinatura
**Deadline:** 20/10/2025

**Critérios de Aceitação:**
- [ ] Verificação de status de acesso
- [ ] Geração de QR Code para TV
- [ ] Redirecionamento para browser (mobile)
- [ ] Timer de verificação automática
- [ ] Tela de aguardo de pagamento
- [ ] Deep link de retorno
- [ ] Opção de envio por SMS
- [ ] Webhook de confirmação
- [ ] Testes de fluxo completo

#### 🔔 Task 3.2: Sistema de Notificações
**Objetivo:** Notificações push com Firebase
**Deadline:** 30/10/2025

**Critérios de Aceitação:**
- [ ] Firebase Cloud Messaging configurado
- [ ] Notificações de vencimento (3 dias antes)
- [ ] Notificações de acesso expirado
- [ ] Notificações de novo conteúdo
- [ ] Notificações de manutenção
- [ ] Notificações locais
- [ ] Deep links nas notificações
- [ ] Preferências de notificação
- [ ] Testes em dispositivos reais

#### 🔍 Task 3.3: Sistema de Busca e Filtros
**Objetivo:** Busca e filtros de conteúdo
**Deadline:** 10/11/2025

**Critérios de Aceitação:**
- [ ] Busca por título
- [ ] Filtros por categoria
- [ ] Filtros por gênero
- [ ] Filtros por ano
- [ ] Filtros por idioma
- [ ] Ordenação (relevância, data, alfabética)
- [ ] Histórico de buscas
- [ ] Sugestões de busca
- [ ] Busca com controle remoto
- [ ] Testes de performance

### Fase 4: White Label (2-3 semanas) - Prioridade MÉDIA

#### 🎨 Task 4.1: Sistema de Temas White Label
**Objetivo:** Customização visual completa
**Deadline:** 20/11/2025

**Critérios de Aceitação:**
- [ ] Sistema de temas dinâmicos
- [ ] Customização de cores
- [ ] Logos personalizados
- [ ] Fontes customizáveis
- [ ] Ícones personalizados
- [ ] Animações customizáveis
- [ ] Preview de temas
- [ ] Documentação de customização
- [ ] Testes com diferentes temas

#### ⚙️ Task 4.2: Configuração Modular
**Objetivo:** Sistema de configuração por cliente
**Deadline:** 01/12/2025

**Critérios de Aceitação:**
- [ ] Parser para JSON de configuração
- [ ] Sistema de feature flags
- [ ] Build variants por cliente
- [ ] Configuração de APIs
- [ ] Categorias customizáveis
- [ ] Configuração de pagamentos
- [ ] Configuração de idiomas
- [ ] Validação de configurações
- [ ] Documentação do formato

### Fase 5: Testes e Deploy (2-3 semanas) - Prioridade ALTA

#### 🧪 Task 5.1: Testes Automatizados
**Objetivo:** Suíte completa de testes
**Deadline:** 10/12/2025

**Critérios de Aceitação:**
- [ ] Testes unitários para core modules
- [ ] Testes de integração para API
- [ ] Testes de UI com Espresso
- [ ] Testes de performance
- [ ] Testes de segurança
- [ ] Testes em diferentes dispositivos
- [ ] Testes de regressão
- [ ] Coverage reports
- [ ] CI/CD para testes automáticos

#### 🚀 Task 5.2: Pipeline CI/CD
**Objetivo:** Deploy automatizado
**Deadline:** 20/12/2025

**Critérios de Aceitação:**
- [ ] GitHub Actions/Jenkins configurado
- [ ] Build automatizado
- [ ] Testes automáticos no pipeline
- [ ] Deploy para staging
- [ ] Deploy para produção
- [ ] Notificações de build
- [ ] Rollback automático
- [ ] Monitoramento de deploy
- [ ] Documentação do pipeline

#### 📚 Task 5.3: Documentação Final
**Objetivo:** Documentação completa
**Deadline:** 30/12/2025

**Critérios de Aceitação:**
- [ ] README principal atualizado
- [ ] Documentação de API
- [ ] Guia de customização white label
- [ ] Guia de instalação
- [ ] Arquitetura do sistema
- [ ] Guia de troubleshooting
- [ ] Documentação de deploy
- [ ] Guia para novos desenvolvedores
- [ ] Processo de white label

## 🔧 Configuração de Desenvolvimento

### Pré-requisitos
- Android Studio Arctic Fox+
- JDK 11+
- SDK Android 21-34
- Gradle 8.0+
- Git

### Setup Rápido
```bash
# Clonar repositório
git clone https://github.com/seu-repo/streaming-app
cd streaming-app

# Configurar ambiente
./gradlew clean build

# Executar testes
./gradlew test

# Executar app
./gradlew installDebug
```

### Estrutura de Branches
- `main` - Código de produção
- `develop` - Desenvolvimento ativo
- `feature/*` - Features específicas
- `hotfix/*` - Correções urgentes
- `release/*` - Preparação para release

## 📝 Guia para Claude Code

### Comandos Prioritários por Fase

#### Fase 1 - Core
```bash
# Task 1.1 - Setup
claude-code create-project --template=android-kotlin --architecture=mvvm
claude-code setup-modules --core=auth,api,player,storage
claude-code configure-di --framework=hilt

# Task 1.2 - Auth
claude-code implement-auth --mac-based --with-encryption
claude-code create-owner-parser --json-config
claude-code setup-auth-repository --retrofit

# Task 1.3 - API
claude-code configure-networking --dns-over-tls --certificate-pinning
claude-code implement-api-service --endpoints=auth,content
claude-code setup-error-handling --retry-policy

# Task 1.4 - Storage
claude-code setup-room-database --entities=content,user,preferences
claude-code implement-encrypted-prefs --credentials-only
claude-code configure-datastore --user-preferences
```

#### Fase 2 - Interface
```bash
# Task 2.1 - Layout
claude-code setup-compose --with-leanback
claude-code create-responsive-layouts --tv-mobile
claude-code implement-material3 --theme-system

# Task 2.2 - Navigation
claude-code setup-tv-navigation --dpad-focus
claude-code implement-key-handling --remote-control
claude-code create-focus-system --visual-feedback

# Task 2.3 - Screens
claude-code generate-screens --auth,home,categories,details
claude-code implement-viewmodels --mvvm-pattern
claude-code setup-navigation-component --compose

# Task 2.4 - Player
claude-code integrate-exoplayer --hls-dash-support
claude-code implement-player-controls --custom-ui
claude-code setup-chromecast --cast-sdk
```

### Patterns de Desenvolvimento

#### Multi-task Execution
```bash
# Executar tarefas paralelas
claude-code parallel-execute \
  --task1="implement-auth-mac" \
  --task2="setup-room-database" \
  --task3="configure-networking"

# Monitorar dependências
claude-code monitor-dependencies \
  --watch="core/auth" \
  --notify-on-completion
```

#### Code Quality
```bash
# Executar análise contínua
claude-code analyze --detekt --ktlint --sonarqube
claude-code fix-style --auto-format
claude-code generate-tests --unit-integration
```

### Configuração Multi-Node

#### Node 1: Core Development
- Responsável por: Auth, API, Storage
- Comando: `claude-code focus-node --area=core`

#### Node 2: UI Development
- Responsável por: Interface, Navigation, Screens
- Comando: `claude-code focus-node --area=ui`

#### Node 3: Features & Testing
- Responsável por: Player, White Label, Tests
- Comando: `claude-code focus-node --area=features`

### Monitoramento e Métricas

#### Tracking de Progresso
```bash
# Status geral
claude-code status --phases --tasks --blockers

# Métricas de código
claude-code metrics --coverage --complexity --performance

# Relatórios automáticos
claude-code generate-report --weekly --stakeholders
```

## 🎯 Marcos Críticos

- **31/08/2025:** Core completo (Auth + API + Storage)
- **15/10/2025:** Interface funcional (TV + Mobile)
- **15/11/2025:** Features principais (Player + Renovação)
- **31/12/2025:** Produto final (White Label + Deploy)

## 🚨 Pontos de Atenção

1. **Segurança:** Priorizar criptografia e validação
2. **Performance:** Otimizar para TV (hardware limitado)
3. **Compatibilidade:** Testar em diversos dispositivos
4. **Escalabilidade:** Preparar para múltiplos clientes
5. **Qualidade:** Manter cobertura de testes > 80%

## 📞 Contatos e Suporte

- **Project Lead:** Rodrigo Luparelli
- **Asana Project:** [Link para projeto]
- **Repository:** [Link para repositório]
- **Documentation:** [Link para documentação]

---

**Última atualização:** 15/07/2025  
**Versão:** 1.0.0  
**Status:** ✅ Pronto para desenvolvimento