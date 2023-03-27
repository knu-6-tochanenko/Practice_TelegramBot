import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val bot = TelegramBot("6107739153:AAHJbmHIvMCCyKu_UuOr14QTrGVov4gFPtY")

    bot.handleUpdates {
        onMessage {
            message(data.text ?: "").send(data.from?.id ?: 0, bot)
        }
    }
}