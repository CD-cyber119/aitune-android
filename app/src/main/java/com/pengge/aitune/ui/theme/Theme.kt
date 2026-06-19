package com.pengge.aitune.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/** 主题模式 */
enum class ThemeMode {
    SYSTEM,     // 跟随系统
    LIGHT,      // 橘子海
    DARK        // 深夜电台
}

// ===================== 浅色方案：橘子海 =====================
private val LightColorScheme = lightColorScheme(
    primary = OrangeSea.Primary,
    onPrimary = OrangeSea.OnPrimary,
    primaryContainer = OrangeSea.SurfaceVariant,
    secondary = OrangeSea.Secondary,
    onSecondary = OrangeSea.OnSecondary,
    tertiary = OrangeSea.Tertiary,
    background = OrangeSea.Background,
    onBackground = OrangeSea.OnBackground,
    surface = OrangeSea.Surface,
    onSurface = OrangeSea.OnSurface,
    surfaceVariant = OrangeSea.SurfaceVariant,
    onSurfaceVariant = OrangeSea.TextSecondary,
    error = OrangeSea.StateError,
    outline = OrangeSea.TextMuted
)

// ===================== 深色方案：深夜电台 =====================
private val DarkColorScheme = darkColorScheme(
    primary = MidnightRadio.Primary,
    onPrimary = MidnightRadio.OnPrimary,
    primaryContainer = MidnightRadio.SurfaceVariant,
    secondary = MidnightRadio.Secondary,
    onSecondary = MidnightRadio.OnSecondary,
    tertiary = MidnightRadio.Tertiary,
    background = MidnightRadio.Background,
    onBackground = MidnightRadio.OnBackground,
    surface = MidnightRadio.Surface,
    onSurface = MidnightRadio.OnSurface,
    surfaceVariant = MidnightRadio.SurfaceVariant,
    onSurfaceVariant = MidnightRadio.TextSecondary,
    error = MidnightRadio.StateError,
    outline = MidnightRadio.TextMuted
)

/**
 * Aitune 主题
 *
 * @param themeMode 主题模式：SYSTEM / LIGHT / DARK
 */
@Composable
fun AituneTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    // 设置状态栏颜色
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
