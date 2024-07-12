package telegram.bot.service.update

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import telegram.bot.service.update.error.UnknownTelegramUpdateException
import telegram.bot.service.update.handler.ChatJoinRequestHandler
import telegram.bot.service.update.handler.TelegramUpdateHandler
import telegram.bot.type.Update

@Service
class UpdateHandlerFactory(
    val context: ApplicationContext
) {
    fun getUpdateHandler(update: Update): TelegramUpdateHandler {
        return when {
            update.chatJoinRequest != null -> context.getBean(ChatJoinRequestHandler::class.java)
            else -> throw UnknownTelegramUpdateException("No handler was found for the update: $update")
        }
    }
}