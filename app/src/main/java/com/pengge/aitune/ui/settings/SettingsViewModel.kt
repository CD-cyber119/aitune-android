package com.pengge.aitune.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pengge.aitune.data.repository.SettingsRepository
import com.pengge.aitune.ui.theme.ThemeMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val deepSeekKey: String = "",
    val mimoKey: String = "",
    val ttsMode: String = "system",
    val djLanguage: String = "casual",
    val spotifyClientId: String = "",
    val spotifyClientSecret: String = "",
    val themeMode: String = "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    data class KeyState(
        val deepSeekKey: String?,
        val mimoKey: String?,
        val clientId: String?,
        val clientSecret: String?
    )

    private val keyState: Flow<KeyState> = combine(
        settingsRepository.deepSeekApiKey,
        settingsRepository.mimoApiKey,
        settingsRepository.spotifyClientId,
        settingsRepository.spotifyClientSecret
    ) { dk, mk, ci, cs ->
        KeyState(dk, mk, ci, cs)
    }

    val uiState: StateFlow<SettingsUiState> = combine(
        keyState,
        settingsRepository.ttsMode,
        settingsRepository.djLanguage,
        settingsRepository.themeMode
    ) { keys, tts, lang, theme ->
        SettingsUiState(
            deepSeekKey = keys.deepSeekKey ?: "",
            mimoKey = keys.mimoKey ?: "",
            ttsMode = tts,
            djLanguage = lang,
            spotifyClientId = keys.clientId ?: "",
            spotifyClientSecret = keys.clientSecret ?: "",
            themeMode = theme
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun saveDeepSeekKey(key: String) = viewModelScope.launch {
        settingsRepository.setDeepSeekApiKey(key)
    }

    fun saveMimoKey(key: String) = viewModelScope.launch {
        settingsRepository.setMimoApiKey(key)
    }

    fun saveTTSMode(mode: String) = viewModelScope.launch {
        settingsRepository.setTTSMode(mode)
    }

    fun saveDJLanguage(language: String) = viewModelScope.launch {
        settingsRepository.setDJLanguage(language)
    }

    fun saveSpotifyClientId(id: String) = viewModelScope.launch {
        settingsRepository.setSpotifyClientId(id)
    }

    fun saveSpotifyClientSecret(secret: String) = viewModelScope.launch {
        settingsRepository.setSpotifyClientSecret(secret)
    }

    fun saveThemeMode(mode: String) = viewModelScope.launch {
        settingsRepository.setThemeMode(mode)
    }
}
