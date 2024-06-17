package telegram.bot.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChatMember(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: Long
)