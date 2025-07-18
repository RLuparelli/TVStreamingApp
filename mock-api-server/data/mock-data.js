/**
 * Dados mock para o servidor de API
 */

const now = Math.floor(Date.now() / 1000);

module.exports = {
    // Informações do servidor/tenant
    serverInfo: {
        basic: {
            tenantId: 'fasted',
            serverName: 'Streaming R.Luparelli Basic',
            features: ['streaming', 'live_tv'],
            serverUrl: 'https://streaming.rluparelli.com',
            supportEmail: 'suporte@rluparelli.com',
            whatsapp: '+55 11 99999-9999'
        },
        premium: {
            tenantId: 'fasted',
            serverName: 'Streaming R.Luparelli Premium',
            features: ['streaming', 'live_tv', 'downloads', '4k', 'multiple_profiles'],
            serverUrl: 'https://streaming.rluparelli.com',
            supportEmail: 'suporte@rluparelli.com',
            whatsapp: '+55 11 99999-9999'
        }
    },
    
    // Usuários mock
    users: {
        expired: {
            id: 'user_123',
            name: 'João Silva',
            email: 'joao.silva@example.com',
            expDate: now - (7 * 86400), // Expirou há 7 dias
            subscriptionStatus: 'expired',
            plan: 'Premium',
            devicesCount: 3,
            maxDevices: 5,
            createdAt: now - (365 * 86400), // Criado há 1 ano
            lastLogin: now - (10 * 86400)
        },
        premium: {
            id: 'user_456',
            name: 'Maria Santos',
            email: 'maria.santos@example.com',
            expDate: now + (30 * 86400), // Expira em 30 dias
            subscriptionStatus: 'active',
            plan: 'Premium',
            devicesCount: 2,
            maxDevices: 5,
            createdAt: now - (180 * 86400), // Criado há 6 meses
            lastLogin: now - (3600) // Último login há 1 hora
        },
        basic: {
            id: 'user_789',
            name: 'Pedro Oliveira',
            email: 'pedro.oliveira@example.com',
            expDate: now + (15 * 86400), // Expira em 15 dias
            subscriptionStatus: 'active',
            plan: 'Basic',
            devicesCount: 1,
            maxDevices: 2,
            createdAt: now - (60 * 86400), // Criado há 2 meses
            lastLogin: now - (7200) // Último login há 2 horas
        }
    },
    
    // Categorias de conteúdo
    categories: [
        {
            id: 'movies',
            name: 'Filmes',
            icon: '🎬',
            description: 'Os melhores filmes nacionais e internacionais',
            itemCount: 1250,
            order: 1
        },
        {
            id: 'series',
            name: 'Séries',
            icon: '📺',
            description: 'Séries completas e lançamentos semanais',
            itemCount: 450,
            order: 2
        },
        {
            id: 'live_tv',
            name: 'TV Ao Vivo',
            icon: '🔴',
            description: 'Canais de TV em tempo real',
            itemCount: 85,
            order: 3
        },
        {
            id: 'anime',
            name: 'Anime',
            icon: '🎌',
            description: 'Animes dublados e legendados',
            itemCount: 320,
            order: 4
        },
        {
            id: 'documentaries',
            name: 'Documentários',
            icon: '🎥',
            description: 'Documentários educativos e informativos',
            itemCount: 180,
            order: 5
        },
        {
            id: 'kids',
            name: 'Infantil',
            icon: '👶',
            description: 'Conteúdo seguro para crianças',
            itemCount: 290,
            order: 6
        }
    ],
    
    // Conteúdo em destaque
    spotlight: [
        {
            id: 'spotlight_1',
            title: 'O Grande Filme',
            description: 'Uma aventura épica que vai te deixar sem fôlego. Ação do início ao fim!',
            banner: 'https://via.placeholder.com/1920x600/FF5733/FFFFFF?text=O+Grande+Filme',
            thumbnail: 'https://via.placeholder.com/300x450/FF5733/FFFFFF?text=O+Grande+Filme',
            category: 'movies',
            duration: 8400,
            year: 2024,
            rating: 9.2,
            streamUrl: 'https://example.com/movie1.m3u8'
        },
        {
            id: 'spotlight_2',
            title: 'Série do Momento',
            description: 'A série que todo mundo está comentando. Drama, suspense e reviravoltas.',
            banner: 'https://via.placeholder.com/1920x600/3498DB/FFFFFF?text=Serie+do+Momento',
            thumbnail: 'https://via.placeholder.com/300x450/3498DB/FFFFFF?text=Serie+do+Momento',
            category: 'series',
            seasons: 3,
            episodes: 30,
            year: 2023,
            rating: 8.8,
            nextEpisode: 'Sexta-feira, 20h'
        }
    ],
    
    // Canais ao vivo
    liveChannels: [
        {
            id: 'channel_news',
            name: 'News 24h',
            number: 501,
            logo: 'https://via.placeholder.com/200x200/FF0000/FFFFFF?text=NEWS',
            category: 'Notícias',
            currentProgram: 'Jornal da Noite',
            nextProgram: 'Debate Político',
            streamUrl: 'https://example.com/news.m3u8',
            isHD: true
        },
        {
            id: 'channel_sports',
            name: 'Sports TV',
            number: 502,
            logo: 'https://via.placeholder.com/200x200/00FF00/FFFFFF?text=SPORTS',
            category: 'Esportes',
            currentProgram: 'Futebol ao Vivo',
            nextProgram: 'Mesa Redonda',
            streamUrl: 'https://example.com/sports.m3u8',
            isHD: true
        },
        {
            id: 'channel_movies',
            name: 'Cine Premium',
            number: 503,
            logo: 'https://via.placeholder.com/200x200/0000FF/FFFFFF?text=CINE',
            category: 'Filmes',
            currentProgram: 'Filme: Ação Total',
            nextProgram: 'Filme: Comédia Romântica',
            streamUrl: 'https://example.com/movies.m3u8',
            isHD: true
        }
    ],
    
    // Gêneros
    genres: [
        'Ação', 'Aventura', 'Animação', 'Biografia', 'Comédia', 'Crime',
        'Documentário', 'Drama', 'Família', 'Fantasia', 'Ficção Científica',
        'Guerra', 'História', 'Horror', 'Musical', 'Mistério', 'Romance',
        'Esporte', 'Suspense', 'Thriller', 'Western'
    ],
    
    // Planos de assinatura
    plans: [
        {
            id: 'basic',
            name: 'Basic',
            price: 'R$ 19,90',
            period: 'mês',
            features: [
                'Acesso ao catálogo básico',
                'Qualidade HD',
                '2 dispositivos simultâneos',
                'TV ao vivo básica'
            ],
            recommended: false
        },
        {
            id: 'premium',
            name: 'Premium',
            price: 'R$ 29,90',
            period: 'mês',
            features: [
                'Acesso completo ao catálogo',
                'Qualidade 4K Ultra HD',
                '5 dispositivos simultâneos',
                'TV ao vivo completa',
                'Downloads offline',
                'Múltiplos perfis',
                'Sem anúncios'
            ],
            recommended: true
        },
        {
            id: 'family',
            name: 'Family',
            price: 'R$ 39,90',
            period: 'mês',
            features: [
                'Tudo do Premium',
                '10 dispositivos simultâneos',
                'Controle parental avançado',
                'Perfis infantis',
                'Compartilhamento familiar'
            ],
            recommended: false
        }
    ]
};