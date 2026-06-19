package com.pengge.aitune.domain.radio

import com.pengge.aitune.domain.api.AIService
import com.pengge.aitune.domain.context.ContextBuilder
import com.pengge.aitune.domain.context.TimeContext
import com.pengge.aitune.domain.context.UserProfile
import com.pengge.aitune.domain.model.DJMemory
import com.pengge.aitune.domain.model.Playlist
import com.pengge.aitune.domain.model.RadioSegment
import com.pengge.aitune.domain.model.Track
import com.pengge.aitune.domain.music.MusicPlayer
import com.pengge.aitune.domain.music.PlaybackState
import com.pengge.aitune.domain.prefetch.FallbackStrategy
import com.pengge.aitune.domain.prefetch.PrefetchResult
import com.pengge.aitune.domain.tts.DJTTS
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ★ 电台核心引擎
 *
 * 状态生命周期：
 * Idle → Loading → Intro → Playing → Segue → Playing → ... → Loading (next segment)
 */
@Singleton
class RadioEngine @Inject constructor(
    private val aiService: AIService,
    private val contextBuilder: ContextBuilder,
    private val musicPlayer: MusicPlayer,
    private val ttsEngine: DJTTS
) {
    private val _state = MutableStateFlow<RadioState>(RadioState.Idle)
    val state: StateFlow<RadioState> = _state.asStateFlow()

    private var currentPlaylist: Playlist? = null
    private var currentSegment: RadioSegment? = null
    private var prefetchedSegment: RadioSegment? = null
    private var playbackHistory = mutableListOf<Track>()
    private var job: Job? = null
    private var scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    /** 启动电台 */
    fun start(playlist: Playlist) {
        currentPlaylist = playlist
        job?.cancel()
        job = scope.launch { runRadioLoop() }
    }

    /** 跳过当前 */
    fun skip() {
        job?.let {
            if (it.isActive) {
                scope.launch {
                    musicPlayer.stop()
                    ttsEngine.stop()
                }
            }
        }
    }

    /** 暂停/恢复 */
    fun pause() {
        scope.launch {
            when (_state.value) {
                is RadioState.Playing -> {
                    musicPlayer.pause()
                    _state.value = RadioState.Paused
                }
                is RadioState.Paused -> {
                    musicPlayer.resume()
                }
                else -> {}
            }
        }
    }

    /** 停止 */
    fun stop() {
        job?.cancel()
        scope.launch {
            musicPlayer.stop()
            ttsEngine.stop()
        }
        _state.value = RadioState.Idle
    }

    /** 重置状态 */
    fun reset() {
        job?.cancel()
        scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        _state.value = RadioState.Idle
        currentSegment = null
        prefetchedSegment = null
        playbackHistory.clear()
    }

    // ===================== 内部循环 =====================

    private suspend fun runRadioLoop() {
        while (isLoopActive) {
            when (_state.value) {
                is RadioState.Idle, is RadioState.Error -> {
                    // 首次或出错重启：进入加载
                    if (!loadNextSegment()) break
                }
                else -> {
                    // 正常运行中
                }
            }

            val segment = currentSegment ?: break

            // 阶段1：播 intro
            _state.value = RadioState.Intro(segment.intro)
            ttsEngine.speak(segment.intro)
            // 等待 TTS 结束（实际通过协程监听，这里简化为延时）
            delay(calculateTTSDuration(segment.intro))

            // 阶段2：逐首播歌
            for (track in segment.tracks) {
                _state.value = RadioState.Playing(track)
                musicPlayer.play(track)
                playbackHistory.add(track)

                // 保守：用歌曲时长作为等待（后期改为监听实际结束事件）
                val playDuration = if (track.durationMs > 0) track.durationMs else 30_000L
                delay(playDuration)

                // 如果被暂停，等待恢复
                while (_state.value is RadioState.Paused) {
                    delay(500)
                }

                // 如果被跳过，中断当前循环
                if (_state.value !is RadioState.Playing) break
            }

            // 阶段3：播 segue
            _state.value = RadioState.Segue(segment.segue)
            ttsEngine.speak(segment.segue)
            delay(calculateTTSDuration(segment.segue))

            // 预取下一段（后台）
            launch { prefetchNextSegment() }

            // 切换到下一段
            val next = prefetchedSegment
            if (next != null) {
                currentSegment = next
                prefetchedSegment = null
            } else {
                if (!loadNextSegment()) break
            }
        }

        // 循环结束
        if (_state.value !is RadioState.Error && _state.value !is RadioState.Idle) {
            _state.value = RadioState.Idle
        }
    }

    private suspend fun loadNextSegment(): Boolean {
        _state.value = RadioState.Loading
        return try {
            val context = contextBuilder.build(
                userProfile = UserProfile(),
                timeContext = TimeContext.now(),
                djMemory = currentSegment?.let {
                    DJMemory(
                        lastSegue = it.segue,
                        lastTrackNames = it.tracks.map { t -> t.title },
                        segmentId = it.timestamp.toString()
                    )
                },
                playbackTrace = playbackHistory.takeLast(10),
                seeds = currentPlaylist?.tracks?.shuffled()?.take(3)?.map { it.title } ?: emptyList()
            )
            currentSegment = aiService.generateRadioSegment(context)
            true
        } catch (e: Exception) {
            // 降级：用 fallback 串词 + 本地缓存歌单
            val fallbackTracks = currentPlaylist?.tracks
                ?.filterNot { it in playbackHistory }
                ?.shuffled()
                ?.take(2)
                ?: emptyList()

            currentSegment = RadioSegment(
                intro = FallbackStrategy.randomIntro(),
                tracks = fallbackTracks,
                segue = FallbackStrategy.randomSegue()
            )
            // 失败也继续，不要死
            true
        }
    }

    private suspend fun prefetchNextSegment() {
        try {
            val context = contextBuilder.build(
                userProfile = UserProfile(),
                timeContext = TimeContext.now(),
                djMemory = currentSegment?.let {
                    DJMemory(
                        lastSegue = it.segue,
                        lastTrackNames = it.tracks.map { t -> t.title },
                        segmentId = it.timestamp.toString()
                    )
                },
                playbackTrace = playbackHistory.takeLast(10),
                seeds = currentPlaylist?.tracks?.shuffled()?.take(3)?.map { it.title } ?: emptyList()
            )
            prefetchedSegment = aiService.generateRadioSegment(context)
        } catch (_: Exception) {
            // 预取失败不要紧，loadNextSegment 会再试
        }
    }

    /** 估算 TTS 播报时长（中文约每秒 4 个字） */
    private fun calculateTTSDuration(text: String): Long {
        return (text.length / 4 * 1000L).coerceIn(2000, 10000)
    }

    private val CoroutineScope.isLoopActive: Boolean get() = job?.isActive != false
}
