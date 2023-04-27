package com.tochanenko.controller

import com.tochanenko.chatGPTAnswer
import com.tochanenko.getCaloriesGPT
import com.tochanenko.getIngredientsForDishGPT
import com.tochanenko.getIngredientsGPT
import com.tochanenko.log.log
import com.tochanenko.tools.TypingAction
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.annotations.CommandHandler
import eu.vendeli.tgbot.annotations.UnprocessedHandler
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.internal.ProcessedUpdate

const val VERSION = "1.1"

const val START = "/start"
const val ABOUT = "/about"
const val COMMANDS = "/commands"
const val CHAT = "/t"
const val EXAMPLE = "/example"
const val DISH = "/dish"

const val helloMessage: String = "*Привіт!*\n\n" +
        "Я - бот, який допоможе тобі дізнатись кількість калорій в страві за її інгредієнтами та їх кількості в " +
        "грамах або в інших способах вимірювання ваги/об'єму: чайні ложки, столові ложки.\n\n" +
        "Просто напиши інгредієнти та їх кількість і чекай на відповідь!"

const val aboutMessage: String = "$helloMessage\n\n" +
        "Мене розробив *Владислав Точаненко* у 2023 році. Я є частиною виробничої практики, яку студенти " +
        "магістратури проходять на факультеті Комп'ютерних наук та кібернетики, КНУ ім Т. Шевченка. Якщо у Вас " +
        "з'явились якісь питання, можете звертатись до @tochanenko.\n\n" +
        "Версія: $VERSION"

const val commandsMessage: String = "$ABOUT - Дізнатись більше про цей бот і про його власника\n" +
        "$COMMANDS - Список усіх доступних команд\n" +
        "$DISH [назва страви] - Дізнатись приблизну калорійність страви " +
        "[Будь-який текст, що містить інгредієнти та їх кількість] - Дізнатись калорійність кожного інгредієнта " +
        "окремо та калорійність усієї страви"

const val chatGPTMessage: String = "Введіть посилання до ChatGPT в одному повідомленні з командою $CHAT"

const val unknownIngredientsMessage: String = "Вибачте, я Вас не зрозумів. Можливо, Ви написали " +
        "текст, що не містить інгредієнтів. Якщо це помилка Бота, зверніться до @tochanenko"

const val exampleMessage: String = "Для визначення калорійності страви треба написати список інгредієнтів та їх " +
        "кількість. Наприклад:\n\nЯйця 2 штуки\nСмажена котлета з телячого фаршу 50 грамів\nТвердий сир 30 грамів\n\n" +
        "Або:\n\nАвокадо 100 грамів, оливкове масло 4 столові ложки, варена курина грудинка 50 грамів, червоний лук " +
        "30грамів, томати 50 грамів, червоний перець 50 грамів."

class BotController {
    @CommandHandler([START])
    suspend fun start(update: ProcessedUpdate, bot: TelegramBot) {
        message { helloMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler([ABOUT])
    suspend fun about(update: ProcessedUpdate, bot: TelegramBot) {
        message { aboutMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler([COMMANDS])
    suspend fun commands(update: ProcessedUpdate, bot: TelegramBot) {
        message { commandsMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler([CHAT])
    suspend fun chatGPT(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()
        val message = update.text!!.toString()
        val response = if (message.length > 2) chatGPTAnswer(userInput = message)
        else chatGPTMessage
        message { response }.send(update.user, bot)
        typingAction.stop()
    }

    @CommandHandler([EXAMPLE])
    suspend fun getExample(update: ProcessedUpdate, bot: TelegramBot) {
        message { exampleMessage }
            .options { parseMode = ParseMode.Markdown }
            .send(update.user, bot)
    }

    @CommandHandler([DISH])
    suspend fun getIngredientsForDish(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()
        val response: String = update.text?.let { getIngredientsForDishGPT(it) }!!
        log(update.user, update.text!!, response)
        message { response }.send(update.user, bot)
        typingAction.stop()
    }

    @UnprocessedHandler
    suspend fun saySomething(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()

        val ingredients: List<String>? = update.text?.let { getIngredientsGPT(it) }

        if (ingredients != null) {
            if (ingredients.isEmpty() || ingredients[0].contains("устий список")) {
                typingAction.stop()
                message { unknownIngredientsMessage }.send(
                    update.user,
                    bot
                )
                log(update.user, update.text!!, "Not understood")
            } else {
                var response = "Для страви, що складається з:\n"
                var totalCalories: Int = 0

                for (ingredient in ingredients) {
                    val ingredientCalories = getCaloriesGPT(ingredient)
                    totalCalories += ingredientCalories
                    if (ingredientCalories > 0)
                        response += "- ${ingredient.trimIndent()}: *$ingredientCalories* cal.\n"
                }

                response += "\n*Сумарна кількість калорій: $totalCalories*"

                log(update.user, update.text!!, response)

                typingAction.stop()
                message { response }.options { parseMode = ParseMode.Markdown }.send(update.user, bot)
            }
        }
    }
}