package com.tvstreaming.app.core.auth

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.text.TextUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MacAddressManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Obtém o endereço MAC do dispositivo
     */
    fun getMacAddress(): String {
        // Método 1: Usar WifiManager se conectado via Wi-Fi (API >= 23)
        if (isWifiConnected()) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Para Android 6.0+ o MAC address via WifiInfo retorna um MAC genérico
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
        }

        // Método 2: Procurar por interfaces de rede comuns
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in Collections.list(networkInterfaces)) {
                if (networkInterface.name.equals("wlan0", ignoreCase = true) ||
                    networkInterface.name.equals("eth0", ignoreCase = true)) {
                    val mac = networkInterface.hardwareAddress
                    if (mac != null) {
                        val macString = mac.joinToString(separator = ":") { 
                            String.format("%02x", it) 
                        }
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
                    val macString = mac.joinToString(separator = ":") { 
                        String.format("%02x", it) 
                    }
                    if (macString != "00:00:00:00:00:00") {
                        return macString
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Método 4: Fallback para MAC simulado
        return generateSimulatedMac()
    }

    /**
     * Valida se o MAC address está no formato correto
     */
    fun validateMac(mac: String): Boolean {
        if (mac.isBlank()) return false
        
        val macRegex = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$"
        return mac.matches(macRegex.toRegex())
    }

    /**
     * Formata o MAC address para o padrão xx:xx:xx:xx:xx:xx
     */
    fun formatMac(mac: String): String {
        val cleanMac = mac.replace("[:-]".toRegex(), "")
        if (cleanMac.length != 12) return mac
        
        return cleanMac.chunked(2).joinToString(":")
    }

    /**
     * Gera um identificador único baseado no dispositivo
     */
    fun getDeviceId(): String {
        return try {
            val androidId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )

            if (androidId != null && androidId.isNotEmpty() && androidId != "9774d56d682e549c") {
                val id = androidId.padEnd(12, '0').substring(0, 12)
                "${id.substring(0,2)}:${id.substring(2,4)}:${id.substring(4,6)}:" +
                        "${id.substring(6,8)}:${id.substring(8,10)}:${id.substring(10,12)}"
            } else {
                generateSimulatedMac()
            }
        } catch (e: Exception) {
            generateSimulatedMac()
        }
    }

    private fun isWifiConnected(): Boolean {
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

    private fun generateSimulatedMac(): String {
        val deviceInfo = "${Build.MANUFACTURER}_${Build.MODEL}_${Build.BOARD}_${Build.FINGERPRINT}"
        val md5 = generateMD5(deviceInfo)
        val mac = md5.substring(0, 12)
        
        return "${mac.substring(0,2)}:${mac.substring(2,4)}:${mac.substring(4,6)}:" +
                "${mac.substring(6,8)}:${mac.substring(8,10)}:${mac.substring(10,12)}"
    }

    private fun generateMD5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            input.hashCode().toString().padStart(32, '0').substring(0, 32)
        }
    }
}