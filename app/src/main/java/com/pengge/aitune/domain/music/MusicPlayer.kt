package com.pengge.aitune.domain.music

import com.pengge.aitune.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/** 音乐播放状态 */
enum class PlaybackState {
    IDLE, LOADING, PLAYING, PAUSED, ENDED, ERROR
}

/** 音乐播放器接口抽象 */
interface MusicPlayer {
    val currentTrack: StateFlow<Track?>
    val playbackState: StateFlow<PlaybackState>
    val progress: StateFlow<Float> // 0.0 ~ 1.0

    suspend fun play(track: Track)
    suspend fun pause()
    suspend fun resume()
    suspend fun seekTo(position: Float)
    suspend fun stop()

    /** 歌曲结束事件 */
    val onTrackEnded: Flow<Track>
}
