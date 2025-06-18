package com.tvstreaming.app.network

import android.content.Context
import com.google.gson.Gson
import com.tvstreaming.app.models.*
import com.tvstreaming.app.utils.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ApiManager private constructor(private val context: Context) {

    private val gson = Gson()

    companion object {
        @Volatile
        private var INSTANCE: ApiManager? = null

        fun getInstance(context: Context): ApiManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ApiManager(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val PREFS_KEY_FIRST_ACCESS = "first_access_"
        private const val PREFS_KEY_LAST_MAC = "last_mac_address"
    }

    /**
     * Autentica o usuário usando o MAC address do dispositivo
     */
    suspend fun authenticateDevice(macAddress: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val authResponse = generateMockAccess(macAddress)
            Result.success(authResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Gera acesso mock com base no MAC address
     */
    private fun generateMockAccess(macAddress: String): AuthResponse {
        val prefs = context.getSharedPreferences("tv_streaming_access", Context.MODE_PRIVATE)
        val currentTime = System.currentTimeMillis() / 1000

        // Chave única para este MAC
        val accessKey = "$PREFS_KEY_FIRST_ACCESS$macAddress"

        // Verificar se é o primeiro acesso para este MAC
        val firstAccess = prefs.getLong(accessKey, 0)
        val lastMac = prefs.getString(PREFS_KEY_LAST_MAC, "")

        val (expDate, isNewDevice) = if (firstAccess == 0L || lastMac != macAddress) {
            // Primeiro acesso ou MAC diferente = dar 1 dia
            val newExpDate = currentTime + (24 * 60 * 60) // 24 horas

            // Salvar primeiro acesso
            prefs.edit()
                .putLong(accessKey, currentTime)
                .putString(PREFS_KEY_LAST_MAC, macAddress)
                .apply()

            Pair(newExpDate, true)
        } else {
            // Acesso já existente = verificar se ainda é válido
            val savedExpDate = prefs.getLong("exp_date_$macAddress", currentTime + (24 * 60 * 60))
            Pair(savedExpDate, false)
        }

        // Salvar data de expiração
        prefs.edit().putLong("exp_date_$macAddress", expDate).apply()

        // Status baseado na expiração
        val isExpired = currentTime >= expDate
        val status = if (isExpired) "Expired" else "Active"

        // Gerar username baseado no MAC
        val cleanMac = macAddress.replace(":", "").replace("-", "")
        val username = "user_${cleanMac.takeLast(8)}"
        val password = "pass_${cleanMac.takeLast(6)}"

        val userInfo = UserInfo(
            username = username,
            password = password,
            message = macAddress,
            auth = if (isExpired) 0 else 1,
            status = status,
            expDate = expDate.toString(),
            isTrial = if (isNewDevice) "1" else "0",
            activeCons = 0,
            createdAt = firstAccess.toString(),
            maxConnections = "1",
            allowedOutputFormats = listOf("m3u8", "ts", "rtmp")
        )

        val serverInfo = ServerInfo(
            xui = true,
            version = "1.5.5",
            revision = 2,
            url = "pfsv.io",
            port = "80",
            httpsPort = null,
            serverProtocol = "http",
            rtmpPort = "8880",
            timestampNow = currentTime,
            timeNow = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            timezone = "UTC",
            tenantId = "fasted"
        )

        return AuthResponse(userInfo, serverInfo)
    }

    /**
     * Verifica se a assinatura está expirada
     */
    fun isSubscriptionExpired(userInfo: UserInfo): Boolean {
        return try {
            val expTimestamp = userInfo.expDate.toLong()
            val currentTimestamp = System.currentTimeMillis() / 1000
            currentTimestamp >= expTimestamp
        } catch (e: Exception) {
            true
        }
    }

    /**
     * Gera URL de pagamento para renovação
     */
    fun generatePaymentUrl(macAddress: String): String {
        return "https://payment.getteste.com/renew?device=${macAddress.replace(":", "")}"
    }

    /**
     * Limpa dados de acesso (para testes)
     */
    fun clearAccessData() {
        val prefs = context.getSharedPreferences("tv_streaming_access", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}