package telegram.bot.service

import org.springframework.stereotype.Service
import telegram.bot.type.ApproveChatJoinRequest
import telegram.bot.type.Update

@Service
class UpdateHandler(
    private val chatJoinRequestApprover: ChatJoinRequestApprover
) {
    fun handleUpdate(update: Update) {
        if (update.chatJoinRequest != null) {
            val approveRequest = ApproveChatJoinRequest(
                update.chatJoinRequest.chat.id.toString(),
                update.chatJoinRequest.userChatId,
                update.chatJoinRequest.inviteLink.name
             )
            chatJoinRequestApprover.processApproveRequest(approveRequest)
        }
    }
}