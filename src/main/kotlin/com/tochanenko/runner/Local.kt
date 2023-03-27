package com.tochanenko.runner

import eu.vendeli.tgbot.TelegramBot

suspend fun runLocal(bot: TelegramBot) {
    println("Local Mode")
    bot.update.setListener {
        handle(it)
    }
}