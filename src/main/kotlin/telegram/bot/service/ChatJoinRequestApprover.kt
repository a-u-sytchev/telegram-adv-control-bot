package telegram.bot.service

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import telegram.bot.client.TelegramApiClient
import telegram.bot.type.ApproveChatJoinRequest
import telegram.event.KafkaProducer
import telegram.event.io.ApproveJoinRequestEvent
import java.time.LocalDateTime

@Service
class ChatJoinRequestApprover(
    private val botClient: TelegramApiClient,
    private val kafkaProducer: KafkaProducer,
    private val json: Json
) {

    fun processApproveRequest(approveRequest: ApproveChatJoinRequest): Boolean = runBlocking {
        return@runBlocking when (!approveRequest.isBot) {
            true -> {
                val result = botClient.approveChatJoinRequest(approveRequest).result
                kafkaProducer.sendMessage(
                    "subscriberApproved",
                    json.encodeToString(
                        ApproveJoinRequestEvent(
                            LocalDateTime.now(),
                            approveRequest.chatId,
                            approveRequest.userId,
                            approveRequest.inviteLinkName,
                            approveRequest.languageCode
                        )
                    )
                )
                result
            }
            false -> botClient.declineChatJoinRequest(approveRequest).result
        }
    }
}