package com.pengge.aitune.domain.model

/** 歌单 */
data class Playlist(
    val id: String,
    val name: String,
    val description: String = "",
    val imageUrl: String = "",
    val tracks: List<Track> = emptyList(),
    val trackCount: Int = tracks.size
)
