package com.tochanenko.controller

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.tochanenko.OPEN_AI
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

    @OptIn(BetaOpenAI::class)
    @UnprocessedHandler
    suspend fun saySomething(update: ProcessedUpdate, bot: TelegramBot) {
        val typingAction = TypingAction(update.user.id, bot).start()

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.User,
                    content = update.text!!
                )
            )
        )
        val completion: ChatCompletion = OPEN_AI.chatCompletion(chatCompletionRequest)

        typingAction.stop()
        message { completion.choices[0].message?.content ?: "" }.send(update.user, bot)
    }
}