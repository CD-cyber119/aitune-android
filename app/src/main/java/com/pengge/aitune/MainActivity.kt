package com.pengge.aitune

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.pengge.aitune.data.repository.SettingsRepository
import com.pengge.aitune.ui.navigation.AituneNavGraph
import com.pengge.aitune.ui.theme.AituneTheme
import com.pengge.aitune.ui.theme.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            var showOnboarding by remember { mutableStateOf(true) }
            var themeMode by remember { mutableStateOf(ThemeMode.SYSTEM) }

            // 检查引导状态 & 主题设置
            LaunchedEffect(Unit) {
                showOnboarding = !settingsRepository.isOnboardingDone()
                val savedMode = settingsRepository.themeMode.first()
                themeMode = when (savedMode) {
                    "light" -> ThemeMode.LIGHT
                    "dark" -> ThemeMode.DARK
                    else -> ThemeMode.SYSTEM
                }
            }

            AituneTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                AituneNavGraph(
                    navController = navController,
                    showOnboarding = showOnboarding,
                    onOnboardingDone = {
                        showOnboarding = false
                    },
                    currentThemeMode = themeMode,
                    onThemeChanged = { newMode ->
                        themeMode = newMode
                    }
                )
            }
        }
    }
}
