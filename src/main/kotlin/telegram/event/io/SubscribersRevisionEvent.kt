package telegram.event.io

import kotlinx.serialization.Serializable

@Serializable
data class SubscribersRevisionEvent(
    val subscribers: List<Subscriber>
)