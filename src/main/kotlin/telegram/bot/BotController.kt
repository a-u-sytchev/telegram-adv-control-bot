package telegram.bot

import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import telegram.bot.service.UpdateHandler
import telegram.bot.type.Update

@RestController
@RequestMapping("/")
class BotController(
    private val json: Json,
    private val updateHandler: UpdateHandler
) {
    private val logger = LoggerFactory.getLogger(BotController::class.java)

    @PostMapping
    suspend fun getUpdates(@RequestBody body: String) {
        try {
            val update = json.decodeFromString<Update>(body)
            try {
                updateHandler.handleUpdate(update)
            } catch (error: Exception) {
                logger.error("[${error.message}] Failed handle update: $update")
            }
        } catch (error: Exception) {
            logger.error("[${error.message}] Failed deserialize update: $body")
        }
    }
}