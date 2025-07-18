/**
 * Aplicação principal para Samsung Tizen TV
 */

class TVStreamingApp {
    constructor() {
        this.currentMac = null;
        this.userInfo = null;
        this.serverInfo = null;
        this.tenantId = 'default';
        
        this.init();
    }

    /**
     * Inicializa a aplicação
     */
    async init() {
        console.log('Inicializando TV Streaming App...');
        
        // Configurar eventos dos botões
        this.setupEventListeners();
        
        // Iniciar autenticação
        await this.startAuthentication();
    }

    /**
     * Configura listeners de eventos
     */
    setupEventListeners() {
        // Botão tentar novamente
        const retryBtn = document.getElementById('retry-btn');
        if (retryBtn) {
            retryBtn.addEventListener('click', () => this.startAuthentication());
        }

        // Botão verificar pagamento
        const checkPaymentBtn = document.getElementById('check-payment-btn');
        if (checkPaymentBtn) {
            checkPaymentBtn.addEventListener('click', () => this.checkPayment());
        }

        // Botão sair
        const closeBtn = document.getElementById('close-app-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.closeApp());
        }

        // Navegação por teclado para TV
        document.addEventListener('keydown', (event) => this.handleKeyNavigation(event));
    }

    /**
     * Inicia processo de autenticação
     */
    async startAuthentication() {
        try {
            // Mostrar tela de autenticação
            TizenUtils.showScreen('auth-screen');
            
            // Atualizar status
            this.updateAuthStatus('Detectando dispositivo...');
            
            // Gerar MAC address
            this.currentMac = await TizenUtils.generateMacAddress();
            
            // Exibir MAC na interface
            const deviceMacElement = document.getElementById('device-mac');
            if (deviceMacElement) {
                deviceMacElement.textContent = this.currentMac;
            }
            
            console.log('MAC Address detectado:', this.currentMac);
            TizenUtils.showToast(`Dispositivo: ${this.currentMac}`, 'info');
            
            // Atualizar status
            this.updateAuthStatus('Conectando com servidor...');
            
            // Aguardar um pouco para mostrar o loading
            await this.delay(1500);
            
            // Autenticar com o backend
            const authResult = await window.apiManager.authenticateDevice(this.currentMac);
            
            if (authResult.success) {
                await this.handleAuthenticationSuccess(authResult.data);
            } else {
                this.handleAuthenticationError(authResult.error || 'Erro desconhecido');
            }
            
        } catch (error) {
            console.error('Erro na autenticação:', error);
            this.handleAuthenticationError(error.message);
        }
    }

    /**
     * Manipula sucesso na autenticação
     */
    async handleAuthenticationSuccess(authData) {
        console.log('Autenticação bem-sucedida:', authData);
        
        this.serverInfo = authData.serverInfo;
        this.tenantId = authData.serverInfo?.tenantId || 'default';
        
        // Atualizar logo baseado no tenant
        TizenUtils.updateLogo(this.tenantId);
        
        // Verificar status do dispositivo
        switch (authData.status) {
            case 'new_device':
                this.showActivationScreen(authData);
                break;
                
            case 'expired':
                this.userInfo = authData.userInfo;
                TizenUtils.saveData('userInfo', this.userInfo);
                TizenUtils.saveData('serverInfo', this.serverInfo);
                TizenUtils.saveData('tenantId', this.tenantId);
                this.showPaymentScreen(authData.paymentUrl, authData.paymentQR);
                break;
                
            case 'active':
                this.userInfo = authData.userInfo;
                TizenUtils.saveData('userInfo', this.userInfo);
                TizenUtils.saveData('serverInfo', this.serverInfo);
                TizenUtils.saveData('tenantId', this.tenantId);
                this.showSuccessScreen();
                break;
                
            default:
                this.handleAuthenticationError('Status desconhecido');
        }
    }

    /**
     * Mostra tela de ativação para novos dispositivos
     */
    showActivationScreen(authData) {
        console.log('Mostrando tela de ativação');
        
        // Criar tela de ativação dinamicamente
        const activationHTML = `
            <div id="activation-screen" class="screen active">
                <div class="activation-container">
                    <h2>🔐 Ativar Dispositivo</h2>
                    <div class="activation-info">
                        <p>Este dispositivo ainda não está cadastrado.</p>
                        <p>Para ativar, acesse nosso site e digite o código:</p>
                        <div class="activation-code">${authData.activationCode}</div>
                        <p class="activation-url">www.rluparelli.com/ativar</p>
                        <div class="device-info">
                            <p><strong>Dispositivo:</strong> ${this.currentMac}</p>
                            <p><strong>Servidor:</strong> ${authData.serverInfo.serverName}</p>
                        </div>
                    </div>
                    <div class="activation-actions">
                        <button id="check-activation-btn" class="btn">Verificar Ativação</button>
                        <button id="refresh-code-btn" class="btn secondary">Novo Código</button>
                    </div>
                </div>
            </div>
        `;
        
        // Adicionar tela ao DOM
        const mainContent = document.querySelector('.main-content');
        const existingScreen = document.getElementById('activation-screen');
        if (existingScreen) {
            existingScreen.remove();
        }
        mainContent.insertAdjacentHTML('beforeend', activationHTML);
        
        // Ocultar outras telas
        document.querySelectorAll('.screen').forEach(screen => {
            if (screen.id !== 'activation-screen') {
                screen.classList.remove('active');
            }
        });
        
        // Configurar eventos
        document.getElementById('check-activation-btn')?.addEventListener('click', () => {
            this.checkActivation();
        });
        
        document.getElementById('refresh-code-btn')?.addEventListener('click', () => {
            this.startAuthentication();
        });
        
        TizenUtils.showToast('Use o código de ativação no site', 'info');
    }

    /**
     * Verifica se o dispositivo foi ativado
     */
    async checkActivation() {
        try {
            TizenUtils.showToast('Verificando ativação...', 'info');
            
            // Simular verificação (em produção, chamaria a API)
            await this.delay(2000);
            
            // Reiniciar autenticação para verificar novo status
            await this.startAuthentication();
            
        } catch (error) {
            console.error('Erro ao verificar ativação:', error);
            TizenUtils.showToast('Erro ao verificar ativação', 'error');
        }
    }

    /**
     * Manipula erro na autenticação
     */
    handleAuthenticationError(error) {
        console.error('Erro de autenticação:', error);
        
        this.updateAuthStatus(`Erro: ${error}`);
        TizenUtils.showToast(`Erro de autenticação: ${error}`, 'error');
        
        // Mostrar botão para tentar novamente
        const retryBtn = document.getElementById('retry-btn');
        if (retryBtn) {
            retryBtn.classList.remove('hidden');
        }
        
        // Ocultar loading
        const loading = document.getElementById('loading');
        if (loading) {
            loading.style.display = 'none';
        }
    }

    /**
     * Mostra tela de pagamento
     */
    showPaymentScreen(paymentUrl, paymentQR) {
        console.log('Mostrando tela de pagamento');
        
        TizenUtils.showScreen('payment-screen');
        
        // Atualizar informações de pagamento
        const deviceElement = document.getElementById('payment-device');
        if (deviceElement) {
            deviceElement.textContent = this.currentMac;
        }
        
        const urlElement = document.getElementById('payment-url');
        if (urlElement) {
            const url = paymentUrl || window.apiManager.generatePaymentUrl(this.currentMac, this.tenantId);
            urlElement.textContent = url;
        }
        
        // Gerar QR Code (placeholder ou PIX)
        const qrElement = document.getElementById('qr-code');
        if (qrElement) {
            if (paymentQR && paymentQR.pix) {
                qrElement.innerHTML = `
                    <div style="text-align: center;">
                        <p style="font-size: 14px; margin-bottom: 10px;">PIX</p>
                        <p style="font-size: 24px; font-weight: bold;">${paymentQR.amount}</p>
                        <p style="font-size: 12px; margin-top: 10px;">QR Code PIX</p>
                    </div>
                `;
            } else {
                const qrData = window.apiManager.generateQRCode(paymentUrl);
                qrElement.textContent = qrData.placeholder;
            }
        }
        
        TizenUtils.showToast('Assinatura expirada. Renove para continuar.', 'error');
    }

    /**
     * Mostra tela de sucesso
     */
    showSuccessScreen() {
        console.log('Mostrando tela de sucesso');
        
        TizenUtils.showScreen('success-screen');
        
        // Atualizar informações do usuário
        const expiryElement = document.getElementById('expiry-date');
        if (expiryElement && this.userInfo) {
            expiryElement.textContent = TizenUtils.formatTimestamp(this.userInfo.expDate);
        }
        
        const deviceElement = document.getElementById('success-device');
        if (deviceElement) {
            deviceElement.textContent = this.currentMac;
        }
        
        const expDate = TizenUtils.formatTimestamp(this.userInfo?.expDate);
        TizenUtils.showToast(`✅ Acesso liberado até: ${expDate}`, 'success');
        
        // Atualizar status no header
        const statusElement = document.getElementById('status');
        if (statusElement) {
            statusElement.textContent = `Online - ${this.userInfo?.name || 'Usuário'}`;
        }
    }

    /**
     * Verifica status do pagamento
     */
    async checkPayment() {
        try {
            TizenUtils.showToast('Verificando pagamento...', 'info');
            
            const result = await window.apiManager.checkPaymentStatus(this.currentMac);
            
            if (result.success && result.data?.paid) {
                TizenUtils.showToast('Pagamento confirmado!', 'success');
                // Reiniciar autenticação para obter novos dados
                await this.delay(1000);
                await this.startAuthentication();
            } else {
                TizenUtils.showToast('Pagamento ainda não detectado', 'error');
            }
            
        } catch (error) {
            console.error('Erro ao verificar pagamento:', error);
            TizenUtils.showToast('Erro ao verificar pagamento', 'error');
        }
    }

    /**
     * Fecha a aplicação
     */
    closeApp() {
        try {
            // Tentar usar API do Tizen para fechar
            if (typeof tizen !== 'undefined' && tizen.application) {
                tizen.application.getCurrentApplication().exit();
            } else {
                // Fallback para navegador
                TizenUtils.showToast('Pressione o botão Home do controle para sair', 'info');
                // Ou tentar fechar a janela
                window.close();
            }
        } catch (error) {
            console.error('Erro ao fechar app:', error);
            TizenUtils.showToast('Use o controle remoto para sair', 'info');
        }
    }

    /**
     * Manipula navegação por teclado/controle remoto
     */
    handleKeyNavigation(event) {
        const focusableElements = document.querySelectorAll('button:not(.hidden), .content-item');
        const currentFocus = document.activeElement;
        const currentIndex = Array.from(focusableElements).indexOf(currentFocus);
        
        switch (event.keyCode) {
            case 37: // Seta esquerda
                event.preventDefault();
                if (currentIndex > 0) {
                    focusableElements[currentIndex - 1].focus();
                }
                break;
                
            case 39: // Seta direita
                event.preventDefault();
                if (currentIndex < focusableElements.length - 1) {
                    focusableElements[currentIndex + 1].focus();
                }
                break;
                
            case 38: // Seta cima
                event.preventDefault();
                // Navegar para linha acima
                break;
                
            case 40: // Seta baixo
                event.preventDefault();
                // Navegar para linha abaixo
                break;
                
            case 13: // Enter
                event.preventDefault();
                if (currentFocus && currentFocus.click) {
                    currentFocus.click();
                }
                break;
                
            case 27: // ESC
                event.preventDefault();
                this.closeApp();
                break;
        }
    }

    /**
     * Atualiza status de autenticação
     */
    updateAuthStatus(status) {
        const statusElement = document.getElementById('auth-status');
        if (statusElement) {
            statusElement.textContent = status;
        }
        
        const headerStatus = document.getElementById('status');
        if (headerStatus) {
            headerStatus.textContent = status;
        }
    }

    /**
     * Utilitário para delay
     */
    delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}

// Inicializar aplicação quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM carregado, inicializando app...');
    window.tvApp = new TVStreamingApp();
});

// Manipular eventos do Tizen
document.addEventListener('tizenhwkey', (event) => {
    if (event.keyName === 'back') {
        if (window.tvApp) {
            window.tvApp.closeApp();
        }
    }
});