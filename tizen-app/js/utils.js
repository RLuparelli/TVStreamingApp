/**
 * Utilitários para Samsung Tizen TV
 */

class TizenUtils {
    
    /**
     * Gera hash MD5 simples (para compatibility)
     */
    static generateMD5(str) {
        // Implementação simples de hash para gerar MAC
        let hash = 0;
        if (str.length === 0) return hash.toString();
        
        for (let i = 0; i < str.length; i++) {
            const char = str.charCodeAt(i);
            hash = ((hash << 5) - hash) + char;
            hash = hash & hash; // Convert to 32-bit integer
        }
        
        // Convert to hex and pad
        let hexHash = Math.abs(hash).toString(16);
        while (hexHash.length < 12) {
            hexHash = '0' + hexHash;
        }
        
        return hexHash.substring(0, 12);
    }

    /**
     * Obtém informações do dispositivo Samsung TV
     */
    static getDeviceInfo() {
        try {
            // Tentar usar API do Tizen
            if (typeof tizen !== 'undefined' && tizen.systeminfo) {
                return new Promise((resolve) => {
                    tizen.systeminfo.getPropertyValue("BUILD", (build) => {
                        tizen.systeminfo.getPropertyValue("DEVICE", (device) => {
                            resolve({
                                model: build.model || 'Samsung-TV',
                                brand: 'Samsung',
                                board: device.processor || 'ARM',
                                device: build.buildVersion || 'Tizen',
                                serial: this.getTizenSerial()
                            });
                        });
                    });
                });
            }
        } catch (error) {
            console.log('Tizen API não disponível, usando fallback');
        }

        // Fallback usando informações do navegador
        return Promise.resolve({
            model: navigator.userAgent.includes('Tizen') ? 'Samsung-Smart-TV' : 'Unknown-TV',
            brand: 'Samsung',
            board: 'ARM-Cortex',
            device: 'Tizen-WebApp',
            serial: this.getBrowserSerial()
        });
    }

    /**
     * Tenta obter serial do Tizen
     */
    static getTizenSerial() {
        try {
            if (typeof webapis !== 'undefined' && webapis.productinfo) {
                return webapis.productinfo.getSerialNumber() || this.getBrowserSerial();
            }
        } catch (error) {
            console.log('Serial Tizen não disponível');
        }
        return this.getBrowserSerial();
    }

    /**
     * Gera serial baseado no navegador
     */
    static getBrowserSerial() {
        // Usar características únicas do navegador/dispositivo
        const canvas = document.createElement('canvas');
        const ctx = canvas.getContext('2d');
        ctx.textBaseline = 'top';
        ctx.font = '14px Arial';
        ctx.fillText('Samsung TV Fingerprint', 2, 2);
        
        const fingerprint = canvas.toDataURL() + 
                          navigator.userAgent + 
                          screen.width + 
                          screen.height + 
                          new Date().getTimezoneOffset();
        
        return this.generateMD5(fingerprint).substring(0, 8);
    }

    /**
     * Gera MAC address para Samsung TV
     */
    static async generateMacAddress() {
        try {
            const deviceInfo = await this.getDeviceInfo();
            
            // Concatenar informações únicas do dispositivo
            const uniqueString = 
                (deviceInfo.model || '') +
                (deviceInfo.brand || '') +
                (deviceInfo.board || '') +
                (deviceInfo.device || '') +
                (deviceInfo.serial || '').substring(0, 8);

            console.log('Device Info:', deviceInfo);
            console.log('Unique String:', uniqueString);

            // Gerar hash e formatar como MAC
            const hash = this.generateMD5(uniqueString);
            const mac = hash.substring(0, 12);

            const formattedMac = `${mac.substring(0,2)}:${mac.substring(2,4)}:${mac.substring(4,6)}:${mac.substring(6,8)}:${mac.substring(8,10)}:${mac.substring(10,12)}`;
            
            console.log('Generated MAC:', formattedMac);
            return formattedMac;
            
        } catch (error) {
            console.error('Erro ao gerar MAC:', error);
            // Fallback MAC baseado em timestamp
            const fallbackMac = this.generateMD5(Date.now().toString() + navigator.userAgent).substring(0, 12);
            return `${fallbackMac.substring(0,2)}:${fallbackMac.substring(2,4)}:${fallbackMac.substring(4,6)}:${fallbackMac.substring(6,8)}:${fallbackMac.substring(8,10)}:${fallbackMac.substring(10,12)}`;
        }
    }

    /**
     * Salva dados no localStorage
     */
    static saveData(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
        } catch (error) {
            console.error('Erro ao salvar dados:', error);
        }
    }

    /**
     * Recupera dados do localStorage
     */
    static getData(key) {
        try {
            const data = localStorage.getItem(key);
            return data ? JSON.parse(data) : null;
        } catch (error) {
            console.error('Erro ao recuperar dados:', error);
            return null;
        }
    }

    /**
     * Formata timestamp para data legível
     */
    static formatTimestamp(timestamp) {
        if (!timestamp) return 'Data inválida';
        
        const date = new Date(timestamp * 1000);
        return date.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    /**
     * Mostra toast/notificação
     */
    static showToast(message, type = 'info') {
        console.log(`[${type.toUpperCase()}] ${message}`);
        
        // Criar elemento de toast visual
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.textContent = message;
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${type === 'error' ? '#ff4757' : type === 'success' ? '#2ed573' : '#00d4ff'};
            color: white;
            padding: 15px 25px;
            border-radius: 10px;
            font-weight: bold;
            z-index: 9999;
            animation: slideIn 0.3s ease;
        `;
        
        document.body.appendChild(toast);
        
        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => document.body.removeChild(toast), 300);
        }, 3000);
    }

    /**
     * Atualiza logo baseado no tenant
     */
    static updateLogo(tenantId) {
        const logoElement = document.getElementById('logo');
        if (logoElement) {
            switch (tenantId?.toLowerCase()) {
                case 'fasted':
                    logoElement.textContent = 'R.Luparelli';
                    break;
                case 'netflix':
                    logoElement.textContent = 'NETFLIX';
                    break;
                case 'prime':
                    logoElement.textContent = 'PRIME';
                    break;
                default:
                    logoElement.textContent = 'STREAMING';
            }
        }
    }

    /**
     * Navega entre telas
     */
    static showScreen(screenId) {
        // Ocultar todas as telas
        document.querySelectorAll('.screen').forEach(screen => {
            screen.classList.remove('active');
        });
        
        // Mostrar tela específica
        const targetScreen = document.getElementById(screenId);
        if (targetScreen) {
            setTimeout(() => {
                targetScreen.classList.add('active');
            }, 100);
        }
    }
}

// CSS para animações de toast
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);