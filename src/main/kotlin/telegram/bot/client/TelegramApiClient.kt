package telegram.bot.client

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import telegram.bot.io.TelegramApiResponse
import telegram.bot.type.*

@Service
class TelegramApiClient(
    private val telegramBotProperties: TelegramBotProperties
) {
    private val client = WebClient.builder()
        .baseUrl("https://api.telegram.org/bot${this.telegramBotProperties.token}/")
        .build()

    suspend fun approveChatJoinRequest(approveRequest: ApproveChatJoinRequest): TelegramApiResponse<Boolean> {
        return this.client
            .post()
            .uri("approveChatJoinRequest")
            .bodyValue(approveRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun declineChatJoinRequest(approveRequest: ApproveChatJoinRequest): TelegramApiResponse<Boolean> {
        return this.client
            .post()
            .uri("declineChatJoinRequest")
            .bodyValue(approveRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun getChatMember(chatMemberRequest: GetChatMember): TelegramApiResponse<ChatMember> {
        return this.client
            .post()
            .uri("getChatMember")
            .bodyValue(chatMemberRequest)
            .retrieve()
            .awaitBody()
    }

    suspend fun registerWebhook(): TelegramApiResponse<Boolean> {
        return this.client
            .get()
            .uri {
                builder -> builder.path("setWebhook")
                    .queryParam("url", this.telegramBotProperties.webhook)
                    .build()
            }
            .retrieve()
            .awaitBody()
    }
}