package com.tvstreaming.app

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.tvstreaming.app.models.UserInfo
import com.tvstreaming.app.models.ServerInfo
import com.tvstreaming.app.network.ApiManager
import com.tvstreaming.app.utils.AppUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var userInfo: UserInfo? = null
    private var serverInfo: ServerInfo? = null
    private var tenantId: String = "default"
    private lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiManager = ApiManager.getInstance(this)

        // Autenticar dispositivo
        authenticateDevice()
    }

    private fun authenticateDevice() {
        val macAddress = AppUtils.getMacAddress(this) // Passar o contexto
        Toast.makeText(this, "Autenticando dispositivo: $macAddress", Toast.LENGTH_LONG).show()

        lifecycleScope.launch {
            apiManager.authenticateDevice(macAddress)
                .onSuccess { authResponse ->
                    handleAuthenticationSuccess(authResponse)
                }
                .onFailure { error ->
                    handleAuthenticationError(error.message ?: "Erro desconhecido")
                }
        }
    }

    private fun handleAuthenticationSuccess(authResponse: com.tvstreaming.app.models.AuthResponse) {
        userInfo = authResponse.userInfo
        serverInfo = authResponse.serverInfo
        tenantId = authResponse.serverInfo.tenantId ?: "default"

        // Salvar dados
        AppUtils.saveTenantId(this, tenantId)

        // Verificar se a assinatura está expirada
        if (apiManager.isSubscriptionExpired(authResponse.userInfo)) {
            showPaymentDialog()
        } else {
            proceedToMainApp()
        }
    }

    private fun handleAuthenticationError(error: String) {
        Toast.makeText(this, "Erro de autenticação: $error", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun proceedToMainApp() {
        // Atualizar logo baseado no tenant
        updateLogo()

        val expDate = AppUtils.formatTimestamp(userInfo?.expDate?.toLong() ?: 0)
        Toast.makeText(this, "✅ Acesso liberado até: $expDate", Toast.LENGTH_LONG).show()

        // TODO: Carregar HomeFragment aqui
    }

    private fun updateLogo() {
        val logoText = findViewById<TextView>(R.id.tv_logo)
        logoText?.text = when (tenantId.lowercase()) {
            "fasted" -> "R.Luparelli"
            "netflix" -> "NETFLIX"
            "prime" -> "PRIME"
            else -> "STREAMING"
        }
    }

    private fun showPaymentDialog() {
        val macAddress = AppUtils.getDeviceId(this)
        val paymentUrl = "https://payment.example.com/renew?device=$macAddress"

        AlertDialog.Builder(this)
            .setTitle("🚫 Assinatura Expirada")
            .setMessage("Sua assinatura expirou.\n\nDispositivo: $macAddress\n\nPara renovar, acesse:\n$paymentUrl")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Tentar Novamente") { _, _ ->
                authenticateDevice()
            }
            .setNegativeButton("Sair") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    // Getters para acesso dos fragments
    fun getUserInfo(): UserInfo? = userInfo
    fun getServerInfo(): ServerInfo? = serverInfo
    fun getTenantId(): String = tenantId
}