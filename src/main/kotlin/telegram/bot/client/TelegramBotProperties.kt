package telegram.bot.client

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration


@ConfigurationProperties("telegram.bot")
data class TelegramBotProperties(
    val token: String,
    val webhook: String,
    val timeout: Duration
)