package telegram.bot.service.update.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import telegram.bot.service.ChatJoinRequestApprover
import telegram.bot.type.ApproveChatJoinRequest
import telegram.bot.type.Update

@Service
class ChatJoinRequestHandler(
    private val chatJoinRequestApprover: ChatJoinRequestApprover
): TelegramUpdateHandler {
    private val logger = LoggerFactory.getLogger(ChatJoinRequestHandler::class.java)

    override fun handleUpdate(update: Update) {
        val chatJoinRequest = update.chatJoinRequest!!
        val approveRequest = ApproveChatJoinRequest(
            chatJoinRequest.chat.id.toString(),
            chatJoinRequest.userChatId,
            chatJoinRequest.from.isBot,
            chatJoinRequest.inviteLink.name,
            chatJoinRequest.from.languageCode!!
        )
        when(chatJoinRequestApprover.processApproveRequest(approveRequest)) {
            true -> logger.info(
                "The request to join the chat has been approved. Chat ID ${approveRequest.chatId}. User ID ${approveRequest.userId}."
            )
            false -> logger.info(
                "The request to join the chat was declined. Chat ID ${approveRequest.chatId}. User ID ${approveRequest.userId}."
            )
        }
    }
}