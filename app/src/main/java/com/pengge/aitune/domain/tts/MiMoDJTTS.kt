package com.pengge.aitune.domain.tts

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Base64
import java.util.concurrent.TimeUnit
/**
 * 小米 MiMo V2.5 TTS 实现
 *
 * API: https://api.xiaomimimo.com/v1/chat/completions
 * 认证: api-key 头
 * 文档: https://mimo.mi.com/docs/zh-CN/quick-start/usage-guide/audio/speech-synthesis-v2.5
 *
 * 调用方式：
 * 1. 使用 chat-completions 格式
 * 2. 风格指令放在 role:user 的 content 中
 * 3. 播报文本放在 role:assistant 的 content 中
 * 4. 通过 audio 参数指定格式和音色
 * 5. 音频以 base64 编码返回在 message.audio.data 中
 */
class MiMoDJTTS(
    private val apiKey: String,
    private val endpoint: String = "https://api.xiaomimimo.com/v1"
) : DJTTS {

    override val displayName: String = "小米MiMo TTS"

    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val _isSpeaking = MutableStateFlow(false)
    override val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _onSpeakingEnd = Channel<String>(Channel.BUFFERED)
    override val onSpeakingEnd: Flow<String> = _onSpeakingEnd.receiveAsFlow()

    // 当前语音数据缓存
    private var currentAudioData: ByteArray? = null

    // 默认音色（中文女性）
    private var defaultVoice: String = "冰糖"

    /** 设置默认音色 */
    fun setDefaultVoice(voice: String) {
        defaultVoice = voice
    }

    /**
     * 电台风格指令模板：
     * 自然、温暖的中文电台主持人风格，语速适中，咬字清晰，
     * 像深夜电台一样自然有温度。
     */
    private val radioStylePrompt = """
        你是一位温暖自然的中文电台主持人。
        声音沉稳有温度，语速平缓，咬字清晰。
        像在深夜电台里跟听众聊天一样自然。
    """.trimIndent()

    override suspend fun speak(text: String): Long = withContext(Dispatchers.IO) {
        _isSpeaking.value = true

        try {
            val requestBody = MiMoRequest(
                model = "mimo-v2.5-tts",
                messages = listOf(
                    MiMoMessage("user", radioStylePrompt),
                    MiMoMessage("assistant", text)
                ),
                audio = MiMoAudio(
                    format = "wav",
                    voice = defaultVoice
                )
            )

            val bodyJson = json.encodeToString(MiMoRequest.serializer(), requestBody)
            val request = Request.Builder()
                .url("$endpoint/chat/completions")
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(bodyJson.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
                ?: throw RuntimeException("MiMo TTS API 返回空")

            if (!response.isSuccessful) {
                _isSpeaking.value = false
                return@withContext (text.length * 200L).coerceIn(1000, 15000)
            }

            val mimoResponse = json.decodeFromString(MiMoResponse.serializer(), responseBody)
            val audioBase64 = mimoResponse.choices.firstOrNull()
                ?.message?.audio?.data
                ?: throw RuntimeException("MiMo TTS 返回无音频数据")

            currentAudioData = Base64.getDecoder().decode(audioBase64)

            // 通知播放完成（实际播放由外部 AudioTrack/MediaPlayer 接管）
            _isSpeaking.value = false
            _onSpeakingEnd.trySend(text)

            // 返回估算时长（24kHz PCM 16bit mono, 约每秒 48000 字节）
            val audioLengthMs = (currentAudioData!!.size / 48000L) * 1000
            audioLengthMs.coerceIn(1000, 30000)

        } catch (e: Exception) {
            _isSpeaking.value = false
            (text.length * 200L).coerceIn(1000, 15000)
        }
    }

    override suspend fun prefetchTTS(text: String): ByteArray? {
        return try {
            speak(text)
            currentAudioData
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun stop() {
        _isSpeaking.value = false
        currentAudioData = null
    }

    /** 获取最新合成的音频数据 */
    fun getLatestAudio(): ByteArray? = currentAudioData
}

// ===================== MiMo API DTO =====================

@Serializable
data class MiMoRequest(
    val model: String,
    val messages: List<MiMoMessage>,
    val audio: MiMoAudio,
    val stream: Boolean = false
)

@Serializable
data class MiMoMessage(
    val role: String,
    val content: String
)

@Serializable
data class MiMoAudio(
    val format: String = "wav",
    val voice: String = "冰糖"
)

@Serializable
data class MiMoResponse(
    val choices: List<MiMoChoice>
)

@Serializable
data class MiMoChoice(
    val message: MiMoResponseMessage
)

@Serializable
data class MiMoResponseMessage(
    val audio: MiMoAudioData?
)

@Serializable
data class MiMoAudioData(
    val data: String // base64 编码的音频
)
