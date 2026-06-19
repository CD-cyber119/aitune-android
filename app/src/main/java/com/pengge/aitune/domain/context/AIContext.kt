package com.pengge.aitune.domain.context

import com.pengge.aitune.domain.model.DJMemory
import com.pengge.aitune.domain.model.Track

/** AI 调用时传递的完整上下文 */
data class AIContext(
    val systemPrompt: String,
    val userProfile: UserProfile,
    val timeContext: TimeContext,
    val djMemory: DJMemory?,
    val playbackTrace: List<Track>,
    val seeds: List<String>,
    val userChat: String? = null
)
