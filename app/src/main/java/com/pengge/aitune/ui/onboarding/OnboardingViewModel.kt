package com.pengge.aitune.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pengge.aitune.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    suspend fun isOnboardingComplete(): Boolean = settingsRepository.isOnboardingDone()

    fun completeOnboarding() {
        viewModelScope.launch {
            settingsRepository.setOnboardingDone(true)
        }
    }
}
