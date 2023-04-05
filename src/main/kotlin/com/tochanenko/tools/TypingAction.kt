package com.tochanenko.tools

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.chat.chatAction
import eu.vendeli.tgbot.types.chat.ChatAction
import kotlinx.coroutines.*

class TypingAction(
    private val userId: Long,
    private val bot: TelegramBot
) {
    private val delayMillis: Long = 5000L
    private val job: Job = Job()

    fun start(): TypingAction {
        val scope = CoroutineScope(Dispatchers.IO + job)
        scope.launch {
            sendTypingAction(userId, bot)
        }
        return this
    }

    fun stop() {
        job.cancel()
    }

    private suspend fun sendTypingAction(userId: Long, bot: TelegramBot) {
        while (true) {
            chatAction(null, ChatAction.Typing).send(userId, bot)
            delay(delayMillis)
        }
    }
}


