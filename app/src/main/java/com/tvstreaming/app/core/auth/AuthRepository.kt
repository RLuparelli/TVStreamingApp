package com.tvstreaming.app.core.auth

import com.tvstreaming.app.core.api.ApiService
import com.tvstreaming.app.core.storage.preferences.SecurePreferences
import com.tvstreaming.app.core.utils.Resource
import com.tvstreaming.app.core.utils.safeApiCall
import com.tvstreaming.app.core.utils.safeCall
import com.tvstreaming.app.models.AuthResponse
import com.tvstreaming.app.core.api.CodeAuthRequest
import com.tvstreaming.app.core.api.DeviceAuthRequest
import com.tvstreaming.app.models.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val macAddressManager: MacAddressManager,
    private val ownerConfigParser: OwnerConfigParser,
    private val securePreferences: SecurePreferences
) {

    /**
     * Autentica o dispositivo usando MAC address
     */
    suspend fun authenticateWithMac(ownerId: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val macAddress = macAddressManager.getMacAddress()
            val authResponse = generateMockAccess(macAddress, ownerId)
            Result.success(authResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Autentica usando código de acesso (mock)
     */
    suspend fun authenticateWithCodeMock(code: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            // TODO: Implementar autenticação real com código
            val macAddress = macAddressManager.getMacAddress()
            val authResponse = generateMockAccess(macAddress, "default")
            Result.success(authResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
     * Gera URL de renovação de pagamento
     */
    fun generateRenewalUrl(macAddress: String, tenantId: String): String {
        val cleanMac = macAddress.replace(":", "")
        return "https://payment.getteste.com/renew?device=$cleanMac&tenant=$tenantId"
    }

    /**
     * Gera acesso mock para desenvolvimento
     */
    private fun generateMockAccess(macAddress: String, ownerId: String): AuthResponse {
        val currentTime = System.currentTimeMillis() / 1000
        val expDate = currentTime + (24 * 60 * 60) // 24 horas

        // Gerar credenciais baseadas no MAC
        val cleanMac = macAddress.replace(":", "").replace("-", "")
        val username = "user_${cleanMac.takeLast(8)}"
        val password = "pass_${cleanMac.takeLast(6)}"

        val userInfo = UserInfo(
            username = username,
            password = password,
            message = macAddress,
            auth = 1,
            status = "Active",
            expDate = expDate.toString(),
            isTrial = "1",
            activeCons = 0,
            createdAt = currentTime.toString(),
            maxConnections = "1",
            allowedOutputFormats = listOf("m3u8", "ts", "rtmp")
        )

        val ownerConfig = ownerConfigParser.getDefaultConfig().copy(tenantId = ownerId)
        val serverInfo = ownerConfigParser.toServerInfo(ownerConfig)

        return AuthResponse(userInfo, serverInfo, "mock_token_${System.currentTimeMillis()}")
    }
    
    /**
     * Autentica o dispositivo usando a API
     */
    suspend fun authenticateDevice(): Resource<AuthResponse> {
        val deviceId = macAddressManager.getMacAddress()
        return safeCall {
            apiService.authenticateDevice(
                DeviceAuthRequest(
                    macAddress = deviceId,
                    deviceInfo = mapOf(
                        "deviceType" to if (securePreferences.isTV()) "android_tv" else "android_mobile",
                        "platform" to "android",
                        "appVersion" to "1.0.0" // TODO: Get from BuildConfig
                    ),
                    tenantId = null
                )
            )
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { response ->
                    // Save token on successful authentication
                    securePreferences.saveToken(response.token)
                }
            }
        }
    }
    
    /**
     * Autentica usando código
     */
    suspend fun authenticateWithCode(code: String): Resource<AuthResponse> {
        // For testing without server, use mock code "1234"
        if (code == "1234") {
            val mockResponse = generateMockAccess(
                macAddressManager.getMacAddress(),
                "test-tenant"
            )
            securePreferences.saveToken(mockResponse.token)
            return Resource.Success(mockResponse)
        }
        
        // Real API call
        val deviceId = macAddressManager.getMacAddress()
        return safeCall {
            apiService.authenticateCode(
                CodeAuthRequest(
                    code = code,
                    deviceInfo = mapOf(
                        "deviceId" to deviceId,
                        "platform" to "android"
                    )
                )
            )
        }.also { result ->
            if (result is Resource.Success) {
                result.data?.let { response ->
                    // Save token on successful authentication
                    securePreferences.saveToken(response.token)
                }
            }
        }
    }
    
    /**
     * Verifica se está autenticado
     */
    fun isAuthenticated(): Boolean {
        return try {
            securePreferences.getAuthToken() != null
        } catch (e: Exception) {
            Timber.e(e, "Error checking authentication status")
            false
        }
    }
}