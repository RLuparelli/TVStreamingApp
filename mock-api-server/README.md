# Mock API Server - TV Streaming App

Este é um servidor de API mock para desenvolvimento do TV Streaming App, fornecendo endpoints simulados para testar tanto o app Android quanto o app Tizen TV.

## Instalação

```bash
cd mock-api-server
npm install
```

## Como executar

```bash
# Modo produção
npm start

# Modo desenvolvimento (com auto-reload)
npm run dev
```

O servidor estará disponível em `http://localhost:3000`

## Endpoints Disponíveis

### Autenticação

#### `POST /api/auth/device`
Autentica dispositivo usando MAC address.

**Request Body:**
```json
{
  "device_id": "aa:bb:cc:dd:ee:ff",
  "device_type": "samsung_tv",
  "platform": "tizen",
  "app_version": "1.0.0"
}
```

**Cenários de Resposta:**
- MAC terminado em 0 ou 1: Novo dispositivo (requer ativação)
- MAC terminado em a, b, c: Assinatura expirada
- MAC terminado em d, e, f: Assinatura Premium ativa
- Outros: Assinatura Basic ativa

#### `POST /api/auth/code`
Ativa dispositivo usando código.

**Request Body:**
```json
{
  "code": "ABC-123",
  "device_id": "aa:bb:cc:dd:ee:ff"
}
```

### Pagamento

#### `POST /api/payment/check`
Verifica status do pagamento.

**Request Body:**
```json
{
  "device_id": "aa:bb:cc:dd:ee:ff"
}
```

### Conteúdo

#### `GET /api/content/categories`
Lista todas as categorias disponíveis.

#### `GET /api/content/:categoryId`
Lista conteúdo de uma categoria específica.

**Query Parameters:**
- `page`: Número da página (default: 1)
- `limit`: Itens por página (default: 20)

#### `GET /api/content/details/:contentId`
Detalhes completos de um conteúdo.

### Usuário

#### `GET /api/user/subscription`
Status da assinatura do usuário.

#### `GET /api/user/devices`
Lista dispositivos cadastrados do usuário.

## Configuração nos Apps

### App Tizen TV

No arquivo `tizen-app/js/api.js`, altere o `baseUrl`:

```javascript
this.baseUrl = 'http://[SEU_IP]:3000/api';
```

### App Android

No arquivo de configuração da API:

```kotlin
// Para emulador Android
const val BASE_URL = "http://10.0.2.2:3000/api"

// Para dispositivo físico
const val BASE_URL = "http://[SEU_IP]:3000/api"
```

## Estrutura de Dados Mock

Os dados mock estão em `/data/mock-data.js` e incluem:

- **Usuários**: expired, premium, basic
- **Categorias**: movies, series, live_tv, anime, documentaries, kids
- **Planos**: basic, premium, family
- **Canais ao vivo**: news, sports, movies

## Testando Diferentes Cenários

### 1. Novo Dispositivo
Use MAC address terminado em 0 ou 1:
- `00:00:00:00:00:00`
- `11:11:11:11:11:11`

### 2. Assinatura Expirada
Use MAC address terminado em a, b ou c:
- `aa:aa:aa:aa:aa:aa`
- `bb:bb:bb:bb:bb:bb`

### 3. Assinatura Premium
Use MAC address terminado em d, e ou f:
- `dd:dd:dd:dd:dd:dd`
- `ee:ee:ee:ee:ee:ee`

### 4. Assinatura Basic
Use qualquer outro MAC address:
- `99:99:99:99:99:99`
- `12:34:56:78:90:ab`

## Simulação de Pagamento

O endpoint `/api/payment/check` tem 30% de chance de retornar pagamento confirmado, simulando o processo real de verificação de pagamento.

## Desenvolvimento

Para adicionar novos endpoints ou modificar comportamentos:

1. Edite `server.js` para adicionar rotas
2. Edite `data/mock-data.js` para adicionar dados
3. Reinicie o servidor

## Troubleshooting

### CORS Issues
O servidor já está configurado com CORS habilitado para aceitar requisições de qualquer origem.

### Conexão recusada
- Verifique se o servidor está rodando
- Verifique o IP da máquina host
- Verifique firewall/antivírus

### Timeout nas requisições
Os endpoints têm delay simulado de 500-2000ms para simular latência real de rede.