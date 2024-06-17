package telegram.event.io

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import telegram.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class ApproveJoinRequestEvent(
    val timestamp: @Serializable(with = LocalDateTimeSerializer::class) LocalDateTime,
    @SerialName("channel_id")
    val channelId: String,
    @SerialName("subscriber_id")
    val subscriberId: Long,
    @SerialName("invite_link_name")
    val inviteLinkName: String
)