package com.tvstreaming.app.core.storage.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val dataStore = context.dataStore
    
    companion object {
        // App settings
        val THEME_KEY = stringPreferencesKey("theme_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val AUTOPLAY_KEY = booleanPreferencesKey("autoplay_enabled")
        val QUALITY_KEY = stringPreferencesKey("video_quality")
        val SUBTITLES_KEY = booleanPreferencesKey("subtitles_enabled")
        val SUBTITLE_LANGUAGE_KEY = stringPreferencesKey("subtitle_language")
        
        // Player settings
        val PLAYER_CONTROLS_TIMEOUT = intPreferencesKey("player_controls_timeout")
        val SKIP_INTRO_KEY = booleanPreferencesKey("skip_intro")
        val CONTINUE_WATCHING_KEY = booleanPreferencesKey("continue_watching")
        val PLAYBACK_SPEED_KEY = floatPreferencesKey("playback_speed")
        
        // Network settings
        val CACHE_SIZE_KEY = longPreferencesKey("cache_size")
        val DOWNLOAD_QUALITY_KEY = stringPreferencesKey("download_quality")
        val CELLULAR_DATA_KEY = booleanPreferencesKey("cellular_data_enabled")
        
        // UI settings
        val GRID_COLUMNS_KEY = intPreferencesKey("grid_columns")
        val SHOW_ADULT_CONTENT_KEY = booleanPreferencesKey("show_adult_content")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        val PARENTAL_LOCK_KEY = booleanPreferencesKey("parental_lock_enabled")
        
        // Default values
        const val DEFAULT_THEME = "system"
        const val DEFAULT_LANGUAGE = "pt"
        const val DEFAULT_AUTOPLAY = true
        const val DEFAULT_QUALITY = "auto"
        const val DEFAULT_SUBTITLES = false
        const val DEFAULT_SUBTITLE_LANGUAGE = "pt"
        const val DEFAULT_CONTROLS_TIMEOUT = 3000
        const val DEFAULT_SKIP_INTRO = false
        const val DEFAULT_CONTINUE_WATCHING = true
        const val DEFAULT_PLAYBACK_SPEED = 1.0f
        const val DEFAULT_CACHE_SIZE = 500L * 1024 * 1024 // 500MB
        const val DEFAULT_DOWNLOAD_QUALITY = "medium"
        const val DEFAULT_CELLULAR_DATA = true
        const val DEFAULT_GRID_COLUMNS = 2
        const val DEFAULT_SHOW_ADULT_CONTENT = false
        const val DEFAULT_NOTIFICATIONS = true
        const val DEFAULT_PARENTAL_LOCK = false
    }
    
    // App settings
    val themeMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[THEME_KEY] ?: DEFAULT_THEME
    }
    
    suspend fun setThemeMode(theme: String) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme
        }
    }
    
    val language: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: DEFAULT_LANGUAGE
    }
    
    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }
    
    val autoplayEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTOPLAY_KEY] ?: DEFAULT_AUTOPLAY
    }
    
    suspend fun setAutoplayEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTOPLAY_KEY] = enabled
        }
    }
    
    val videoQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[QUALITY_KEY] ?: DEFAULT_QUALITY
    }
    
    suspend fun setVideoQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[QUALITY_KEY] = quality
        }
    }
    
    val subtitlesEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SUBTITLES_KEY] ?: DEFAULT_SUBTITLES
    }
    
    suspend fun setSubtitlesEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SUBTITLES_KEY] = enabled
        }
    }
    
    val subtitleLanguage: Flow<String> = dataStore.data.map { preferences ->
        preferences[SUBTITLE_LANGUAGE_KEY] ?: DEFAULT_SUBTITLE_LANGUAGE
    }
    
    suspend fun setSubtitleLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[SUBTITLE_LANGUAGE_KEY] = language
        }
    }
    
    // Player settings
    val playerControlsTimeout: Flow<Int> = dataStore.data.map { preferences ->
        preferences[PLAYER_CONTROLS_TIMEOUT] ?: DEFAULT_CONTROLS_TIMEOUT
    }
    
    suspend fun setPlayerControlsTimeout(timeout: Int) {
        dataStore.edit { preferences ->
            preferences[PLAYER_CONTROLS_TIMEOUT] = timeout
        }
    }
    
    val skipIntro: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SKIP_INTRO_KEY] ?: DEFAULT_SKIP_INTRO
    }
    
    suspend fun setSkipIntro(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SKIP_INTRO_KEY] = enabled
        }
    }
    
    val continueWatching: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CONTINUE_WATCHING_KEY] ?: DEFAULT_CONTINUE_WATCHING
    }
    
    suspend fun setContinueWatching(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CONTINUE_WATCHING_KEY] = enabled
        }
    }
    
    val playbackSpeed: Flow<Float> = dataStore.data.map { preferences ->
        preferences[PLAYBACK_SPEED_KEY] ?: DEFAULT_PLAYBACK_SPEED
    }
    
    suspend fun setPlaybackSpeed(speed: Float) {
        dataStore.edit { preferences ->
            preferences[PLAYBACK_SPEED_KEY] = speed
        }
    }
    
    // Network settings
    val cacheSize: Flow<Long> = dataStore.data.map { preferences ->
        preferences[CACHE_SIZE_KEY] ?: DEFAULT_CACHE_SIZE
    }
    
    suspend fun setCacheSize(size: Long) {
        dataStore.edit { preferences ->
            preferences[CACHE_SIZE_KEY] = size
        }
    }
    
    val downloadQuality: Flow<String> = dataStore.data.map { preferences ->
        preferences[DOWNLOAD_QUALITY_KEY] ?: DEFAULT_DOWNLOAD_QUALITY
    }
    
    suspend fun setDownloadQuality(quality: String) {
        dataStore.edit { preferences ->
            preferences[DOWNLOAD_QUALITY_KEY] = quality
        }
    }
    
    val cellularDataEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[CELLULAR_DATA_KEY] ?: DEFAULT_CELLULAR_DATA
    }
    
    suspend fun setCellularDataEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[CELLULAR_DATA_KEY] = enabled
        }
    }
    
    // UI settings
    val gridColumns: Flow<Int> = dataStore.data.map { preferences ->
        preferences[GRID_COLUMNS_KEY] ?: DEFAULT_GRID_COLUMNS
    }
    
    suspend fun setGridColumns(columns: Int) {
        dataStore.edit { preferences ->
            preferences[GRID_COLUMNS_KEY] = columns
        }
    }
    
    val showAdultContent: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SHOW_ADULT_CONTENT_KEY] ?: DEFAULT_SHOW_ADULT_CONTENT
    }
    
    suspend fun setShowAdultContent(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOW_ADULT_CONTENT_KEY] = enabled
        }
    }
    
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_KEY] ?: DEFAULT_NOTIFICATIONS
    }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_KEY] = enabled
        }
    }
    
    val parentalLockEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PARENTAL_LOCK_KEY] ?: DEFAULT_PARENTAL_LOCK
    }
    
    suspend fun setParentalLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PARENTAL_LOCK_KEY] = enabled
        }
    }
    
    // Clear all preferences
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}