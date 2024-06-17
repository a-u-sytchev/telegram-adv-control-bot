package telegram.bot.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ChatInviteLink(
    @JsonNames("invite_link")
    val inviteLink: String,
    val name: String,
    val creator: User
)