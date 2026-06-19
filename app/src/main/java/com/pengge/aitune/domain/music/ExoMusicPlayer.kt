package com.pengge.aitune.domain.music

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.pengge.aitune.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 音乐播放器实现 — 基于 Media3 ExoPlayer
 *
 * 当前用本地文件/URL 播放作为基础实现。
 * 后续接入 Spotify SDK 后可替换 play() 内部逻辑。
 */
@Singleton
class ExoMusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicPlayer {

    private val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _currentTrack = MutableStateFlow<Track?>(null)
    override val currentTrack: StateFlow<Track?> = _currentTrack.asStateFlow()

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    override val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _onTrackEnded = Channel<Track>(Channel.BUFFERED)
    override val onTrackEnded: Flow<Track> = _onTrackEnded.receiveAsFlow()

    private var currentPlayingTrack: Track? = null
    private var progressJob: Job? = null

    init {
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                _playbackState.value = when (state) {
                    Player.STATE_IDLE -> PlaybackState.IDLE
                    Player.STATE_BUFFERING -> PlaybackState.LOADING
                    Player.STATE_READY -> PlaybackState.PLAYING
                    Player.STATE_ENDED -> {
                        currentPlayingTrack?.let { _onTrackEnded.trySend(it) }
                        PlaybackState.ENDED
                    }
                    else -> PlaybackState.IDLE
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying && _playbackState.value == PlaybackState.PLAYING) {
                    _playbackState.value = PlaybackState.PAUSED
                }
            }
        })
    }

    override suspend fun play(track: Track) {
        stop()

        currentPlayingTrack = track
        _currentTrack.value = track
        _playbackState.value = PlaybackState.LOADING

        // 使用预览 URL（30秒片段）
        // 后续接入 Spotify SDK 后替换为完整歌曲播放
        val url = track.previewUrl ?: track.spotifyUri

        val mediaItem = MediaItem.Builder()
            .setMediaId(track.id)
            .setUri(url)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .build()
            )
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        // 启动进度跟踪
        startProgressLoop()
    }

    override suspend fun pause() {
        player.pause()
    }

    override suspend fun resume() {
        player.play()
    }

    override suspend fun seekTo(position: Float) {
        val duration = player.duration
        if (duration > 0) {
            player.seekTo((duration * position).toLong())
        }
    }

    override suspend fun stop() {
        progressJob?.cancel()
        player.stop()
        player.clearMediaItems()
        _currentTrack.value = null
        _playbackState.value = PlaybackState.IDLE
        _progress.value = 0f
    }

    /** 进度更新循环 */
    private fun startProgressLoop() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                val duration = player.duration
                if (duration > 0) {
                    _progress.value = (player.currentPosition.toFloat() / duration).coerceIn(0f, 1f)
                }
                delay(500)
            }
        }
    }

    /** 清理资源 */
    fun release() {
        progressJob?.cancel()
        player.release()
        scope.cancel()
    }
}
