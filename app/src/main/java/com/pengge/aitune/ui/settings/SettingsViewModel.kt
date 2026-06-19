package com.pengge.aitune.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pengge.aitune.data.repository.SettingsRepository
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
    val spotifyClientSecret: String = ""
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.deepSeekApiKey,
        settingsRepository.mimoApiKey,
        settingsRepository.ttsMode,
        settingsRepository.djLanguage,
        settingsRepository.spotifyClientId,
        settingsRepository.spotifyClientSecret
    ) { keys ->
        SettingsUiState(
            deepSeekKey = keys[0] ?: "",
            mimoKey = keys[1] ?: "",
            ttsMode = keys[2],
            djLanguage = keys[3],
            spotifyClientId = keys[4] ?: "",
            spotifyClientSecret = keys[5] ?: ""
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
}
