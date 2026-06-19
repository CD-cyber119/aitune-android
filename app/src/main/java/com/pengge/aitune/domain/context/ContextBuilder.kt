package com.pengge.aitune.domain.context

import com.pengge.aitune.domain.model.DJMemory
import com.pengge.aitune.domain.model.Track

/**
 * Prompt 组装器
 * 把用户画像、时间、DJ记忆、播放历史等拼成 DeepSeek 的 system + user prompt
 */
class ContextBuilder {

    /**
     * 语体风格枚举
     */
    sealed class DJLanguage(val id: String, val name: String) {
        data object Casual : DJLanguage("casual", "闲聊电台")
        data object Story : DJLanguage("story", "故事电台")
        data object Chill : DJLanguage("chill", "深夜电台")
        data object Energetic : DJLanguage("energetic", "嗨场")
        data object Hype : DJLanguage("hype", "安利大会")
        data class Custom(val prompt: String) : DJLanguage("custom", "自定义")

        val systemPrompt: String get() = when (this) {
            Casual -> "你是一个随性自然的电台DJ，说话像朋友聊天一样轻松。"
            Story -> "你是一个会讲故事的电台DJ，每首歌都有它的背景和情绪。"
            Chill -> "你是深夜电台主持人，声音温柔，节奏缓慢。"
            Energetic -> "你是一场音乐节的现场MC，充满能量。"
            Hype -> "你是疯狂的音乐安利员，恨不得所有人都听这些歌。"
            is Custom -> this.prompt
        }
    }

    fun build(
        userProfile: UserProfile,
        timeContext: TimeContext,
        djMemory: DJMemory?,
        playbackTrace: List<Track>,
        seeds: List<String>,
        language: DJLanguage = DJLanguage.Casual,
        userChat: String? = null
    ): AIContext {

        val systemPrompt = buildString {
            appendLine(language.systemPrompt)
            appendLine()
            appendLine("## 规则约束")
            appendLine("- 每次返回 intro + 1~3首歌 + segue 的结构")
            appendLine("- 歌曲必须真实存在于 Spotify 曲库中")
            appendLine("- intro 和 segue 要自然，像真人DJ说话，有呼吸感")
            appendLine("- 避免重复推荐刚播过的歌曲")
            appendLine("- 严格按 JSON 格式返回，不要 markdown 包裹")
        }

        val userMessage = buildString {
            appendLine("## 当前时间")
            appendLine(timeContext.toPrompt())

            if (!userProfile.isEmpty()) {
                appendLine()
                appendLine("## 用户画像")
                appendLine(userProfile.toPrompt())
            }

            if (djMemory != null) {
                appendLine()
                appendLine("## DJ记忆（上次播报的结尾）")
                appendLine("上次结尾说：${djMemory.lastSegue}")
                appendLine("上次播了：${djMemory.lastTrackNames.joinToString("、")}")
            }

            if (playbackTrace.isNotEmpty()) {
                appendLine()
                appendLine("## 近期播放（避免重复推荐）")
                playbackTrace.forEachIndexed { i, t ->
                    appendLine("${i + 1}. ${t.title} - ${t.artist}")
                }
            }

            if (userChat != null) {
                appendLine()
                appendLine("## 用户刚才说")
                appendLine(userChat)
            }

            if (seeds.isNotEmpty()) {
                appendLine()
                appendLine("## 种子歌曲/关键词")
                appendLine(seeds.joinToString("、"))
            }

            appendLine()
            appendLine("请按以下 JSON 格式返回（不要加其他文字）：")
            appendLine("""{"intro":"串词...","tracks":[{"title":"歌名","artist":"歌手"}],"segue":"过渡语...","mood":"情绪标签"}""")
        }

        return AIContext(
            systemPrompt = systemPrompt,
            userProfile = userProfile,
            timeContext = timeContext,
            djMemory = djMemory,
            playbackTrace = playbackTrace,
            seeds = seeds,
            userChat = userChat
        )
    }
}
