# 🔐 Backend Authentication System - TV Streaming White Label

## 📋 Visão Geral

Sistema de autenticação backend para aplicativo de streaming white label baseado em MAC address do dispositivo, com suporte a multi-tenant e gerenciamento de assinaturas.

## 🏗️ Arquitetura do Sistema

### Stack Tecnológico Sugerida
```
Backend: Node.js + Express / Python + FastAPI / Java + Spring Boot
Database: PostgreSQL / MySQL
Cache: Redis
Auth: JWT + Device Fingerprinting
Security: Rate Limiting + Input Validation + SSL/TLS
```

### Componentes Principais
```
backend/
├── src/
│   ├── controllers/        # Controladores da API
│   ├── services/          # Lógica de negócio
│   ├── models/            # Models do banco de dados
│   ├── middleware/        # Middlewares (auth, rate limiting)
│   ├── utils/             # Utilitários
│   └── config/            # Configurações
├── migrations/            # Migrations do banco
├── tests/                 # Testes
└── docs/                  # Documentação
```

## 🗄️ Estrutura do Banco de Dados

### 1. Tabela `tenants` (Multi-tenant)
```sql
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    domain VARCHAR(100),
    server_url VARCHAR(255) NOT NULL,
    server_port INTEGER DEFAULT 80,
    https_port INTEGER,
    protocol VARCHAR(10) DEFAULT 'http',
    
    -- Branding
    brand_name VARCHAR(100) NOT NULL,
    logo_url VARCHAR(255),
    primary_color VARCHAR(7) DEFAULT '#1976D2',
    secondary_color VARCHAR(7) DEFAULT '#424242',
    
    -- Features
    features JSONB DEFAULT '{
        "enablePaymentQr": true,
        "enableSmsPayment": false,
        "enableChromecast": true,
        "enableDownloads": false,
        "enableProfiles": false,
        "maxDevices": 1,
        "trialDays": 1
    }',
    
    -- Control
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 2. Tabela `devices` (Dispositivos registrados)
```sql
CREATE TABLE devices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id),
    mac_address VARCHAR(17) NOT NULL, -- xx:xx:xx:xx:xx:xx
    device_id VARCHAR(255) NOT NULL,
    
    -- Device Info
    device_info JSONB DEFAULT '{}', -- manufacturer, model, os, etc.
    device_type VARCHAR(20) DEFAULT 'mobile', -- mobile, tv, tablet
    
    -- Authentication
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    
    -- Status
    status VARCHAR(20) DEFAULT 'active', -- active, expired, suspended
    subscription_type VARCHAR(20) DEFAULT 'trial', -- trial, premium, basic
    
    -- Dates
    first_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    
    -- Control
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(tenant_id, mac_address)
);
```

### 3. Tabela `device_sessions` (Sessões ativas)
```sql
CREATE TABLE device_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID REFERENCES devices(id),
    
    -- Session Data
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    
    -- Device Info
    ip_address INET,
    user_agent TEXT,
    
    -- Control
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. Tabela `access_codes` (Códigos de acesso temporários)
```sql
CREATE TABLE access_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id),
    
    -- Code Data
    code VARCHAR(8) NOT NULL,
    mac_address VARCHAR(17),
    device_info JSONB DEFAULT '{}',
    
    -- Control
    is_used BOOLEAN DEFAULT false,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(tenant_id, code)
);
```

### 5. Tabela `payments` (Controle de pagamentos)
```sql
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    device_id UUID REFERENCES devices(id),
    
    -- Payment Data
    payment_id VARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'BRL',
    plan_id VARCHAR(50),
    
    -- Payment Method
    payment_method VARCHAR(20), -- pix, credit_card, bank_transfer
    qr_code TEXT,
    
    -- Status
    status VARCHAR(20) DEFAULT 'pending', -- pending, completed, failed, expired
    
    -- Dates
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL
);
```

## 🚀 Endpoints da API

### 1. Autenticação

#### POST `/api/auth/device`
Autenticação por MAC address
```json
{
    "macAddress": "aa:bb:cc:dd:ee:ff",
    "deviceInfo": {
        "manufacturer": "Samsung",
        "model": "Galaxy S21",
        "os": "Android 12",
        "appVersion": "1.0.0"
    },
    "tenantId": "fasted" // opcional
}
```

**Response:**
```json
{
    "success": true,
    "data": {
        "userInfo": {
            "username": "user_12345678",
            "password": "pass_123456",
            "auth": 1,
            "status": "Active",
            "expDate": "1698764400",
            "isTrial": "1",
            "activeCons": 0,
            "createdAt": "1698677999",
            "maxConnections": "1",
            "allowedOutputFormats": ["m3u8", "ts", "rtmp"]
        },
        "serverInfo": {
            "xui": true,
            "version": "1.5.5",
            "revision": 2,
            "url": "pfsv.io",
            "port": "80",
            "httpsPort": null,
            "serverProtocol": "http",
            "rtmpPort": "8880",
            "timestampNow": 1698677999,
            "timeNow": "2023-10-30 15:33:19",
            "timezone": "UTC",
            "tenantId": "fasted"
        },
        "tokens": {
            "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
            "expiresIn": 3600
        }
    }
}
```

#### POST `/api/auth/code`
Autenticação por código de acesso
```json
{
    "code": "ABC123XY",
    "deviceInfo": {
        "manufacturer": "Samsung",
        "model": "Galaxy S21",
        "os": "Android 12",
        "appVersion": "1.0.0"
    }
}
```

#### POST `/api/auth/refresh`
Renovar token de acesso
```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Gerenciamento de Dispositivos

#### GET `/api/devices/status`
Verificar status do dispositivo
```
Headers: Authorization: Bearer <token>
```

#### PUT `/api/devices/heartbeat`
Atualizar última atividade
```json
{
    "timestamp": 1698677999,
    "activeConnections": 1
}
```

### 3. Pagamentos

#### POST `/api/payment/generate-qr`
Gerar QR code para pagamento
```json
{
    "amount": 29.90,
    "currency": "BRL",
    "planId": "premium_monthly",
    "deviceId": "device_uuid"
}
```

#### GET `/api/payment/status/{paymentId}`
Verificar status do pagamento
```
Headers: Authorization: Bearer <token>
```

### 4. Administração (Tenant)

#### GET `/api/admin/tenants/{tenantId}/config`
Obter configuração do tenant
```json
{
    "tenantId": "fasted",
    "serverUrl": "pfsv.io",
    "serverPort": "80",
    "protocol": "http",
    "brandName": "R.Luparelli",
    "logoUrl": "https://example.com/logo.png",
    "primaryColor": "#1976D2",
    "secondaryColor": "#424242",
    "features": {
        "enablePaymentQr": true,
        "enableSmsPayment": false,
        "enableChromecast": true,
        "maxDevices": 1,
        "trialDays": 1
    }
}
```

#### PUT `/api/admin/tenants/{tenantId}/config`
Atualizar configuração do tenant

#### GET `/api/admin/tenants/{tenantId}/devices`
Listar dispositivos do tenant

#### POST `/api/admin/access-codes`
Gerar código de acesso
```json
{
    "tenantId": "fasted",
    "expiresInHours": 24,
    "maxUses": 1
}
```

## 🔧 Implementação Passo a Passo

### **Passo 1: Setup do Projeto**

#### Node.js + Express
```bash
# Criar projeto
mkdir tv-streaming-backend
cd tv-streaming-backend
npm init -y

# Instalar dependências
npm install express bcryptjs jsonwebtoken
npm install pg redis cors helmet rate-limiter-flexible
npm install joi uuid crypto-js moment
npm install --save-dev nodemon jest supertest

# Estrutura inicial
mkdir -p src/{controllers,services,models,middleware,utils,config}
mkdir -p migrations tests docs
```

#### Python + FastAPI
```bash
# Criar projeto
mkdir tv-streaming-backend
cd tv-streaming-backend
python -m venv venv
source venv/bin/activate  # Linux/Mac
# venv\Scripts\activate  # Windows

# Instalar dependências
pip install fastapi uvicorn sqlalchemy psycopg2-binary
pip install redis pydantic[email] python-jose[cryptography]
pip install bcrypt python-multipart pytest
```

### **Passo 2: Configuração do Banco de Dados**

#### docker-compose.yml
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: tv_streaming
      POSTGRES_USER: streaming_user
      POSTGRES_PASSWORD: streaming_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

volumes:
  postgres_data:
  redis_data:
```

#### Migration inicial (SQL)
```sql
-- migrations/001_initial_schema.sql
-- Executar as tabelas definidas acima
```

### **Passo 3: Implementação dos Services**

#### DeviceService (Node.js)
```javascript
// src/services/DeviceService.js
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');

class DeviceService {
    constructor(dbClient, redisClient) {
        this.db = dbClient;
        this.redis = redisClient;
    }

    async authenticateDevice(macAddress, deviceInfo, tenantId = 'default') {
        // 1. Validar MAC address
        if (!this.isValidMacAddress(macAddress)) {
            throw new Error('Invalid MAC address format');
        }

        // 2. Buscar tenant
        const tenant = await this.getTenant(tenantId);
        if (!tenant) {
            throw new Error('Tenant not found');
        }

        // 3. Buscar ou criar dispositivo
        let device = await this.findDeviceByMac(macAddress, tenant.id);
        
        if (!device) {
            device = await this.createDevice(macAddress, deviceInfo, tenant);
        } else {
            device = await this.updateDeviceAccess(device.id, deviceInfo);
        }

        // 4. Verificar expiração
        if (this.isDeviceExpired(device)) {
            throw new Error('Device subscription expired');
        }

        // 5. Gerar tokens
        const tokens = await this.generateTokens(device);

        // 6. Salvar sessão
        await this.createSession(device.id, tokens, deviceInfo);

        return {
            userInfo: this.formatUserInfo(device),
            serverInfo: this.formatServerInfo(tenant),
            tokens
        };
    }

    async createDevice(macAddress, deviceInfo, tenant) {
        const username = this.generateUsername(macAddress);
        const password = this.generatePassword(macAddress);
        const passwordHash = await bcrypt.hash(password, 10);
        
        const expiresAt = new Date();
        expiresAt.setDate(expiresAt.getDate() + tenant.features.trialDays);

        const device = await this.db.query(`
            INSERT INTO devices (
                tenant_id, mac_address, device_id, device_info, 
                username, password_hash, expires_at
            ) VALUES ($1, $2, $3, $4, $5, $6, $7)
            RETURNING *
        `, [
            tenant.id, macAddress, this.generateDeviceId(macAddress),
            JSON.stringify(deviceInfo), username, passwordHash, expiresAt
        ]);

        return device.rows[0];
    }

    async generateTokens(device) {
        const payload = {
            deviceId: device.id,
            tenantId: device.tenant_id,
            username: device.username
        };

        const accessToken = jwt.sign(payload, process.env.JWT_SECRET, {
            expiresIn: '1h'
        });

        const refreshToken = jwt.sign(payload, process.env.JWT_REFRESH_SECRET, {
            expiresIn: '7d'
        });

        return { accessToken, refreshToken, expiresIn: 3600 };
    }

    generateUsername(macAddress) {
        const cleanMac = macAddress.replace(/[:-]/g, '');
        return `user_${cleanMac.slice(-8)}`;
    }

    generatePassword(macAddress) {
        const cleanMac = macAddress.replace(/[:-]/g, '');
        return `pass_${cleanMac.slice(-6)}`;
    }

    generateDeviceId(macAddress) {
        return crypto.createHash('sha256').update(macAddress).digest('hex');
    }

    isValidMacAddress(mac) {
        const macRegex = /^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/;
        return macRegex.test(mac);
    }

    isDeviceExpired(device) {
        return new Date() > new Date(device.expires_at);
    }

    formatUserInfo(device) {
        return {
            username: device.username,
            password: device.password, // Deve ser obtido de forma segura
            message: device.mac_address,
            auth: device.status === 'active' ? 1 : 0,
            status: device.status,
            expDate: Math.floor(new Date(device.expires_at).getTime() / 1000).toString(),
            isTrial: device.subscription_type === 'trial' ? '1' : '0',
            activeCons: 0,
            createdAt: Math.floor(new Date(device.created_at).getTime() / 1000).toString(),
            maxConnections: '1',
            allowedOutputFormats: ['m3u8', 'ts', 'rtmp']
        };
    }

    formatServerInfo(tenant) {
        return {
            xui: true,
            version: '1.5.5',
            revision: 2,
            url: tenant.server_url,
            port: tenant.server_port.toString(),
            httpsPort: tenant.https_port?.toString(),
            serverProtocol: tenant.protocol,
            rtmpPort: '8880',
            timestampNow: Math.floor(Date.now() / 1000),
            timeNow: new Date().toISOString().slice(0, 19).replace('T', ' '),
            timezone: 'UTC',
            tenantId: tenant.tenant_id
        };
    }
}

module.exports = DeviceService;
```

### **Passo 4: Implementação dos Controllers**

#### AuthController (Node.js)
```javascript
// src/controllers/AuthController.js
const DeviceService = require('../services/DeviceService');
const { validateDeviceAuth, validateCodeAuth } = require('../utils/validators');

class AuthController {
    constructor(deviceService) {
        this.deviceService = deviceService;
    }

    async deviceAuth(req, res) {
        try {
            const { error } = validateDeviceAuth(req.body);
            if (error) {
                return res.status(400).json({
                    success: false,
                    message: error.details[0].message
                });
            }

            const { macAddress, deviceInfo, tenantId } = req.body;
            
            const result = await this.deviceService.authenticateDevice(
                macAddress, deviceInfo, tenantId
            );

            res.json({
                success: true,
                data: result
            });
        } catch (error) {
            res.status(401).json({
                success: false,
                message: error.message
            });
        }
    }

    async codeAuth(req, res) {
        try {
            const { error } = validateCodeAuth(req.body);
            if (error) {
                return res.status(400).json({
                    success: false,
                    message: error.details[0].message
                });
            }

            const { code, deviceInfo } = req.body;
            
            const result = await this.deviceService.authenticateWithCode(
                code, deviceInfo
            );

            res.json({
                success: true,
                data: result
            });
        } catch (error) {
            res.status(401).json({
                success: false,
                message: error.message
            });
        }
    }

    async refreshToken(req, res) {
        try {
            const { refreshToken } = req.body;
            
            const result = await this.deviceService.refreshToken(refreshToken);

            res.json({
                success: true,
                data: result
            });
        } catch (error) {
            res.status(401).json({
                success: false,
                message: error.message
            });
        }
    }
}

module.exports = AuthController;
```

### **Passo 5: Middleware de Autenticação**

```javascript
// src/middleware/auth.js
const jwt = require('jsonwebtoken');

const authenticate = async (req, res, next) => {
    try {
        const token = req.header('Authorization')?.replace('Bearer ', '');
        
        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Access token required'
            });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        next();
    } catch (error) {
        res.status(401).json({
            success: false,
            message: 'Invalid token'
        });
    }
};

module.exports = { authenticate };
```

### **Passo 6: Rate Limiting**

```javascript
// src/middleware/rateLimiter.js
const { RateLimiterRedis } = require('rate-limiter-flexible');

const rateLimiter = new RateLimiterRedis({
    storeClient: redisClient,
    keyPrefix: 'auth_fail',
    points: 5, // Número de tentativas
    duration: 900, // Por 15 minutos
    blockDuration: 900, // Bloquear por 15 minutos
});

const rateLimitMiddleware = async (req, res, next) => {
    try {
        await rateLimiter.consume(req.ip);
        next();
    } catch (rateLimiterRes) {
        res.status(429).json({
            success: false,
            message: 'Too many requests',
            retryAfter: rateLimiterRes.msBeforeNext
        });
    }
};

module.exports = { rateLimitMiddleware };
```

### **Passo 7: Configuração do Servidor**

```javascript
// src/app.js
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const AuthController = require('./controllers/AuthController');
const DeviceService = require('./services/DeviceService');
const { authenticate } = require('./middleware/auth');
const { rateLimitMiddleware } = require('./middleware/rateLimiter');

const app = express();

// Middleware de segurança
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(rateLimitMiddleware);

// Inicializar services
const deviceService = new DeviceService(dbClient, redisClient);
const authController = new AuthController(deviceService);

// Rotas
app.post('/api/auth/device', authController.deviceAuth.bind(authController));
app.post('/api/auth/code', authController.codeAuth.bind(authController));
app.post('/api/auth/refresh', authController.refreshToken.bind(authController));

// Rotas protegidas
app.get('/api/devices/status', authenticate, (req, res) => {
    // Implementar status do dispositivo
});

app.put('/api/devices/heartbeat', authenticate, (req, res) => {
    // Implementar heartbeat
});

// Error handling
app.use((error, req, res, next) => {
    res.status(500).json({
        success: false,
        message: 'Internal server error'
    });
});

module.exports = app;
```

### **Passo 8: Variáveis de Ambiente**

```env
# .env
NODE_ENV=development
PORT=3000

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tv_streaming
DB_USER=streaming_user
DB_PASSWORD=streaming_pass

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=your-super-secret-jwt-key
JWT_REFRESH_SECRET=your-super-secret-refresh-key

# Security
BCRYPT_ROUNDS=12
RATE_LIMIT_WINDOW=900000
RATE_LIMIT_MAX=5

# Default tenant
DEFAULT_TENANT_ID=default
```

### **Passo 9: Testes**

```javascript
// tests/auth.test.js
const request = require('supertest');
const app = require('../src/app');

describe('Authentication', () => {
    describe('POST /api/auth/device', () => {
        it('should authenticate device with valid MAC address', async () => {
            const response = await request(app)
                .post('/api/auth/device')
                .send({
                    macAddress: 'aa:bb:cc:dd:ee:ff',
                    deviceInfo: {
                        manufacturer: 'Samsung',
                        model: 'Galaxy S21',
                        os: 'Android 12'
                    },
                    tenantId: 'test'
                });

            expect(response.status).toBe(200);
            expect(response.body.success).toBe(true);
            expect(response.body.data).toHaveProperty('userInfo');
            expect(response.body.data).toHaveProperty('serverInfo');
            expect(response.body.data).toHaveProperty('tokens');
        });

        it('should reject invalid MAC address', async () => {
            const response = await request(app)
                .post('/api/auth/device')
                .send({
                    macAddress: 'invalid-mac',
                    deviceInfo: {},
                    tenantId: 'test'
                });

            expect(response.status).toBe(400);
            expect(response.body.success).toBe(false);
        });
    });
});
```

### **Passo 10: Deploy**

#### Docker
```dockerfile
# Dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

EXPOSE 3000

CMD ["npm", "start"]
```

#### docker-compose.prod.yml
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "3000:3000"
    environment:
      - NODE_ENV=production
    depends_on:
      - postgres
      - redis
    restart: unless-stopped

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: tv_streaming
      POSTGRES_USER: streaming_user
      POSTGRES_PASSWORD: streaming_pass
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    volumes:
      - redis_data:/data
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:
```

## 🔒 Segurança

### 1. Validação de Entrada
```javascript
const Joi = require('joi');

const validateDeviceAuth = (data) => {
    const schema = Joi.object({
        macAddress: Joi.string().pattern(/^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$/).required(),
        deviceInfo: Joi.object({
            manufacturer: Joi.string().required(),
            model: Joi.string().required(),
            os: Joi.string().required(),
            appVersion: Joi.string().required()
        }).required(),
        tenantId: Joi.string().optional()
    });

    return schema.validate(data);
};
```

### 2. Sanitização de Dados
```javascript
const sanitizeInput = (input) => {
    if (typeof input === 'string') {
        return input.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
    }
    return input;
};
```

### 3. Logs de Segurança
```javascript
const logSecurityEvent = (event, details) => {
    console.log(JSON.stringify({
        timestamp: new Date().toISOString(),
        event,
        details,
        severity: 'SECURITY'
    }));
};
```

## 📊 Monitoramento

### 1. Health Check
```javascript
app.get('/health', (req, res) => {
    res.json({
        status: 'ok',
        timestamp: new Date().toISOString(),
        uptime: process.uptime(),
        memory: process.memoryUsage(),
        database: 'connected', // Verificar conexão real
        redis: 'connected' // Verificar conexão real
    });
});
```

### 2. Métricas
```javascript
const prometheus = require('prom-client');

const httpRequestsTotal = new prometheus.Counter({
    name: 'http_requests_total',
    help: 'Total number of HTTP requests',
    labelNames: ['method', 'route', 'status_code']
});

const authAttemptsTotal = new prometheus.Counter({
    name: 'auth_attempts_total',
    help: 'Total number of authentication attempts',
    labelNames: ['result', 'tenant_id']
});
```

## 🚀 Próximos Passos

1. **Implementar sistema de pagamento** - Integração com PIX/gateway
2. **Adicionar WebSocket** - Para notificações em tempo real
3. **Implementar cache** - Para melhor performance
4. **Adicionar logs estruturados** - Para monitoramento
5. **Implementar testes de carga** - Para verificar performance
6. **Adicionar documentação OpenAPI** - Para facilitar integração
7. **Implementar CI/CD** - Para deploy automatizado

## 📚 Documentação Adicional

- [API Documentation](./docs/api.md)
- [Database Schema](./docs/database.md)
- [Security Guidelines](./docs/security.md)
- [Deployment Guide](./docs/deployment.md)

---

**Última atualização:** 15/07/2025  
**Versão:** 1.0.0  
**Status:** 🔧 Em desenvolvimento