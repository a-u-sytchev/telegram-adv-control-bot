package telegram.bot.service.update

import org.springframework.stereotype.Service
import telegram.bot.service.update.handler.ChatJoinRequestHandler
import telegram.bot.type.Update

@Service
class UpdateDispatcher(
    private val chatJoinRequestHandler: ChatJoinRequestHandler,
    private val updateHandlerFactory: UpdateHandlerFactory
) {
    fun handleUpdate(update: Update) {
        val updateHandler = updateHandlerFactory.getUpdateHandler(update)
        updateHandler.handleUpdate(update)
    }
}