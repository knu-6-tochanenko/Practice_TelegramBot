package com.tochanenko.runner

import eu.vendeli.tgbot.TelegramBot

suspend fun runLocal(bot: TelegramBot) {
    bot.update.setListener {
        handle(it)
    }
}