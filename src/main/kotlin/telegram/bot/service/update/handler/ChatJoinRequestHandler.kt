package telegram.bot.service.update.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import telegram.bot.service.ChatJoinRequestApprover
import telegram.bot.type.ApproveChatJoinRequest
import telegram.bot.type.ChatJoinRequest
import telegram.bot.type.Update

@Service
class ChatJoinRequestHandler(
    private val chatJoinRequestApprover: ChatJoinRequestApprover
): TelegramUpdateHandler {
    private val logger = LoggerFactory.getLogger(ChatJoinRequestHandler::class.java)

    override suspend fun handleUpdate(update: Update) {
        val approveChatJoinRequest = getApproveChatJoinRequest(update.chatJoinRequest!!)
        val result = chatJoinRequestApprover.processApproveRequest(approveChatJoinRequest)
        logResult(result, approveChatJoinRequest)
    }

    suspend fun getApproveChatJoinRequest(chatJoinRequest: ChatJoinRequest): ApproveChatJoinRequest {
        return ApproveChatJoinRequest(
            chatJoinRequest.chat.id.toString(),
            chatJoinRequest.userChatId,
            chatJoinRequest.from.isBot,
            chatJoinRequest.inviteLink.name,
            chatJoinRequest.from.languageCode!!
        )
    }

    suspend fun logResult(result: Boolean, approveChatJoinRequest: ApproveChatJoinRequest) {
        when (result) {
            true -> logger.info(
                "The request to join the chat has been approved. Chat ID ${approveChatJoinRequest.chatId}. User ID ${approveChatJoinRequest.userId}."
            )
            false -> logger.info(
                "The request to join the chat was declined. Chat ID ${approveChatJoinRequest.chatId}. User ID ${approveChatJoinRequest.userId}."
            )
        }
    }
}