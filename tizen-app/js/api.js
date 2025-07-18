/**
 * Gerenciador de API para comunicação com o backend
 */

class ApiManager {
    constructor() {
        // URLs do backend (ajustar conforme necessário)
        this.baseUrl = 'https://your-backend-url.com/api';
        this.endpoints = {
            authenticate: '/auth/device',
            checkPayment: '/payment/check',
            getContent: '/content'
        };
    }

    /**
     * Autentica dispositivo no backend
     */
    async authenticateDevice(macAddress) {
        try {
            console.log('Authenticating device:', macAddress);
            
            const response = await fetch(`${this.baseUrl}${this.endpoints.authenticate}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    device_id: macAddress,
                    device_type: 'samsung_tv',
                    platform: 'tizen',
                    app_version: '1.0.0'
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            console.log('Authentication response:', data);
            
            return {
                success: true,
                data: data
            };

        } catch (error) {
            console.error('Authentication error:', error);
            
            // Para desenvolvimento/teste, retornar dados mockados
            if (error.message.includes('fetch')) {
                console.log('Backend não disponível, usando dados de teste...');
                return this.getMockAuthResponse(macAddress);
            }
            
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Verifica status do pagamento
     */
    async checkPaymentStatus(deviceId) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoints.checkPayment}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    device_id: deviceId
                })
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();

        } catch (error) {
            console.error('Payment check error:', error);
            
            // Mock para desenvolvimento
            if (error.message.includes('fetch')) {
                return this.getMockPaymentStatus(deviceId);
            }
            
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Mock do status de pagamento
     */
    getMockPaymentStatus(deviceId) {
        return new Promise((resolve) => {
            setTimeout(() => {
                // Simular 30% de chance de pagamento confirmado
                const isPaid = Math.random() < 0.3;
                
                resolve({
                    success: true,
                    data: {
                        paid: isPaid,
                        timestamp: Math.floor(Date.now() / 1000),
                        transactionId: isPaid ? `TRX-${Date.now()}` : null,
                        message: isPaid ? 'Pagamento confirmado!' : 'Aguardando pagamento...'
                    }
                });
            }, 1000 + Math.random() * 1000);
        });
    }

    /**
     * Obtém conteúdo disponível
     */
    async getContent(userId, tenantId) {
        try {
            const response = await fetch(`${this.baseUrl}${this.endpoints.getContent}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${userId}`,
                    'X-Tenant-ID': tenantId
                }
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            return await response.json();

        } catch (error) {
            console.error('Content fetch error:', error);
            
            // Mock para desenvolvimento
            if (error.message.includes('fetch')) {
                return this.getMockContent();
            }
            
            return {
                success: false,
                error: error.message
            };
        }
    }

    /**
     * Mock de conteúdo
     */
    getMockContent() {
        return new Promise((resolve) => {
            setTimeout(() => {
                resolve({
                    success: true,
                    data: {
                        categories: [
                            {
                                id: 'movies',
                                name: 'Filmes',
                                icon: '🎬',
                                itemCount: 1250,
                                featured: [
                                    {
                                        id: 'movie_1',
                                        title: 'Ação Total',
                                        thumbnail: 'https://via.placeholder.com/300x450/FF0000/FFFFFF?text=Acao+Total',
                                        duration: 7200,
                                        year: 2024,
                                        rating: 8.5
                                    },
                                    {
                                        id: 'movie_2',
                                        title: 'Comédia em Família',
                                        thumbnail: 'https://via.placeholder.com/300x450/00FF00/000000?text=Comedia',
                                        duration: 5400,
                                        year: 2024,
                                        rating: 7.2
                                    }
                                ]
                            },
                            {
                                id: 'series',
                                name: 'Séries',
                                icon: '📺',
                                itemCount: 450,
                                featured: [
                                    {
                                        id: 'series_1',
                                        title: 'Drama Investigativo',
                                        thumbnail: 'https://via.placeholder.com/300x450/0000FF/FFFFFF?text=Drama',
                                        seasons: 3,
                                        episodes: 24,
                                        year: 2023,
                                        rating: 9.0
                                    }
                                ]
                            },
                            {
                                id: 'live_tv',
                                name: 'TV Ao Vivo',
                                icon: '🔴',
                                itemCount: 85,
                                featured: [
                                    {
                                        id: 'channel_1',
                                        title: 'Canal News 24h',
                                        thumbnail: 'https://via.placeholder.com/300x450/FF00FF/FFFFFF?text=News',
                                        isLive: true,
                                        viewers: 15420
                                    }
                                ]
                            },
                            {
                                id: 'anime',
                                name: 'Anime',
                                icon: '🎌',
                                itemCount: 320,
                                featured: [
                                    {
                                        id: 'anime_1',
                                        title: 'Aventura Épica',
                                        thumbnail: 'https://via.placeholder.com/300x450/FFFF00/000000?text=Anime',
                                        episodes: 150,
                                        year: 2024,
                                        rating: 8.8
                                    }
                                ]
                            }
                        ],
                        spotlight: {
                            id: 'spotlight_1',
                            title: 'Lançamento da Semana',
                            description: 'O filme mais esperado do ano chegou! Ação, aventura e emoção em uma história inesquecível.',
                            banner: 'https://via.placeholder.com/1920x600/333333/FFFFFF?text=Lancamento+da+Semana',
                            videoUrl: 'https://example.com/trailer.mp4',
                            category: 'movies'
                        }
                    }
                });
            }, 500 + Math.random() * 1000);
        });
    }

    /**
     * Dados mockados para desenvolvimento/teste
     */
    getMockAuthResponse(macAddress) {
        // Simular delay de rede
        return new Promise((resolve) => {
            setTimeout(() => {
                // Simular diferentes cenários baseado no MAC
                const lastDigit = macAddress.slice(-1);
                const now = Math.floor(Date.now() / 1000);
                
                // Cenário 1: Novo dispositivo (não cadastrado)
                if (lastDigit === '0' || lastDigit === '1') {
                    resolve({
                        success: true,
                        data: {
                            status: 'new_device',
                            userInfo: null,
                            serverInfo: {
                                tenantId: 'fasted',
                                serverName: 'Streaming R.Luparelli',
                                features: ['streaming', 'live_tv', 'downloads']
                            },
                            activationCode: this.generateActivationCode(),
                            message: 'Dispositivo não cadastrado. Use o código de ativação.'
                        }
                    });
                }
                // Cenário 2: Assinatura expirada
                else if (lastDigit === 'a' || lastDigit === 'b' || lastDigit === 'c') {
                    resolve({
                        success: true,
                        data: {
                            status: 'expired',
                            userInfo: {
                                id: 'user_123',
                                name: 'João Silva',
                                email: 'joao.silva@example.com',
                                expDate: now - (7 * 86400), // Expirou há 7 dias
                                subscriptionStatus: 'expired',
                                plan: 'Premium',
                                devicesCount: 3,
                                maxDevices: 5
                            },
                            serverInfo: {
                                tenantId: 'fasted',
                                serverName: 'Streaming R.Luparelli',
                                features: ['streaming', 'live_tv'],
                                serverUrl: 'https://streaming.rluparelli.com'
                            },
                            paymentUrl: `https://payment.rluparelli.com/renew?device=${macAddress}&plan=premium`,
                            paymentQR: {
                                pix: '00020126580014BR.GOV.BCB.PIX0136123e4567-e12b-12d1-a456-426614174000',
                                amount: 'R$ 29,90',
                                expiresIn: 3600 // 1 hora
                            }
                        }
                    });
                }
                // Cenário 3: Assinatura ativa - Premium
                else if (lastDigit === 'd' || lastDigit === 'e' || lastDigit === 'f') {
                    resolve({
                        success: true,
                        data: {
                            status: 'active',
                            userInfo: {
                                id: 'user_456',
                                name: 'Maria Santos',
                                email: 'maria.santos@example.com',
                                expDate: now + (30 * 86400), // Expira em 30 dias
                                subscriptionStatus: 'active',
                                plan: 'Premium',
                                devicesCount: 2,
                                maxDevices: 5
                            },
                            serverInfo: {
                                tenantId: 'fasted',
                                serverName: 'Streaming R.Luparelli Premium',
                                features: ['streaming', 'live_tv', 'downloads', '4k', 'multiple_profiles'],
                                serverUrl: 'https://streaming.rluparelli.com'
                            },
                            categories: ['movies', 'series', 'live_tv', 'anime', 'documentaries', 'kids']
                        }
                    });
                }
                // Cenário 4: Assinatura ativa - Básica
                else {
                    resolve({
                        success: true,
                        data: {
                            status: 'active',
                            userInfo: {
                                id: 'user_789',
                                name: 'Pedro Oliveira',
                                email: 'pedro.oliveira@example.com',
                                expDate: now + (15 * 86400), // Expira em 15 dias
                                subscriptionStatus: 'active',
                                plan: 'Basic',
                                devicesCount: 1,
                                maxDevices: 2
                            },
                            serverInfo: {
                                tenantId: 'fasted',
                                serverName: 'Streaming R.Luparelli Basic',
                                features: ['streaming', 'live_tv'],
                                serverUrl: 'https://streaming.rluparelli.com'
                            },
                            categories: ['movies', 'series', 'live_tv']
                        }
                    });
                }
            }, 800 + Math.random() * 1200); // Delay aleatório entre 800-2000ms
        });
    }

    /**
     * Gera código de ativação
     */
    generateActivationCode() {
        const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
        let code = '';
        for (let i = 0; i < 6; i++) {
            if (i === 3) code += '-';
            code += chars.charAt(Math.floor(Math.random() * chars.length));
        }
        return code;
    }

    /**
     * Verifica se a assinatura está expirada
     */
    isSubscriptionExpired(userInfo) {
        if (!userInfo || !userInfo.expDate) {
            return true;
        }
        
        const now = Math.floor(Date.now() / 1000);
        return userInfo.expDate < now;
    }

    /**
     * Gera URL de pagamento
     */
    generatePaymentUrl(deviceId, tenantId = 'default') {
        return `https://payment.example.com/renew?device=${deviceId}&tenant=${tenantId}&source=samsung_tv`;
    }

    /**
     * Gera QR Code para pagamento (placeholder)
     */
    generateQRCode(url) {
        // Em um app real, você usaria uma biblioteca como qrcode.js
        // Por enquanto, retornamos um placeholder
        return {
            url: url,
            placeholder: `QR: ${url.substring(0, 50)}...`
        };
    }
}

// Instância global
window.apiManager = new ApiManager();