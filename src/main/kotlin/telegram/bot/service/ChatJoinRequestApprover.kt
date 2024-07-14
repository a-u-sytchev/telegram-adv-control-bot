package telegram.bot.service

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
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
    private val logger = LoggerFactory.getLogger(ChatJoinRequestApprover::class.java)

    suspend fun createEvent(approveRequest: ApproveChatJoinRequest) {
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
    }

    suspend fun processApproveRequest(approveRequest: ApproveChatJoinRequest): Boolean {
        return when (!approveRequest.isBot) {
            true -> {
                try {
                    val result = botClient.approveChatJoinRequest(approveRequest).result
                    createEvent(approveRequest)
                    result
                } catch (error: Exception) {
                    logger.error("${error.message}. Failed approve: $approveRequest")
                    false
                }
            }
            false -> botClient.declineChatJoinRequest(approveRequest).result
        }
    }
}