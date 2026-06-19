package com.pengge.aitune.domain.prefetch

/** 预取结果 */
sealed class PrefetchResult {
    data class Success(val segment: com.pengge.aitune.domain.model.RadioSegment) : PrefetchResult()
    data class Fallback(val segment: com.pengge.aitune.domain.model.RadioSegment) : PrefetchResult()
    data object AlreadyInProgress : PrefetchResult()
    data object Skipped : PrefetchResult()
}

/** 降级策略 — API 不可用时的通用串词模板 */
object FallbackStrategy {
    private val intros = listOf(
        "好，我们先缓一缓，来听这首。",
        "来，换换心情。",
        "接下来这一首，是时候出场了。",
        "嗯，这首有点东西。",
        "别急，慢慢听。"
    )

    private val segues = listOf(
        "怎么样，这首有感觉吗？",
        "好歌不怕多，再来。",
        "继续往下走。",
        "音乐不停，我们继续。"
    )

    fun randomIntro() = intros.random()
    fun randomSegue() = segues.random()
}
