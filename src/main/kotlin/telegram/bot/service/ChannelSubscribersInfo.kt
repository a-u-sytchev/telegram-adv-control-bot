package telegram.bot.service

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import telegram.bot.client.TelegramApiClient
import telegram.bot.type.ChatMember
import telegram.bot.type.GetChatMember
import telegram.event.KafkaProducer
import telegram.event.io.SubscribersRevisionCompleteEvent
import telegram.event.io.SubscribersRevisionEvent

@Service
class ChannelSubscribersInfo(
    private val botClient: TelegramApiClient,
    private val kafkaProducer: KafkaProducer,
    private val json: Json
) {
    private fun getSubscriber(channelId: String, subscriberId: Long): ChatMember = runBlocking {
        return@runBlocking botClient.getChatMember(
            GetChatMember(channelId, subscriberId)
        ).result
    }

    @KafkaListener(topics = ["subscribersRevision"], groupId = "telegrambot")
    fun getSubscribersRevision(message: String) {
        val subscribers = json.decodeFromString<SubscribersRevisionEvent>(message).subscribers
        val revision = SubscribersRevisionCompleteEvent(
            subscribers.map { getSubscriber(it.channelId, it.subscriberId) }.toList()
        )
        kafkaProducer.sendMessage(
            "subscribersRevisionComplete",
            json.encodeToString(revision)
        )
    }

    @KafkaListener(topics = ["subscriberApproved"], groupId = "telegrambot")
    fun test(message: String) {
        println(message)
    }
}