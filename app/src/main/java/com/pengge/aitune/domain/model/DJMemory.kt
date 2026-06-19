package com.pengge.aitune.domain.model

/** DJ 记忆 — AI 记住上次播报的结尾，保持上下文连贯 */
data class DJMemory(
    val lastSegue: String,
    val lastTrackNames: List<String>,
    val segmentId: String,
    val createdAt: Long = System.currentTimeMillis()
)
