package telegram.bot.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import telegram.bot.io.TelegramApiResponse
import telegram.bot.type.*

@Service
class TelegramApiClient(
    private val telegramBotProperties: TelegramBotProperties,
    @Qualifier("telegramWebClient") private val client: WebClient
) {

    suspend fun approveChatJoinRequest(approveRequest: ApproveChatJoinRequest): TelegramApiResponse<Boolean> = withContext(Dispatchers.IO) {
        client
            .post()
            .uri("approveChatJoinRequest")
            .bodyValue(approveRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun declineChatJoinRequest(approveRequest: ApproveChatJoinRequest): TelegramApiResponse<Boolean> = withContext(Dispatchers.IO) {
        client
            .post()
            .uri("declineChatJoinRequest")
            .bodyValue(approveRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun getChatMember(chatMemberRequest: GetChatMember): TelegramApiResponse<ChatMember> = withContext(Dispatchers.IO) {
        client
            .post()
            .uri("getChatMember")
            .bodyValue(chatMemberRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun registerWebhook(): TelegramApiResponse<Boolean> = withContext(Dispatchers.IO) {
        client
            .get()
            .uri {
                builder -> builder.path("setWebhook")
                    .queryParam("url", telegramBotProperties.webhook)
                    .build()
            }
            .retrieve()
            .awaitBody()
    }
}