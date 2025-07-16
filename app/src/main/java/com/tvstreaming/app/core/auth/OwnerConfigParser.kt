package com.tvstreaming.app.core.auth

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tvstreaming.app.models.ServerInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerConfigParser @Inject constructor(
    private val gson: Gson
) {

    /**
     * Configuração do proprietário/tenant
     */
    data class OwnerConfig(
        val tenantId: String,
        val serverUrl: String,
        val serverPort: String = "80",
        val httpsPort: String? = null,
        val protocol: String = "http",
        val brandName: String = "STREAMING",
        val logoUrl: String? = null,
        val primaryColor: String = "#1976D2",
        val secondaryColor: String = "#424242",
        val features: OwnerFeatures = OwnerFeatures()
    )

    /**
     * Features habilitadas para o tenant
     */
    data class OwnerFeatures(
        val enablePaymentQr: Boolean = true,
        val enableSmsPayment: Boolean = false,
        val enableChromecast: Boolean = true,
        val enableDownloads: Boolean = false,
        val enableProfiles: Boolean = false,
        val maxDevices: Int = 1,
        val trialDays: Int = 1
    )

    /**
     * Faz parse da configuração JSON do proprietário
     */
    fun parseOwnerConfig(json: String): Result<OwnerConfig> {
        return try {
            val config = gson.fromJson(json, OwnerConfig::class.java)
            if (validateConfig(config)) {
                Result.success(config)
            } else {
                Result.failure(IllegalArgumentException("Invalid owner configuration"))
            }
        } catch (e: JsonSyntaxException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida a configuração do proprietário
     */
    fun validateConfig(config: OwnerConfig): Boolean {
        return when {
            config.tenantId.isBlank() -> false
            config.serverUrl.isBlank() -> false
            config.serverPort.isBlank() -> false
            config.brandName.isBlank() -> false
            !isValidUrl(config.serverUrl) -> false
            !isValidPort(config.serverPort) -> false
            config.httpsPort != null && !isValidPort(config.httpsPort) -> false
            config.protocol !in listOf("http", "https") -> false
            config.features.maxDevices < 1 -> false
            config.features.trialDays < 0 -> false
            else -> true
        }
    }

    /**
     * Gera configuração padrão para desenvolvimento
     */
    fun getDefaultConfig(): OwnerConfig {
        return OwnerConfig(
            tenantId = "default",
            serverUrl = "pfsv.io",
            serverPort = "80",
            httpsPort = null,
            protocol = "http",
            brandName = "STREAMING",
            logoUrl = null,
            primaryColor = "#1976D2",
            secondaryColor = "#424242",
            features = OwnerFeatures(
                enablePaymentQr = true,
                enableSmsPayment = false,
                enableChromecast = true,
                enableDownloads = false,
                enableProfiles = false,
                maxDevices = 1,
                trialDays = 1
            )
        )
    }

    /**
     * Converte OwnerConfig para ServerInfo
     */
    fun toServerInfo(config: OwnerConfig): ServerInfo {
        return ServerInfo(
            xui = true,
            version = "1.5.5",
            revision = 2,
            url = config.serverUrl,
            port = config.serverPort,
            httpsPort = config.httpsPort,
            serverProtocol = config.protocol,
            rtmpPort = "8880",
            timestampNow = System.currentTimeMillis() / 1000,
            timeNow = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()),
            timezone = "UTC",
            tenantId = config.tenantId
        )
    }

    /**
     * Serializa configuração para JSON
     */
    fun configToJson(config: OwnerConfig): String {
        return gson.toJson(config)
    }

    private fun isValidUrl(url: String): Boolean {
        return url.matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()) ||
               url.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$".toRegex())
    }

    private fun isValidPort(port: String): Boolean {
        return try {
            val portNumber = port.toInt()
            portNumber in 1..65535
        } catch (e: NumberFormatException) {
            false
        }
    }
}