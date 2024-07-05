package telegram.event.io

import kotlinx.serialization.Serializable

@Serializable
data class SubscribersRevisionCompleteEvent(
    val subscribers: List<Subscriber>
)