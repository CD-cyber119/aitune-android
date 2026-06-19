package com.pengge.aitune.domain.model

/** AI 生成的一次播报单元 */
data class RadioSegment(
    val intro: String,
    val tracks: List<Track>,
    val segue: String,
    val timestamp: Long = System.currentTimeMillis(),
    val mood: String? = null
)
