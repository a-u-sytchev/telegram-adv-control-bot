package telegram.bot

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import telegram.bot.service.update.UpdateDispatcher
import telegram.bot.service.update.error.UnknownTelegramUpdateException
import telegram.bot.type.Update

@RestController
@RequestMapping("/")
class BotController(
    private val json: Json,
    private val updateDispatcher: UpdateDispatcher
) {
    private val logger = LoggerFactory.getLogger(BotController::class.java)

    @PostMapping
    suspend fun getUpdates(@RequestBody body: String) {
        try {
            val update = json.decodeFromString<Update>(body)
            try {
                updateDispatcher.handleUpdate(update)
            } catch (error: UnknownTelegramUpdateException) {
                logger.error(error.message)
            }
        } catch (error: Exception) {
            logger.error("[${error.message}] Failed deserialize update: $body")
        }
    }
}