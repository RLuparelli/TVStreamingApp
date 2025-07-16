package com.tvstreaming.app.core.storage.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        
        EncryptedSharedPreferences.create(
            "secure_prefs",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_TENANT_ID = "tenant_id"
        private const val KEY_DEVICE_ID = "device_id"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_LAST_MAC = "last_mac"
        private const val KEY_OWNER_CONFIG = "owner_config"
    }
    
    // Auth credentials
    fun saveCredentials(username: String, password: String) {
        encryptedPrefs.edit()
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .apply()
    }
    
    fun getCredentials(): Pair<String, String>? {
        val username = encryptedPrefs.getString(KEY_USERNAME, null)
        val password = encryptedPrefs.getString(KEY_PASSWORD, null)
        
        return if (username != null && password != null) {
            Pair(username, password)
        } else {
            null
        }
    }
    
    fun clearCredentials() {
        encryptedPrefs.edit()
            .remove(KEY_USERNAME)
            .remove(KEY_PASSWORD)
            .apply()
    }
    
    // Tokens
    fun saveToken(token: String) {
        encryptedPrefs.edit()
            .putString(KEY_TOKEN, token)
            .apply()
    }
    
    fun getToken(): String? {
        return encryptedPrefs.getString(KEY_TOKEN, null)
    }
    
    fun saveRefreshToken(refreshToken: String) {
        encryptedPrefs.edit()
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }
    
    fun getRefreshToken(): String? {
        return encryptedPrefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    fun clearTokens() {
        encryptedPrefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }
    
    // Tenant & Device
    fun saveTenantId(tenantId: String) {
        encryptedPrefs.edit()
            .putString(KEY_TENANT_ID, tenantId)
            .apply()
    }
    
    fun getTenantId(): String? {
        return encryptedPrefs.getString(KEY_TENANT_ID, null)
    }
    
    fun saveDeviceId(deviceId: String) {
        encryptedPrefs.edit()
            .putString(KEY_DEVICE_ID, deviceId)
            .apply()
    }
    
    fun getDeviceId(): String? {
        return encryptedPrefs.getString(KEY_DEVICE_ID, null)
    }
    
    fun saveLastMac(mac: String) {
        encryptedPrefs.edit()
            .putString(KEY_LAST_MAC, mac)
            .apply()
    }
    
    fun getLastMac(): String? {
        return encryptedPrefs.getString(KEY_LAST_MAC, null)
    }
    
    // App state
    fun setFirstLaunch(isFirst: Boolean) {
        encryptedPrefs.edit()
            .putBoolean(KEY_FIRST_LAUNCH, isFirst)
            .apply()
    }
    
    fun isFirstLaunch(): Boolean {
        return encryptedPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }
    
    // Owner config
    fun saveOwnerConfig(configJson: String) {
        encryptedPrefs.edit()
            .putString(KEY_OWNER_CONFIG, configJson)
            .apply()
    }
    
    fun getOwnerConfig(): String? {
        return encryptedPrefs.getString(KEY_OWNER_CONFIG, null)
    }
    
    // Clear all data
    fun clearAll() {
        encryptedPrefs.edit().clear().apply()
    }
    
    // Check if user is logged in
    fun isLoggedIn(): Boolean {
        return getCredentials() != null && getToken() != null
    }
}