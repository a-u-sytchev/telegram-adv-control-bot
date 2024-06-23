package telegram.bot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import telegram.bot.type.ApproveChatJoinRequest
import telegram.bot.type.Update

@Service
class UpdateHandler(
    private val chatJoinRequestApprover: ChatJoinRequestApprover
) {
    private val logger = LoggerFactory.getLogger(UpdateHandler::class.java)

    fun handleUpdate(update: Update) {
        if (update.chatJoinRequest != null) {
            val approveRequest = ApproveChatJoinRequest(
                update.chatJoinRequest.chat.id.toString(),
                update.chatJoinRequest.userChatId,
                update.chatJoinRequest.from.isBot,
                update.chatJoinRequest.inviteLink.name
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
}