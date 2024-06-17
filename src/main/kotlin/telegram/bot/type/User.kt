package telegram.bot.type

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class User(
    val id: Long,
    @JsonNames("is_bot")
    val isBot: Boolean,
    @JsonNames("first_name")
    val firstName: String?
)