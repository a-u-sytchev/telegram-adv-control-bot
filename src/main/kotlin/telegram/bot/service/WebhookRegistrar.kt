package telegram.bot.service

import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import telegram.bot.client.TelegramApiClient

@Service
class WebhookRegistrar(
    private val botClient: TelegramApiClient
) {
    private val logger = LoggerFactory.getLogger(WebhookRegistrar::class.java)

    @PostConstruct
    fun setWebhook() = runBlocking {
        val response = botClient.registerWebhook()
        if (response.ok && response.result) {
            logger.info(response.description)
        }
    }
}