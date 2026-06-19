package com.pengge.aitune.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeepSeekRequest(
    val model: String = "deepseek-chat",
    val messages: List<Message>,
    val response_format: ResponseFormat? = null,
    val temperature: Double = 0.8,
    val max_tokens: Int = 2000
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class ResponseFormat(
    val type: String = "json_object"
)

@Serializable
data class DeepSeekResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ResponseMessage
)

@Serializable
data class ResponseMessage(
    val content: String
)

/** AI 返回的歌曲引用（歌名+歌手，需要解析为实际 Track） */
@Serializable
data class AIResponse(
    val intro: String,
    val tracks: List<AITrackRef>,
    val segue: String,
    val mood: String? = null
)

@Serializable
data class AITrackRef(
    val title: String,
    val artist: String
)
