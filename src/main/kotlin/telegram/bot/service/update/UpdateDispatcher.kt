package telegram.bot.service.update

import org.springframework.stereotype.Service
import telegram.bot.service.update.handler.ChatJoinRequestHandler
import telegram.bot.type.Update

@Service
class UpdateDispatcher(
    private val chatJoinRequestHandler: ChatJoinRequestHandler
) {
    fun handleUpdate(update: Update) {
        if (update.chatJoinRequest != null) {
            chatJoinRequestHandler.handleUpdate(update.chatJoinRequest)
        }
    }
}