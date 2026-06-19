package com.pengge.aitune.domain.radio

import com.pengge.aitune.domain.model.Track

/** 电台状态机 */
sealed interface RadioState {
    /** 空闲 */
    data object Idle : RadioState

    /** 正在请求 AI */
    data object Loading : RadioState

    /** DJ 正在说开场词 */
    data class Intro(val text: String) : RadioState

    /** 正在播放歌曲 */
    data class Playing(
        val track: Track,
        val progress: Float = 0f
    ) : RadioState

    /** DJ 正在说过渡串词 */
    data class Segue(val text: String) : RadioState

    /** 已暂停 */
    data object Paused : RadioState

    /** 错误 */
    data class Error(val message: String) : RadioState
}
