package telegram.bot.io

data class TelegramApiResponse<T: Any>(
    val ok: Boolean,
    val result: T,
    val description: String? = null
)