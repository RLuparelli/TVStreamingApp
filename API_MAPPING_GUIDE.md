# API Mapping Guide - TV Streaming App

## Visão Geral

Este documento serve como guia de mapeamento entre a implementação atual (usando servidor mock) e a futura integração com a API real do backend. Foi criado para facilitar a migração quando a API definitiva estiver disponível.

**Data de criação**: 20 de Julho de 2025  
**Versão atual do app**: 1.0  
**URL Base Mock**: `http://172.29.19.189:3000/api/` (WSL)  
**URL Base Emulador**: `http://10.0.2.2:3000/api/`

## 1. Estrutura de Endpoints

### 1.1 Autenticação

| Endpoint Mock | Método | Parâmetros | Headers | Resposta | Observações |
|--------------|--------|------------|---------|----------|-------------|
| `/auth/device` | POST | `DeviceAuthRequest` | - | `AuthResponse` | Autenticação por MAC Address |
| `/auth/code` | POST | `CodeAuthRequest` | - | `AuthResponse` | Autenticação por código |
| `/auth/refresh` | POST | `RefreshTokenRequest` | - | `AuthResponse` | Renovação de token |

**DeviceAuthRequest**:
```json
{
  "macAddress": "string",
  "deviceInfo": {
    "key": "value"
  },
  "tenantId": "string?" // opcional
}
```

**CodeAuthRequest**:
```json
{
  "code": "string",
  "deviceInfo": {
    "key": "value"
  }
}
```

**AuthResponse**:
```json
{
  "token": "string",
  "refreshToken": "string",
  "expiresIn": "number",
  "userInfo": "UserInfo"
}
```

### 1.2 Canais de TV

| Endpoint Mock | Método | Parâmetros | Headers | Resposta | Observações |
|--------------|--------|------------|---------|----------|-------------|
| `/channels/categories` | GET | - | Authorization | `ChannelCategoriesResponse` | Lista categorias de canais |
| `/channels` | GET | `category?`, `search?`, `page?`, `limit?` | Authorization | `ChannelListResponse` | Lista canais com filtros |
| `/channels/{channelId}` | GET | `channelId` | Authorization | `ChannelDetails` | Detalhes do canal |

**ChannelListResponse**:
```json
{
  "channels": [Channel],
  "total": "number",
  "page": "number",
  "totalPages": "number",
  "categories": [ChannelCategory]
}
```

**Channel**:
```json
{
  "id": "string",
  "name": "string",
  "number": "number",
  "logo": "string",
  "category": "string",
  "currentProgram": "string",
  "nextProgram": "string",
  "description": "string",
  "streamUrl": "string",
  "isHD": "boolean",
  "isFavorite": "boolean",
  "viewerCount": "number?",
  "quality": ["string"]?,
  "audioTracks": ["string"]?
}
```

### 1.3 Conteúdo (Filmes/Séries)

| Endpoint Mock | Método | Parâmetros | Headers | Resposta | Observações |
|--------------|--------|------------|---------|----------|-------------|
| `/content/categories` | GET | - | - | `List<Category>` | Categorias de conteúdo |
| `/content/{categoryId}` | GET | `categoryId`, `page?` | - | `ContentListResponse` | Conteúdos por categoria |
| `/content/details/{contentId}` | GET | `contentId` | - | `ContentDetails` | Detalhes do conteúdo |
| `/content/search` | GET | `q`, `category?` | Authorization | `List<ContentItem>` | Busca de conteúdo |

### 1.4 Usuário

| Endpoint Mock | Método | Parâmetros | Headers | Resposta | Observações |
|--------------|--------|------------|---------|----------|-------------|
| `/user/profile` | GET | - | Authorization | `UserInfo` | Perfil do usuário |
| `/user/watch-progress` | POST | `WatchProgressRequest` | Authorization | - | Salvar progresso |
| `/user/watch-progress/{contentId}` | GET | `contentId` | Authorization | `WatchProgressResponse` | Obter progresso |

### 1.5 Pagamento

| Endpoint Mock | Método | Parâmetros | Headers | Resposta | Observações |
|--------------|--------|------------|---------|----------|-------------|
| `/payment/generate-qr` | POST | `PaymentQrRequest` | Authorization | `PaymentQrResponse` | Gerar QR Code PIX |
| `/payment/status/{paymentId}` | GET | `paymentId` | Authorization | `PaymentStatusResponse` | Status do pagamento |

## 2. Configurações de Rede

### 2.1 Retrofit Configuration
- **Base URL**: Configurada em `build.gradle.kts` via BuildConfig
- **Timeout**: 30 segundos (connect, read, write)
- **Retry**: Habilitado para falhas de conexão

### 2.2 Interceptors
1. **AuthInterceptor**: Adiciona token automaticamente
2. **CacheInterceptor**: Gerencia cache HTTP
3. **ErrorInterceptor**: (Desabilitado temporariamente)
4. **LoggingInterceptor**: Debug de requisições

### 2.3 Headers Padrão
```
Authorization: Bearer {token}
X-Platform: android
X-Device-Type: mobile/tv
Cache-Control: public, max-age=X
```

## 3. Padrões de Nomenclatura

### 3.1 Mapeamento JSON
A API mock usa **camelCase** para os campos JSON:
- `currentProgram` (não `current_program`)
- `nextProgram` (não `next_program`)
- `streamUrl` (não `stream_url`)
- `isHD` (não `is_hd`)
- `isFavorite` (não `is_favorite`)

### 3.2 Anotações Gson
Todos os modelos usam `@SerializedName` para mapear campos JSON:
```kotlin
@SerializedName("currentProgram")
val currentProgram: String
```

## 4. Fluxo de Autenticação

### 4.1 Fluxo Atual
1. App tenta autenticação por MAC Address
2. Se falhar, solicita código ao usuário
3. Token é armazenado em `SecurePreferences`
4. Token é renovado automaticamente quando expira

### 4.2 Gerenciamento de Token
- **Storage**: `SecurePreferences` (criptografado)
- **Formato**: `Bearer {token}`
- **Expiração**: 3600 segundos (1 hora)
- **Renovação**: Automática via `RefreshTokenRequest`

## 5. Tratamento de Erros

### 5.1 Padrão Resource
```kotlin
sealed class Resource<T> {
    class Success<T>(data: T) : Resource<T>
    class Loading<T>(data: T? = null) : Resource<T>
    class Error<T>(error: Throwable, message: String) : Resource<T>
}
```

### 5.2 SafeApiCall
Wrapper para chamadas de API com tratamento de erros:
- `UnauthorizedException`: 401
- `NetworkException`: Erros de rede
- `IOException`: Timeout/Conexão
- `Exception`: Erros genéricos

## 6. Estrutura de Repositórios

### 6.1 ChannelRepository
- `getChannelCategories()`: Flow com categorias
- `getChannels()`: Flow com lista paginada
- `getChannelDetails()`: Flow com detalhes
- `getFavoriteChannels()`: Mock local
- `toggleFavorite()`: Mock local

### 6.2 AuthRepository
- `authenticateDevice()`: Autenticação por MAC
- `authenticateCode()`: Autenticação por código
- `refreshToken()`: Renovação de token
- `logout()`: Limpar credenciais

## 7. Checklist de Migração

### 7.1 Configurações
- [ ] Atualizar URL base em `build.gradle.kts`
- [ ] Verificar certificados SSL se HTTPS
- [ ] Ajustar timeouts se necessário
- [ ] Configurar proxy se ambiente corporativo

### 7.2 Autenticação
- [ ] Validar formato de token real
- [ ] Ajustar tempo de expiração
- [ ] Implementar refresh token real
- [ ] Verificar headers necessários

### 7.3 Modelos
- [ ] Verificar nomenclatura de campos (camelCase vs snake_case)
- [ ] Adicionar/remover campos conforme API real
- [ ] Ajustar tipos de dados se necessário
- [ ] Validar campos obrigatórios vs opcionais

### 7.4 Endpoints
- [ ] Mapear endpoints reais
- [ ] Verificar métodos HTTP
- [ ] Ajustar parâmetros de query/path
- [ ] Validar estrutura de respostas

### 7.5 Tratamento de Erros
- [ ] Mapear códigos de erro da API real
- [ ] Ajustar mensagens de erro
- [ ] Implementar retry policies específicas
- [ ] Adicionar analytics de erros

### 7.6 Features Específicas
- [ ] Implementar favoritos no backend
- [ ] Integrar sistema de pagamento real
- [ ] Configurar streaming de vídeo real
- [ ] Implementar EPG (guia de programação)

## 8. Testes Necessários

### 8.1 Testes de Integração
- Autenticação completa (device + code)
- Listagem e filtragem de canais
- Reprodução de streams
- Salvamento de progresso
- Pagamentos

### 8.2 Testes de Erro
- Timeout de rede
- Token expirado
- Respostas inválidas
- Sem conexão

### 8.3 Testes de Performance
- Cache de imagens
- Paginação
- Tempo de resposta
- Uso de memória

## 9. Observações Importantes

1. **Servidor Mock**: Atualmente rodando em Node.js com Express
2. **Dados Mock**: Todos os dados são estáticos no arquivo `mock-data.js`
3. **Autenticação Mock**: Aceita qualquer MAC/código
4. **Streams**: URLs de exemplo, não funcionais
5. **Favoritos**: Apenas local, não sincroniza
6. **Pagamento**: Sempre retorna sucesso

## 10. Contatos e Suporte

- **Servidor Mock**: `/mock-api-server/README.md`
- **Logs**: Habilitados via `Timber` + `HttpLoggingInterceptor`
- **Debug**: BuildConfig.DEBUG_MODE

---

**Última atualização**: 20/07/2025