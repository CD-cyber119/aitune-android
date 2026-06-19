package com.pengge.aitune.domain.model

import kotlinx.serialization.Serializable

/** 歌曲 */
@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String = "",
    val albumArtUrl: String = "",
    val durationMs: Long = 0L,
    val spotifyUri: String = "",
    val previewUrl: String? = null
)
