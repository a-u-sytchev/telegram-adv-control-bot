package telegram.event.io

import kotlinx.serialization.Serializable
import telegram.bot.type.ChatMemberStatus

@Serializable
data class Subscriber(
    val id: Long,
    val subscriberId: Long,
    val channelId: String,
    var status: ChatMemberStatus
)
