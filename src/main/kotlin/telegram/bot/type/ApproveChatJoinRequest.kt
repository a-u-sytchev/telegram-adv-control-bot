package telegram.bot.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApproveChatJoinRequest(
    @SerialName("chat_id")
    val chatId: String,
    @SerialName("user_id")
    val userId: Long,
    @SerialName("is_bot")
    val isBot: Boolean,
    @SerialName("invite_link_name")
    val inviteLinkName: String
)