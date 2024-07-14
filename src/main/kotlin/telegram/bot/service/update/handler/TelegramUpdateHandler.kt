package telegram.bot.service.update.handler

import telegram.bot.type.Update

interface TelegramUpdateHandler {
    suspend fun handleUpdate(update: Update)
}