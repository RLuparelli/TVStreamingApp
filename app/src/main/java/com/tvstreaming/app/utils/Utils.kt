package com.tvstreaming.app.utils

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import java.security.MessageDigest
import java.net.NetworkInterface
import android.text.TextUtils

object AppUtils {

    private const val PREFS_NAME = "tv_streaming_prefs"
    private const val KEY_USER_DATA = "user_data"
    private const val KEY_SERVER_DATA = "server_data"
    private const val KEY_TENANT_ID = "tenant_id"
    private const val KEY_LAST_LOGIN = "last_login"

    /**
     * Salva dados do usuário nas preferências
     */
    fun saveUserData(context: Context, userData: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(KEY_USER_DATA, userData).apply()
    }

    /**
     * Recupera dados do usuário das preferências
     */
    fun getUserData(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_USER_DATA, null)
    }

    /**
     * Salva tenant ID
     */
    fun saveTenantId(context: Context, tenantId: String) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putString(KEY_TENANT_ID, tenantId).apply()
    }

    /**
     * Recupera tenant ID
     */
    fun getTenantId(context: Context): String? {
        val prefs = getSharedPreferences(context)
        return prefs.getString(KEY_TENANT_ID, null)
    }

    /**
     * Limpa todas as preferências
     */
    fun clearAllData(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().clear().apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Verifica se há conexão com a internet
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.isConnected == true
        }
    }

    /**
     * Obtém identificador único do dispositivo
     */
    fun getDeviceId(context: Context): String {
        return try {
            // Usar Android ID (funciona sempre)
            val androidId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )

            if (androidId != null && androidId.isNotEmpty() && androidId != "9774d56d682e549c") {
                // Formatar Android ID como MAC address
                val id = androidId.padEnd(12, '0').substring(0, 12)
                "${id.substring(0,2)}:${id.substring(2,4)}:${id.substring(4,6)}:" +
                        "${id.substring(6,8)}:${id.substring(8,10)}:${id.substring(10,12)}"
            } else {
                // Fallback: gerar baseado em características do dispositivo
                generateDeviceBasedId()
            }
        } catch (e: Exception) {
            generateDeviceBasedId()
        }
    }

    /**
     * Gera ID baseado em características únicas do dispositivo
     */
    private fun generateDeviceBasedId(): String {
        val deviceInfo = getDeviceInfo()
        val serialNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                Build.getSerial()
            } catch (e: SecurityException) {
                "unknown"
            }
        } else {
            @Suppress("DEPRECATION")
            Build.SERIAL
        }
        
        val uniqueString = "${deviceInfo["manufacturer"]}" +
                "${deviceInfo["model"]}" +
                "${deviceInfo["brand"]}" +
                "${deviceInfo["board"]}" +
                "${deviceInfo["device"]}" +
                "${serialNumber}".take(8)

        // Gerar hash e formatar como MAC
        val hash = generateMD5(uniqueString)
        val mac = hash.substring(0, 12)

        return "${mac.substring(0,2)}:${mac.substring(2,4)}:${mac.substring(4,6)}:" +
                "${mac.substring(6,8)}:${mac.substring(8,10)}:${mac.substring(10,12)}"
    }

    /**
     * Obtém endereço MAC do dispositivo
     */
    fun getMacAddress(context: Context): String {
        // Método 1: Usar WifiManager se conectado via Wi-Fi (com tratamento de API deprecada)
        if (isWifiConnected(context)) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // A partir do Android 6.0, o MAC address via WifiInfo retorna um valor genérico
                // por questões de privacidade, então usamos outros métodos
            } else {
                @Suppress("DEPRECATION")
                val wifiInfo = wifiManager.connectionInfo
                if (wifiInfo != null) {
                    @Suppress("DEPRECATION")
                    val macAddress = wifiInfo.macAddress
                if (!TextUtils.isEmpty(macAddress) && macAddress != "02:00:00:00:00:00") {
                    return macAddress
                }
            }
        }

        // Método 2: Procurar por interfaces de rede comuns (wlan0, eth0)
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in Collections.list(networkInterfaces)) {
                if (networkInterface.name.equals("wlan0", ignoreCase = true) ||
                    networkInterface.name.equals("eth0", ignoreCase = true)) {
                    val mac = networkInterface.hardwareAddress
                    if (mac != null) {
                        val macString = mac.joinToString(separator = ":") { String.format("%02x", it) }
                        if (macString != "00:00:00:00:00:00") {
                            return macString
                        }
                    }
                }
            }

            // Método 3: Qualquer interface com endereço de hardware
            for (networkInterface in Collections.list(networkInterfaces)) {
                if (networkInterface.hardwareAddress != null) {
                    val mac = networkInterface.hardwareAddress
                    val macString = mac.joinToString(separator = ":") { String.format("%02x", it) }
                    if (macString != "00:00:00:00:00:00") {
                        return macString
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Método 4: Fallback para um MAC simulado
        return generateSimulatedMac()
    }

    /**
     * Verifica se o dispositivo está conectado via Wi-Fi
     */
    private fun isWifiConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            networkInfo?.type == ConnectivityManager.TYPE_WIFI
        }
    }

    /**
     * Gera MAC simulado baseado em características únicas do dispositivo
     */
    private fun generateSimulatedMac(): String {
        val deviceInfo = getDeviceInfo()
        val uniqueString = "${deviceInfo["manufacturer"]}_${deviceInfo["model"]}_${deviceInfo["board"]}_${Build.FINGERPRINT}"

        // Gerar hash MD5 e usar os primeiros 12 caracteres
        val md5 = generateMD5(uniqueString)
        val mac = md5.substring(0, 12)

        // Formatar como MAC address
        return "${mac.substring(0,2)}:${mac.substring(2,4)}:${mac.substring(4,6)}:" +
                "${mac.substring(6,8)}:${mac.substring(8,10)}:${mac.substring(10,12)}"
    }

    /**
     * Gera hash MD5 de uma string
     */
    private fun generateMD5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback se MD5 falhar
            input.hashCode().toString().padStart(32, '0').substring(0, 32)
        }
    }

    /**
     * Formata timestamp para data legível
     */
    fun formatTimestamp(timestamp: Long, pattern: String = "dd/MM/yyyy HH:mm"): String {
        val date = Date(timestamp * 1000) // Assumindo timestamp em segundos
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }

    /**
     * Mostra toast de forma segura
     */
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        try {
            Toast.makeText(context, message, duration).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Verifica se é uma TV/Android TV
     */
    fun isAndroidTV(context: Context): Boolean {
        return context.packageManager.hasSystemFeature("android.software.leanback")
    }

    /**
     * Obtém informações do dispositivo
     */
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "version" to Build.VERSION.RELEASE,
            "sdk" to Build.VERSION.SDK_INT.toString(),
            "board" to Build.BOARD,
            "brand" to Build.BRAND,
            "device" to Build.DEVICE,
            "product" to Build.PRODUCT,
            "fingerprint" to Build.FINGERPRINT.substring(0, minOf(50, Build.FINGERPRINT.length))
        )
    }

    /**
     * Formata duração em segundos para formato legível
     */
    fun formatDuration(durationInSeconds: Int): String {
        val hours = durationInSeconds / 3600
        val minutes = (durationInSeconds % 3600) / 60
        val seconds = durationInSeconds % 60

        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%d:%02d", minutes, seconds)
            else -> String.format("0:%02d", seconds)
        }
    }

    /**
     * Verifica se uma URL é válida
     */
    fun isValidUrl(url: String): Boolean {
        return try {
            // Verificação simples sem regex complexa
            when {
                url.startsWith("http://") -> true
                url.startsWith("https://") -> true
                url.startsWith("ftp://") -> true
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Calcula progresso em porcentagem
     */
    fun calculateProgress(current: Long, total: Long): Int {
        return if (total > 0) {
            ((current * 100) / total).toInt()
        } else {
            0
        }
    }
}