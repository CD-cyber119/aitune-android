package com.pengge.aitune.domain.context

import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** 时间/场景上下文 */
data class TimeContext(
    val hour: Int,
    val dayOfWeek: DayOfWeek,
    val isWeekend: Boolean,
    val season: String = detectSeason(hour),
    val weatherTag: String? = null
) {
    companion object {
        fun now(): TimeContext {
            val now = LocalDateTime.now()
            return TimeContext(
                hour = now.hour,
                dayOfWeek = now.dayOfWeek,
                isWeekend = now.dayOfWeek == DayOfWeek.SATURDAY ||
                        now.dayOfWeek == DayOfWeek.SUNDAY,
                season = detectSeason(now.hour)
            )
        }

        private fun detectSeason(hour: Int): String = when (hour) {
            in 5..8 -> "清晨"
            in 9..11 -> "上午"
            in 12..13 -> "午间"
            in 14..17 -> "午后"
            in 18..20 -> "傍晚"
            in 21..23 -> "夜晚"
            else -> "深夜"
        }
    }

    fun toPrompt(): String {
        val dayStr = when (dayOfWeek) {
            DayOfWeek.MONDAY -> "周一"
            DayOfWeek.TUESDAY -> "周二"
            DayOfWeek.WEDNESDAY -> "周三"
            DayOfWeek.THURSDAY -> "周四"
            DayOfWeek.FRIDAY -> "周五"
            DayOfWeek.SATURDAY -> "周六"
            DayOfWeek.SUNDAY -> "周日"
        }
        val weekendNote = if (isWeekend) "（周末）" else "（工作日）"
        return "$dayStr$weekendNote，当前$season（${hour}点）"
    }
}
