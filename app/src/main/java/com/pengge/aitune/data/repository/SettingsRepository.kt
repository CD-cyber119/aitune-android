package com.pengge.aitune.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "aitune_settings")

/** 应用设置 — 使用 DataStore 持久化 API Key 等敏感信息 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_DEEPSEEK_API_KEY = stringPreferencesKey("deepseek_api_key")
        private val KEY_MIMO_API_KEY = stringPreferencesKey("mimo_api_key")
        private val KEY_MIMO_ENDPOINT = stringPreferencesKey("mimo_endpoint")
        private val KEY_SPOTIFY_CLIENT_ID = stringPreferencesKey("spotify_client_id")
        private val KEY_SPOTIFY_CLIENT_SECRET = stringPreferencesKey("spotify_client_secret")
        private val KEY_TTS_MODE = stringPreferencesKey("tts_mode")
        private val KEY_DJ_LANGUAGE = stringPreferencesKey("dj_language")
        private val KEY_SELECTED_PLAYLIST_ID = stringPreferencesKey("selected_playlist_id")
        private val KEY_ONBOARDING_DONE = stringPreferencesKey("onboarding_done")
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }

    // --- DeepSeek ---
    val deepSeekApiKey: Flow<String?> = context.settingsDataStore.data
        .map { it[KEY_DEEPSEEK_API_KEY] }

    suspend fun setDeepSeekApiKey(key: String) {
        context.settingsDataStore.edit { it[KEY_DEEPSEEK_API_KEY] = key }
    }

    // --- MiMo TTS ---
    val mimoApiKey: Flow<String?> = context.settingsDataStore.data
        .map { it[KEY_MIMO_API_KEY] }

    suspend fun setMimoApiKey(key: String) {
        context.settingsDataStore.edit { it[KEY_MIMO_API_KEY] = key }
    }

    val mimoEndpoint: Flow<String> = context.settingsDataStore.data
        .map { it[KEY_MIMO_ENDPOINT] ?: "https://api.xiaomimimo.com/v1" }

    suspend fun setMimoEndpoint(endpoint: String) {
        context.settingsDataStore.edit { it[KEY_MIMO_ENDPOINT] = endpoint }
    }

    // --- Spotify ---
    val spotifyClientId: Flow<String?> = context.settingsDataStore.data
        .map { it[KEY_SPOTIFY_CLIENT_ID] }

    suspend fun setSpotifyClientId(id: String) {
        context.settingsDataStore.edit { it[KEY_SPOTIFY_CLIENT_ID] = id }
    }

    val spotifyClientSecret: Flow<String?> = context.settingsDataStore.data
        .map { it[KEY_SPOTIFY_CLIENT_SECRET] }

    suspend fun setSpotifyClientSecret(secret: String) {
        context.settingsDataStore.edit { it[KEY_SPOTIFY_CLIENT_SECRET] = secret }
    }

    // --- TTS 模式 ---
    val ttsMode: Flow<String> = context.settingsDataStore.data
        .map { it[KEY_TTS_MODE] ?: "system" }

    suspend fun setTTSMode(mode: String) {
        context.settingsDataStore.edit { it[KEY_TTS_MODE] = mode }
    }

    // --- DJ 语体 ---
    val djLanguage: Flow<String> = context.settingsDataStore.data
        .map { it[KEY_DJ_LANGUAGE] ?: "casual" }

    suspend fun setDJLanguage(language: String) {
        context.settingsDataStore.edit { it[KEY_DJ_LANGUAGE] = language }
    }

    // --- 选中的歌单 ---
    val selectedPlaylistId: Flow<String?> = context.settingsDataStore.data
        .map { it[KEY_SELECTED_PLAYLIST_ID] }

    suspend fun setSelectedPlaylistId(id: String) {
        context.settingsDataStore.edit { it[KEY_SELECTED_PLAYLIST_ID] = id }
    }

    // --- 引导完成 ---
    val onboardingDone: Flow<Boolean> = context.settingsDataStore.data
        .map { it[KEY_ONBOARDING_DONE] == "true" }

    suspend fun setOnboardingDone(done: Boolean) {
        context.settingsDataStore.edit { it[KEY_ONBOARDING_DONE] = done.toString() }
    }

    // --- 主题模式 ---
    val themeMode: Flow<String> = context.settingsDataStore.data
        .map { it[KEY_THEME_MODE] ?: "system" }

    suspend fun setThemeMode(mode: String) {
        context.settingsDataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    // --- 便捷读取（非 Flow） ---
    suspend fun getDeepSeekApiKeySuspended(): String? = deepSeekApiKey.first()
    suspend fun getMimoApiKeySuspended(): String? = mimoApiKey.first()
    suspend fun getMimoEndpointSuspended(): String = mimoEndpoint.first()
    suspend fun isOnboardingDone(): Boolean = onboardingDone.first()
}
