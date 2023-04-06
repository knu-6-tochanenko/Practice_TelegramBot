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

const val helloMessage: String = "*Привіт!*\n\n" +
        "Я - бот, який допоможе тобі дізнатись кількість калорій в страві " +
        "за її інгредієнтами та їх кількості в грамах або в інших способах " +
        "вимірювання ваги/об'єму: чайні ложки, столові ложки.\n\n" +
        "Просто напиши інгредієнти та їх кількість і чекай на відповідь!\n\n"

const val aboutMessage: String = "$helloMessage\n\n" +
        "Мене розробив *Владислав Точаненко* у 2023 році. Я є частиною виробничої " +
        "практики, яку студенти магістратури проходять на факультеті Комп'ютерних " +
        "наук та кібернетики, КНУ ім Т. Шевченка. Якщо у Вас з'явились якісь питання, " +
        "можете звертатись до @tochanenko.\n\nВерсія: 1.0"

const val commandsMessage: String = "/about - Дізнатись більше про цей бот і про " +
        "його власника\n" +
        "/commands - Список усіх доступних команд\n" +
        "Будь-який текст, що містить інгредієнти та їх кількість - Список інгредієнтів " +
        "з їх калорійністю та сумарною калорійністю страви"

class BotController {
    @CommandHandler(["/start"])
    suspend fun start(update: ProcessedUpdate, bot: TelegramBot) {
        message { helloMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler(["/about"])
    suspend fun about(update: ProcessedUpdate, bot: TelegramBot) {
        message { aboutMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler(["/commands"])
    suspend fun commands(update: ProcessedUpdate, bot: TelegramBot) {
        message { commandsMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @UnprocessedHandler
    suspend fun saySomething(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()


        val ingredients: List<String>? = update.text?.let { getIngredients(it) }

        if (ingredients != null) {
            if (ingredients.isEmpty() || ingredients[0].contains("пустий список")) {
                typingAction.stop()
                message {"Вибачте, я Вас не зрозумів. Можливо, Ви написали текст, що не містить інгредієнтів. Якщо це помилка Бота, зверніться до @tochanenko"}.send(update.user, bot)
                return
            }
        }

        var response = "Для страви, що складається з:\n"
        var totalCalories: Int = 0

        for (ingredient in ingredients!!) {
            val ingredientCalories = getCalories(ingredient)
            totalCalories += ingredientCalories
            response += "- $ingredient: *$ingredientCalories* cal.\n"
        }

        response += "\n*Сумарна кількість калорій: $totalCalories*"

        typingAction.stop()
        message { response }.options { parseMode = ParseMode.Markdown }.send(update.user, bot)
    }
}