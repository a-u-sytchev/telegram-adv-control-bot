package telegram.bot.type

import kotlinx.serialization.Serializable

@Serializable
enum class ChatMemberStatus {
    member,
    left,
    administrator,
    creator,
    restricted,
    kicked
}

@Serializable
data class ChatMember(
    val status: ChatMemberStatus,
    val user: User
)