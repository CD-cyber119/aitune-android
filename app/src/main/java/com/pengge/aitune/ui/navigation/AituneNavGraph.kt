package com.pengge.aitune.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pengge.aitune.ui.theme.ThemeMode
import com.pengge.aitune.ui.nowplaying.NowPlayingScreen
import com.pengge.aitune.ui.onboarding.OnboardingScreen
import com.pengge.aitune.ui.settings.SettingsScreen

@Composable
fun AituneNavGraph(
    navController: NavHostController,
    showOnboarding: Boolean,
    onOnboardingDone: () -> Unit,
    currentThemeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeChanged: (ThemeMode) -> Unit = {}
) {
    val startDestination = if (showOnboarding) Routes.ONBOARDING else Routes.NOW_PLAYING

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onComplete = {
                    onOnboardingDone()
                    navController.navigate(Routes.NOW_PLAYING) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.NOW_PLAYING) {
            NowPlayingScreen(
                onOpenSettings = {
                    navController.navigate(Routes.SETTINGS)
                },
                currentThemeMode = currentThemeMode,
                onThemeChanged = onThemeChanged
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                currentThemeMode = currentThemeMode,
                onThemeChanged = onThemeChanged
            )
        }
    }
}
