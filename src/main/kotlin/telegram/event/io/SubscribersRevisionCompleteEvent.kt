package telegram.event.io

import kotlinx.serialization.Serializable
import telegram.bot.type.ChatMember

@Serializable
data class SubscribersRevisionCompleteEvent(
    val subscribers: List<ChatMember>
)