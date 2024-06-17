package telegram.event.io

import kotlinx.serialization.Serializable

@Serializable
data class Subscriber(
    val subscriberId: Long,
    val channelId: String
)
