package com.pengge.aitune.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pengge.aitune.domain.model.Playlist
import com.pengge.aitune.domain.radio.RadioEngine
import com.pengge.aitune.domain.radio.RadioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/** 播放界面状态 */
data class NowPlayingUiState(
    val djSubtitle: String? = null,
    val trackTitle: String = "等待启动",
    val trackArtist: String = "",
    val albumArtUrl: String? = null,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val statusLabel: String = "就绪"
)

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val radioEngine: RadioEngine
) : ViewModel() {

    val uiState: StateFlow<NowPlayingUiState> = radioEngine.state.map { state ->
        when (state) {
            is RadioState.Idle -> NowPlayingUiState(
                statusLabel = "就绪"
            )
            is RadioState.Loading -> NowPlayingUiState(
                isLoading = true,
                statusLabel = "DJ 正在酝酿..."
            )
            is RadioState.Intro -> NowPlayingUiState(
                djSubtitle = state.text,
                isPlaying = true,
                statusLabel = "DJ 在说话"
            )
            is RadioState.Playing -> NowPlayingUiState(
                trackTitle = state.track.title,
                trackArtist = state.track.artist,
                albumArtUrl = state.track.albumArtUrl,
                progress = state.progress,
                isPlaying = true,
                statusLabel = "正在播放"
            )
            is RadioState.Segue -> NowPlayingUiState(
                djSubtitle = state.text,
                isPlaying = true,
                statusLabel = "DJ 在说话"
            )
            is RadioState.Paused -> NowPlayingUiState(
                trackTitle = "已暂停",
                isPlaying = false,
                statusLabel = "已暂停"
            )
            is RadioState.Error -> NowPlayingUiState(
                isError = true,
                errorMessage = state.message,
                statusLabel = "出错了"
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NowPlayingUiState())

    fun startRadio() {
        viewModelScope.launch {
            // 使用一个默认测试歌单
            val testPlaylist = Playlist(
                id = "test",
                name = "测试歌单",
                tracks = emptyList()
            )
            radioEngine.start(testPlaylist)
        }
    }

    fun skip() = radioEngine.skip()
    fun pause() = radioEngine.pause()
    fun stop() = radioEngine.stop()
}
