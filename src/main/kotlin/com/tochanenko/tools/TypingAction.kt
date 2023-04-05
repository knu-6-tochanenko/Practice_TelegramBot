package com.tochanenko.tools

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.chatAction
import eu.vendeli.tgbot.types.chat.ChatAction
import kotlinx.coroutines.*

class TypingAction(
    private val telegramId: Long,
    private val bot: TelegramBot
) {
    private val delay = 5000L
    private val job = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        while (isActive) {
            chatAction(null, ChatAction.Typing).send(telegramId, bot)
            delay(delay)
        }
    }

    fun start(): TypingAction = this.also { job.start() }
    fun stop() = job.cancel()
}


