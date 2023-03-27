package com.tochanenko.controller

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.internal.ProcessedUpdate

class BotController {
    @CommandHandler(["/start"])
    suspend fun start(update: ProcessedUpdate, bot: TelegramBot) {
        message { "Test Bot" }.send(update.user, bot)
    }

    @CommandHandler(["/about"])
    suspend fun about(update: ProcessedUpdate, bot: TelegramBot) {
        message { "This bot is a test bot made by *Vladyslav Tochanenko*. More functions coming soon..." }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler(["/commands"])
    suspend fun commands(update: ProcessedUpdate, bot: TelegramBot) {
        message { "`/start` - Start Bot\n`/about` - About bot creator\n`/commands` - List of all available commands" }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }
}