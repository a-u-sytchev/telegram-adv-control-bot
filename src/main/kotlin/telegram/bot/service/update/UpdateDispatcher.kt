package telegram.bot.service.update

import org.springframework.stereotype.Service
import telegram.bot.type.Update

@Service
class UpdateDispatcher(
    private val updateHandlerFactory: UpdateHandlerFactory
) {
    suspend fun handleUpdate(update: Update) {
        val updateHandler = updateHandlerFactory.getUpdateHandler(update)
        updateHandler.handleUpdate(update)
    }
}