package com.pengge.aitune.domain.tts

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * TTS 接口抽象
 *
 * 支持多种后端：
 * - Android 系统 TTS（离线，自然度一般）
 * - 小米 MiMo V2.5 TTS（云端，自然度高）
 * - 其他云端 TTS（可扩展）
 */
interface DJTTS {
    val displayName: String

    /** 播报一段文字，返回播放时长（毫秒） */
    suspend fun speak(text: String): Long

    /** 预缓存 TTS，返回音频数据（字节数组） */
    suspend fun prefetchTTS(text: String): ByteArray?

    /** 停止当前播报 */
    suspend fun stop()

    /** 是否正在播报 */
    val isSpeaking: StateFlow<Boolean>

    /** 播报完成事件流 */
    val onSpeakingEnd: Flow<String>
}
