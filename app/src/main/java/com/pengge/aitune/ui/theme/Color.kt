package com.pengge.aitune.ui.theme

import androidx.compose.ui.graphics.Color

// ===================== 橘子海 · 浅色模式 =====================
object OrangeSea {
    val Primary = Color(0xFFFF6B35)        // 鲜艳橘橙
    val PrimaryVariant = Color(0xFFE85D26) // 深橘色
    val Secondary = Color(0xFFFF8FA3)      // 暖粉色
    val Tertiary = Color(0xFFC5A3FF)       // 淡紫色

    val Background = Color(0xFFFFF8F0)     // 暖白
    val Surface = Color(0xFFFFFDF5)        // 更白的表面
    val SurfaceVariant = Color(0xFFFFECD2) // 浅杏色

    val TextPrimary = Color(0xFF2D1B00)    // 深棕
    val TextSecondary = Color(0xFF7A6A5A)  // 暖灰
    val TextMuted = Color(0xFFB0A090)      // 浅灰

    val OnPrimary = Color(0xFFFFFFFF)
    val OnSecondary = Color(0xFF2D1B00)
    val OnBackground = Color(0xFF2D1B00)
    val OnSurface = Color(0xFF2D1B00)

    // 状态指示
    val StatePlaying = Color(0xFFFF6B35)
    val StateLoading = Color(0xFFFFA500)
    val StateSpeaking = Color(0xFFC5A3FF)
    val StateError = Color(0xFFFF4444)

    // 渐变色（用于封面边框等）
    val GradientStart = Color(0xFFFF6B35)
    val GradientMid = Color(0xFFFF8FA3)
    val GradientEnd = Color(0xFFC5A3FF)

    // DJ 串词背景
    val DJBubbleBg = Color(0xFFFFF0E0)
    val DJBubbleText = Color(0xFF5A4A3A)
}

// ===================== 深夜电台 · 深色模式 =====================
object MidnightRadio {
    val Primary = Color(0xFFFF8C42)        // 暗橘色点缀
    val PrimaryVariant = Color(0xFFE07030)
    val Secondary = Color(0xFFFF6B9D)      // 暖粉
    val Tertiary = Color(0xFFA78BFA)       // 淡紫

    val Background = Color(0xFF0F1023)     // 深蓝灰底
    val Surface = Color(0xFF1E1F36)        // 卡片
    val SurfaceVariant = Color(0xFF2A2B45) // 更深卡片

    val TextPrimary = Color(0xFFF0EDEE)    // 白
    val TextSecondary = Color(0xFFA09DB8)  // 蓝灰
    val TextMuted = Color(0xFF6B6990)      // 暗灰

    val OnPrimary = Color(0xFF0F1023)
    val OnSecondary = Color(0xFF0F1023)
    val OnBackground = Color(0xFFF0EDEE)
    val OnSurface = Color(0xFFF0EDEE)

    val StatePlaying = Color(0xFF6BFFB8)   // 霓虹绿（ON AIR）
    val StateLoading = Color(0xFFFFA500)
    val StateSpeaking = Color(0xFFA78BFA)
    val StateError = Color(0xFFFF6B6B)

    val GradientStart = Color(0xFFFF6B9D)
    val GradientMid = Color(0xFFA78BFA)
    val GradientEnd = Color(0xFF6BB8FF)

    val DJBubbleBg = Color(0xFF1E1F36)
    val DJBubbleText = Color(0xFFA09DB8)
}
