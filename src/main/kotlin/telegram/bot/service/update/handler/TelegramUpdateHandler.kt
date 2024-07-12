package telegram.bot.service.update.handler

import telegram.bot.type.Update

interface TelegramUpdateHandler {
    fun handleUpdate(update: Update)
}