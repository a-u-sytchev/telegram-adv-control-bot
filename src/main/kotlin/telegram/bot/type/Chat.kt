package telegram.bot.type

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Long,
    val type: String,
)