package com.pengge.aitune.domain.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Android 系统 TTS 实现
 * 优点：离线可用，无需网络
 * 缺点：自然度一般
 */
@Singleton
class SystemDJTTS @Inject constructor(
    private val context: Context
) : DJTTS {

    override val displayName: String = "系统TTS"

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _onSpeakingEnd = Channel<String>(Channel.BUFFERED)
    override val onSpeakingEnd: Flow<String> = _onSpeakingEnd.receiveAsFlow()

    private var tts: TextToSpeech? = null
    private var initialized = false

    private suspend fun ensureInit(): Boolean = suspendCancellableCoroutine { cont ->
        if (initialized) {
            cont.resume(true)
            return@suspendCancellableCoroutine
        }
        tts = TextToSpeech(context) { status ->
            initialized = (status == TextToSpeech.SUCCESS)
            if (initialized) {
                tts?.language = Locale.CHINESE
            }
            cont.resume(initialized)
        }
    }

    override suspend fun speak(text: String): Long {
        if (!ensureInit()) return 0L

        _isSpeaking.value = true
        val ttsEngine = tts ?: return 0L

        val utteranceId = "aitune_${System.currentTimeMillis()}"
        ttsEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
                _onSpeakingEnd.trySend(utteranceId ?: "")
            }
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
                _onSpeakingEnd.trySend(utteranceId ?: "")
            }
        })

        ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)

        // 返回估算时长
        return (text.length * 250L).coerceIn(1000, 15000)
    }

    override suspend fun prefetchTTS(text: String): ByteArray? = null

    override suspend fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }
}
