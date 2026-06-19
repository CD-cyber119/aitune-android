package com.pengge.aitune.domain.radio

/** 电台控制动作 */
sealed interface RadioAction {
    /** 启动 */
    data object Start : RadioAction

    /** 播放 */
    data object Play : RadioAction

    /** 暂停 */
    data object Pause : RadioAction

    /** 跳过当前歌曲/串词 */
    data object Skip : RadioAction

    /** 推进到下一段播报 */
    data object NextSegment : RadioAction

    /** 停止 */
    data object Stop : RadioAction

    /** 播放进度更新 */
    data class TrackProgress(val progress: Float) : RadioAction

    /** 歌曲结束 */
    data class TrackEnded(val trackId: String) : RadioAction

    /** TTS 播报结束 */
    data class TTSEnded(val segmentId: String) : RadioAction

    /** 出错 */
    data class Error(val message: String) : RadioAction
}
