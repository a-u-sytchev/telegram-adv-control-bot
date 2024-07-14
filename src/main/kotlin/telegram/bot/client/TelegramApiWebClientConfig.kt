package telegram.bot.client

import io.netty.handler.logging.LogLevel
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat

@Configuration
class TelegramApiWebClientConfig(
    private val telegramBotProperties: TelegramBotProperties,
    private val json: Json
) {

    @Bean("telegramWebClient")
    fun telegramWebClient(): WebClient {
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.create()
                        .wiretap(this::class.qualifiedName!!, LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                        .responseTimeout(telegramBotProperties.timeout)
                )
            )
            .codecs {
                it.defaultCodecs().kotlinSerializationJsonEncoder(KotlinSerializationJsonEncoder(json))
                it.defaultCodecs().kotlinSerializationJsonDecoder(KotlinSerializationJsonDecoder(json))
            }
            .baseUrl("https://api.telegram.org/bot${this.telegramBotProperties.token}/")
            .build()
    }
}