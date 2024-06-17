package telegram.bot.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ChatJoinRequest(
    val chat: Chat,
    val from: User,
    @JsonNames("user_chat_id")
    val userChatId: Long,
    @JsonNames("invite_link")
    val inviteLink: ChatInviteLink
)