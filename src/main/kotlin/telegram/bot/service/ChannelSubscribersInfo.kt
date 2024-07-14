package telegram.bot.service

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import telegram.bot.client.TelegramApiClient
import telegram.bot.type.GetChatMember
import telegram.event.KafkaProducer
import telegram.event.io.Subscriber
import telegram.event.io.SubscribersRevisionCompleteEvent
import telegram.event.io.SubscribersRevisionEvent

@Service
class ChannelSubscribersInfo(
    private val botClient: TelegramApiClient,
    private val kafkaProducer: KafkaProducer,
    private val json: Json
) {
    private suspend fun updateSubscriberStatus(subscriber: Subscriber): Subscriber {
        val chatMember = botClient.getChatMember(
            GetChatMember(subscriber.channelId, subscriber.subscriberId)
        ).result
        subscriber.status = chatMember.status
        return subscriber
    }

    @KafkaListener(topics = ["subscribersRevision"], groupId = "telegrambot")
    suspend fun getSubscribersRevision(message: String) {
        val subscribers = json.decodeFromString<SubscribersRevisionEvent>(message).subscribers
        if (subscribers.isNotEmpty()) {
            val revision = SubscribersRevisionCompleteEvent(
                subscribers.map { updateSubscriberStatus(it) }.toList()
            )
            kafkaProducer.sendMessage(
                "subscribersRevisionComplete",
                json.encodeToString(revision)
            )
        }
    }
}