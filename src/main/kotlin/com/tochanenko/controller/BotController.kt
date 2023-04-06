package com.tochanenko.controller

import com.tochanenko.getCalories
import com.tochanenko.getIngredients
import com.tochanenko.tools.TypingAction
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.UnprocessedHandler
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

    @UnprocessedHandler
    suspend fun saySomething(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()


        val ingredients: List<String>? = update.text?.let { getIngredients(it) }
        var response = "Для страви, що складається з:\n"
        var totalCalories: Int = 0

        for (ingredient in ingredients!!) {
            val ingredientCalories = getCalories(ingredient)
            totalCalories += ingredientCalories
            response += "- $ingredient: *$ingredientCalories* cal.\n"
        }

        response += "\n*Сумарна кількість калорій: $totalCalories*"

        typingAction.stop()
        message {
            response
        }.options {
            parseMode = ParseMode.Markdown
        }.send(update.user, bot)
    }
}