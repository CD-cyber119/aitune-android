package com.pengge.aitune.domain.api

import com.pengge.aitune.domain.context.AIContext
import com.pengge.aitune.domain.model.RadioSegment

/** AI 服务接口 — 封装 DeepSeek API 调用 */
interface AIService {
    /** 根据上下文生成一段电台播报 */
    suspend fun generateRadioSegment(context: AIContext): RadioSegment
}
