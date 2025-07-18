/**
 * Mock API Server for TV Streaming App
 * 
 * Este servidor simula todas as APIs necessárias para desenvolvimento
 * tanto do app Android quanto do app Tizen TV
 */

const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const morgan = require('morgan');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(morgan('dev'));

// Carregar dados mock
const mockData = require('./data/mock-data');

// Rota principal
app.get('/', (req, res) => {
    res.json({
        message: 'TV Streaming Mock API Server',
        version: '1.0.0',
        endpoints: {
            auth: {
                device: 'POST /api/auth/device',
                code: 'POST /api/auth/code',
                verify: 'GET /api/auth/verify/:token'
            },
            payment: {
                check: 'POST /api/payment/check',
                create: 'POST /api/payment/create'
            },
            content: {
                categories: 'GET /api/content/categories',
                byCategory: 'GET /api/content/:categoryId',
                details: 'GET /api/content/details/:contentId',
                search: 'GET /api/content/search'
            },
            user: {
                subscription: 'GET /api/user/subscription',
                devices: 'GET /api/user/devices',
                profile: 'GET /api/user/profile'
            }
        }
    });
});

// ======================
// AUTENTICAÇÃO
// ======================

// Autenticação por dispositivo (MAC Address)
app.post('/api/auth/device', (req, res) => {
    const { device_id, device_type, platform, app_version } = req.body;
    
    console.log('Device Authentication:', { device_id, device_type, platform, app_version });
    
    // Simular delay de rede
    setTimeout(() => {
        // Determinar cenário baseado no MAC address
        const lastChar = device_id ? device_id.slice(-1).toLowerCase() : '0';
        
        // Cenário 1: Novo dispositivo
        if (lastChar === '0' || lastChar === '1') {
            res.json({
                status: 'new_device',
                userInfo: null,
                serverInfo: mockData.serverInfo.premium,
                activationCode: generateActivationCode(),
                message: 'Dispositivo não cadastrado. Use o código de ativação.'
            });
        }
        // Cenário 2: Assinatura expirada
        else if (['a', 'b', 'c'].includes(lastChar)) {
            res.json({
                status: 'expired',
                userInfo: mockData.users.expired,
                serverInfo: mockData.serverInfo.basic,
                paymentUrl: `https://payment.rluparelli.com/renew?device=${device_id}&plan=premium`,
                paymentQR: {
                    pix: '00020126580014BR.GOV.BCB.PIX0136123e4567-e12b-12d1-a456-426614174000',
                    amount: 'R$ 29,90',
                    expiresIn: 3600
                },
                token: generateToken()
            });
        }
        // Cenário 3: Assinatura Premium ativa
        else if (['d', 'e', 'f'].includes(lastChar)) {
            res.json({
                status: 'active',
                userInfo: mockData.users.premium,
                serverInfo: mockData.serverInfo.premium,
                categories: mockData.categories.map(cat => cat.id),
                token: generateToken()
            });
        }
        // Cenário 4: Assinatura Basic ativa
        else {
            res.json({
                status: 'active',
                userInfo: mockData.users.basic,
                serverInfo: mockData.serverInfo.basic,
                categories: ['movies', 'series', 'live_tv'],
                token: generateToken()
            });
        }
    }, 500 + Math.random() * 1000);
});

// Autenticação por código
app.post('/api/auth/code', (req, res) => {
    const { code, device_id } = req.body;
    
    console.log('Code Authentication:', { code, device_id });
    
    setTimeout(() => {
        // Simular validação de código
        if (code && code.length === 7) { // Formato: XXX-XXX
            res.json({
                status: 'active',
                userInfo: mockData.users.premium,
                serverInfo: mockData.serverInfo.premium,
                message: 'Dispositivo ativado com sucesso!',
                token: generateToken()
            });
        } else {
            res.status(400).json({
                error: 'Código inválido',
                message: 'O código deve ter o formato XXX-XXX'
            });
        }
    }, 1000);
});

// ======================
// PAGAMENTO
// ======================

// Verificar status do pagamento
app.post('/api/payment/check', (req, res) => {
    const { device_id } = req.body;
    
    console.log('Payment Check:', { device_id });
    
    setTimeout(() => {
        // Simular 30% de chance de pagamento confirmado
        const isPaid = Math.random() < 0.3;
        
        res.json({
            paid: isPaid,
            timestamp: new Date().toISOString(),
            transactionId: isPaid ? `TRX-${Date.now()}` : null,
            message: isPaid ? 'Pagamento confirmado!' : 'Aguardando pagamento...'
        });
    }, 800);
});

// ======================
// CONTEÚDO
// ======================

// Listar categorias
app.get('/api/content/categories', (req, res) => {
    const token = req.headers.authorization;
    
    console.log('Get Categories, Auth:', token);
    
    setTimeout(() => {
        res.json({
            categories: mockData.categories
        });
    }, 500);
});

// Conteúdo por categoria
app.get('/api/content/:categoryId', (req, res) => {
    const { categoryId } = req.params;
    const { page = 1, limit = 20 } = req.query;
    
    console.log('Get Content by Category:', { categoryId, page, limit });
    
    setTimeout(() => {
        const category = mockData.categories.find(cat => cat.id === categoryId);
        
        if (!category) {
            return res.status(404).json({
                error: 'Categoria não encontrada'
            });
        }
        
        // Gerar conteúdo mock baseado na categoria
        const content = generateContentForCategory(categoryId, page, limit);
        
        res.json({
            category: category,
            content: content,
            pagination: {
                page: parseInt(page),
                limit: parseInt(limit),
                total: 100,
                totalPages: Math.ceil(100 / limit)
            }
        });
    }, 700);
});

// Detalhes do conteúdo
app.get('/api/content/details/:contentId', (req, res) => {
    const { contentId } = req.params;
    
    console.log('Get Content Details:', { contentId });
    
    setTimeout(() => {
        res.json({
            id: contentId,
            title: 'Título do Conteúdo',
            description: 'Descrição detalhada do conteúdo selecionado. Uma história envolvente que prende a atenção do início ao fim.',
            thumbnail: `https://via.placeholder.com/300x450/333333/FFFFFF?text=${contentId}`,
            banner: `https://via.placeholder.com/1920x600/333333/FFFFFF?text=${contentId}_Banner`,
            duration: 7200,
            year: 2024,
            rating: 8.5,
            genres: ['Ação', 'Aventura', 'Drama'],
            cast: ['Ator 1', 'Ator 2', 'Atriz 3'],
            director: 'Diretor Famoso',
            streamUrl: 'https://example.com/stream.m3u8',
            subtitles: [
                { language: 'pt-BR', url: 'https://example.com/subtitles_pt.vtt' },
                { language: 'en', url: 'https://example.com/subtitles_en.vtt' }
            ]
        });
    }, 600);
});

// ======================
// USUÁRIO
// ======================

// Status da assinatura
app.get('/api/user/subscription', (req, res) => {
    const token = req.headers.authorization;
    
    console.log('Get Subscription Status, Auth:', token);
    
    setTimeout(() => {
        res.json({
            subscription: {
                plan: 'Premium',
                status: 'active',
                expiresAt: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
                features: ['4k', 'downloads', 'multiple_profiles', 'no_ads'],
                price: 'R$ 29,90/mês'
            }
        });
    }, 400);
});

// Dispositivos do usuário
app.get('/api/user/devices', (req, res) => {
    const token = req.headers.authorization;
    
    console.log('Get User Devices, Auth:', token);
    
    setTimeout(() => {
        res.json({
            devices: [
                {
                    id: 'aa:bb:cc:dd:ee:ff',
                    name: 'Samsung TV Sala',
                    type: 'samsung_tv',
                    lastAccess: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
                    active: true
                },
                {
                    id: '11:22:33:44:55:66',
                    name: 'Android TV Quarto',
                    type: 'android_tv',
                    lastAccess: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
                    active: true
                },
                {
                    id: '77:88:99:aa:bb:cc',
                    name: 'Celular João',
                    type: 'android_mobile',
                    lastAccess: new Date(Date.now() - 72 * 60 * 60 * 1000).toISOString(),
                    active: false
                }
            ],
            maxDevices: 5,
            activeDevices: 2
        });
    }, 500);
});

// ======================
// FUNÇÕES AUXILIARES
// ======================

function generateActivationCode() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let code = '';
    for (let i = 0; i < 6; i++) {
        if (i === 3) code += '-';
        code += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return code;
}

function generateToken() {
    return 'mock_token_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
}

function generateContentForCategory(categoryId, page, limit) {
    const content = [];
    const startIndex = (page - 1) * limit;
    
    for (let i = 0; i < limit; i++) {
        const index = startIndex + i;
        content.push({
            id: `${categoryId}_${index}`,
            title: `${getCategoryTitle(categoryId)} ${index + 1}`,
            thumbnail: `https://via.placeholder.com/300x450/${getRandomColor()}/FFFFFF?text=${categoryId}_${index}`,
            duration: Math.floor(Math.random() * 7200) + 1800,
            year: 2020 + Math.floor(Math.random() * 5),
            rating: (Math.random() * 5 + 5).toFixed(1),
            isNew: Math.random() < 0.2,
            isPremium: Math.random() < 0.3
        });
    }
    
    return content;
}

function getCategoryTitle(categoryId) {
    const titles = {
        movies: 'Filme',
        series: 'Série',
        live_tv: 'Canal',
        anime: 'Anime',
        documentaries: 'Documentário',
        kids: 'Infantil'
    };
    return titles[categoryId] || 'Conteúdo';
}

function getRandomColor() {
    const colors = ['FF0000', '00FF00', '0000FF', 'FFFF00', 'FF00FF', '00FFFF', 'FFA500', '800080'];
    return colors[Math.floor(Math.random() * colors.length)];
}

// ======================
// TRATAMENTO DE ERROS
// ======================

// 404 handler
app.use((req, res) => {
    res.status(404).json({
        error: 'Endpoint não encontrado',
        path: req.originalUrl
    });
});

// Error handler
app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({
        error: 'Erro interno do servidor',
        message: err.message
    });
});

// ======================
// INICIAR SERVIDOR
// ======================

app.listen(PORT, () => {
    console.log(`
    ========================================
    TV Streaming Mock API Server
    ========================================
    
    Servidor rodando em: http://localhost:${PORT}
    
    Para testar no Tizen App, atualize o baseUrl em api.js:
    this.baseUrl = 'http://localhost:${PORT}/api';
    
    Para testar no Android App, use:
    - Emulador: http://10.0.2.2:${PORT}/api
    - Dispositivo físico: http://[SEU_IP]:${PORT}/api
    
    Endpoints disponíveis em: http://localhost:${PORT}
    ========================================
    `);
});