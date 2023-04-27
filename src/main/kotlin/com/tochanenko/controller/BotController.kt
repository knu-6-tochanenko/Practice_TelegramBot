package com.tochanenko.controller

import com.tochanenko.*
import com.tochanenko.log.log
import com.tochanenko.tools.TypingAction
import com.tochanenko.tools.addMarkdown
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
const val INGREDIENTS_OLD = "/i_old"
const val CHANGELOG = "/changelog"

const val helloMessage: String = "*Привіт!*\n\n" +
        "Я - бот, який допоможе тобі дізнатись кількість калорій в страві за її інгредієнтами та їх кількості в " +
        "грамах або в інших способах вимірювання ваги/об'єму: чайні ложки, столові ложки.\n\n" +
        "Просто напиши інгредієнти та їх кількість і чекай на відповідь!"

const val aboutMessage: String = "$helloMessage\n\n" +
        "Мене розробив *Владислав Точаненко* у 2023 році. Я є частиною виробничої практики, яку студенти " +
        "магістратури проходять на факультеті Комп'ютерних наук та кібернетики, КНУ ім Т. Шевченка. Якщо у Вас " +
        "з'явились якісь питання, можете звертатись до @tochanenko.\n\n" +
        "Версія: $VERSION, дивіться у /changelog що тут новенького з'явилось."

const val commandsMessage: String = "$ABOUT - Дізнатись більше про цей бот і про його власника\n" +
        "$COMMANDS - Список усіх доступних команд\n" +
        "$DISH [назва страви] - Дізнатись приблизну калорійність страви " +
        "[Будь-який текст, що містить інгредієнти та їх кількість] - Дізнатись калорійність кожного інгредієнта " +
        "окремо та калорійність усієї страви"

const val chatGPTMessage: String = "Введіть посилання до ChatGPT в одному повідомленні з командою $CHAT"

const val unknownIngredientsMessage: String = "Вибачте, я Вас не зрозумів. Можливо, Ви написали " +
        "текст, що не містить інгредієнтів, які мають поживну цінність. Якщо це помилка Бота, зверніться до @tochanenko"

const val unknownDishMessage: String = "Вибачте, я Вас не зрозумів. Напишіть назву страви одразу після команди " +
        "/dish. Ящо ви знаєте приблизну кількість страви у грамах чи мілілітрах, можете також вказати її:\n\n" +
        "/dish Чай з цукром 300 мілілітрів."

const val exampleMessage: String = "Для визначення калорійності страви треба написати список інгредієнтів та їх " +
        "кількість. Наприклад:\n\n_Яйця 2 штуки_\n_Смажена котлета з телячого фаршу 50 грамів_\n_Твердий сир 30 " +
        "грамів_\n\n" +
        "Або:\n\n_Авокадо 100 грамів, оливкове масло 4 столові ложки, варена курина грудинка 50 грамів, червоний лук " +
        "30грамів, томати 50 грамів, червоний перець 50 грамів._"

val changelogMessage: String = """
    *v1.1*

    _Новий функціонал_
    - Команда /changelog
    - Команда /dish
    - Команда /example
    - Меню команд бота
    - Опис бота на стартовому екрані

    _Оновлення_
    - Покращений алгоритм визначення калорійності інгредієнтів. Працює у `N + 1` разів швидше за алгоритм версії 1.0. `N` - кількість інгредієнтів
    - Покращений текст відповідей бота

    _Виправлення помилок_
    - Виправлена помилка пустої відповіді коли сумарна кількість калорій дорівнює 0

    *v1.0*

    _Новий функціонал_
    - Команда /about
    - Команда /commands
    - Визначення калорійності страви за інгредієнтами
    - Зображення бота
    - Опис бота
""".trimIndent()

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
        if (update.text!!.substring("/dish".length).isNotBlank()) {
            val typingAction = TypingAction(update.user.id, bot).start()
            val response: String = update.text?.let { getCaloriesByIngredientsForDishGPT(it) }!!
            log(update.user, update.text!!, response)
            message { response }.send(update.user, bot)
            typingAction.stop()
        } else {
            message { unknownDishMessage }.send(update.user, bot)
        }
    }

    @CommandHandler([CHANGELOG])
    suspend fun getChangelog(update: ProcessedUpdate, bot: TelegramBot) {
        message { changelogMessage }.options { parseMode = ParseMode.Markdown }.send(update.user, bot)
    }

    @CommandHandler([INGREDIENTS_OLD])
    suspend fun getCaloriesForIngredient(update: ProcessedUpdate, bot: TelegramBot) {
        getCaloriesForIngredientsOld(update, bot)
    }

    @UnprocessedHandler
    suspend fun anyUserInput(update: ProcessedUpdate, bot: TelegramBot) {
        getCaloriesForIngredients(update, bot)
    }

    private suspend fun getCaloriesForIngredients(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()
        val response = getIngredientsCaloriesGPT(update.text!!)
        typingAction.stop()
        message { addMarkdown(response) }.options { parseMode = ParseMode.Markdown }.send(update.user, bot)
    }

    @Deprecated(message = "This method uses old version of getting calories amount for calories. It works N+1 slower then the new version. It consumes a lot more OpenAI tokens then the new algorithm.")
    private suspend fun getCaloriesForIngredientsOld(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()
        val response = getCaloriesByIngredients(update, bot)
        message { response }.options { parseMode = ParseMode.Markdown }.send(update.user, bot)
        log(update.user, update.text!!, response)
        typingAction.stop()
    }
}
