package com.pengge.aitune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.pengge.aitune.data.repository.SettingsRepository
import com.pengge.aitune.ui.navigation.AituneNavGraph
import com.pengge.aitune.ui.theme.AituneTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AituneTheme {
                var showOnboarding by remember { mutableStateOf(true) }

                // 检查是否已完成引导
                LaunchedEffect(Unit) {
                    showOnboarding = !settingsRepository.isOnboardingDone()
                }

                AituneNavGraph(
                    showOnboarding = showOnboarding,
                    onOnboardingDone = {
                        showOnboarding = false
                    }
                )
            }
        }
    }
}
