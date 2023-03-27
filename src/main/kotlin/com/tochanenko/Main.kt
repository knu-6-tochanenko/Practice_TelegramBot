package com.tochanenko

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.ParseMode
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    print("Bot is Up and Running...")
    val bot = TelegramBot("6107739153:AAHJbmHIvMCCyKu_UuOr14QTrGVov4gFPtY")

    bot.handleUpdates {
        onMessage {
            message { "Processing..." }.send(data.from?.id ?: 0, bot)
        }
        onCommand("/start") {
            message { "Test Bot" }.send(user, bot)
        }
        onCommand("/about") {
            message { "This bot is a test bot made by *Vladyslav Tochanenko*. More functions coming soon..." }
                .options { parseMode = ParseMode.Markdown }
                .send(user, bot)
        }
        onCommand("/commands") {
            message { "`/start` - Start Bot\n`/about` - About bot creator\n`/commands` - List of all available commands" }
                .options { parseMode = ParseMode.Markdown }
                .send(user, bot)
        }
    }
}