package telegram.bot.client

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("telegram.bot")
data class TelegramBotProperties(
    val token: String,
    val webhook: String
)