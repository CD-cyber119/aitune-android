package com.pengge.aitune.domain.context

/** 用户画像 */
data class UserProfile(
    val topArtists: List<String> = emptyList(),
    val topGenres: List<String> = emptyList(),
    val recentTracks: List<String> = emptyList(),
    val preferredMoods: List<String> = emptyList(),
    val autoTasteTags: List<String> = emptyList()
) {
    fun isEmpty(): Boolean =
        topArtists.isEmpty() && topGenres.isEmpty() && recentTracks.isEmpty()

    fun toPrompt(): String = buildString {
        if (topArtists.isNotEmpty()) {
            appendLine("常听歌手：${topArtists.joinToString("、")}")
        }
        if (topGenres.isNotEmpty()) {
            appendLine("偏好风格：${topGenres.joinToString("、")}")
        }
        if (preferredMoods.isNotEmpty()) {
            appendLine("偏好情绪：${preferredMoods.joinToString("、")}")
        }
        if (autoTasteTags.isNotEmpty()) {
            appendLine("口味标签：${autoTasteTags.joinToString("、")}")
        }
    }
}
