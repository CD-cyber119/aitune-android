package com.pengge.aitune.domain.api

import com.pengge.aitune.data.remote.dto.*
import com.pengge.aitune.data.repository.SettingsRepository
import com.pengge.aitune.domain.context.AIContext
import com.pengge.aitune.domain.model.RadioSegment
import com.pengge.aitune.domain.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** DeepSeek API 实现 */
@Singleton
class DeepSeekAIService @Inject constructor(
    private val settingsRepository: SettingsRepository
) : AIService {

    private val json = Json { ignoreUnknownKeys = true }
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    override suspend fun generateRadioSegment(context: AIContext): RadioSegment = withContext(Dispatchers.IO) {
        val apiKey = settingsRepository.getDeepSeekApiKeySuspended()
            ?: throw IllegalStateException("DeepSeek API Key 未设置")

        val requestBody = DeepSeekRequest(
            messages = listOf(
                Message("system", context.systemPrompt),
                Message("user", buildUserMessage(context))
            )
        )

        val bodyJson = json.encodeToString(DeepSeekRequest.serializer(), requestBody)
        val request = Request.Builder()
            .url("https://api.deepseek.com/v1/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(bodyJson.toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()
            ?: throw RuntimeException("DeepSeek API 返回空")

        val deepSeekResponse = json.decodeFromString(DeepSeekResponse.serializer(), responseBody)
        val content = deepSeekResponse.choices.first().message.content

        val aiResponse = try {
            json.decodeFromString(AIResponse.serializer(), content)
        } catch (e: Exception) {
            // 如果 AI 没按 JSON 格式返回，尝试提取
            throw RuntimeException("AI 返回格式错误: ${e.message}")
        }

        RadioSegment(
            intro = aiResponse.intro,
            tracks = aiResponse.tracks.map { ref ->
                Track(
                    id = "${ref.title}-${ref.artist}",
                    title = ref.title,
                    artist = ref.artist,
                    spotifyUri = "spotify:search:${ref.title} ${ref.artist}"
                )
            },
            segue = aiResponse.segue,
            mood = aiResponse.mood
        )
    }

    private fun buildUserMessage(context: AIContext): String = buildString {
        appendLine("## 当前时间")
        appendLine(context.timeContext.toPrompt())

        if (!context.userProfile.isEmpty()) {
            appendLine()
            appendLine("## 用户画像")
            appendLine(context.userProfile.toPrompt())
        }

        context.djMemory?.let { memory ->
            appendLine()
            appendLine("## DJ记忆")
            appendLine("上次结尾说：${memory.lastSegue}")
            appendLine("上次播了：${memory.lastTrackNames.joinToString("、")}")
        }

        if (context.playbackTrace.isNotEmpty()) {
            appendLine()
            appendLine("## 近期播放")
            context.playbackTrace.forEachIndexed { i, t ->
                appendLine("${i + 1}. ${t.title} - ${t.artist}")
            }
        }

        context.userChat?.let {
            appendLine()
            appendLine("## 用户刚才说")
            appendLine(it)
        }

        if (context.seeds.isNotEmpty()) {
            appendLine()
            appendLine("## 种子歌曲")
            appendLine(context.seeds.joinToString("、"))
        }

        appendLine()
        appendLine("请按 JSON 格式返回，不要加 markdown 包裹：")
        appendLine("""{"intro":"串词","tracks":[{"title":"歌名","artist":"歌手"}],"segue":"过渡语","mood":"情绪标签"}""")
    }
}
