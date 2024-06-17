package telegram.bot.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class Update(
    @JsonNames("update_id")
    val updateId: Long,
    @JsonNames("chat_join_request")
    val chatJoinRequest: ChatJoinRequest? = null
)