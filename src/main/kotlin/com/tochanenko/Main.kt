package com.tochanenko

import com.aallam.openai.client.OpenAI
import com.tochanenko.tools.parseEnvVar
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.botactions.setWebhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

val TELEGRAM_API_TOKEN = parseEnvVar("apiKey")
val OPENAI_API_TOKEN = parseEnvVar("OpenAIApiKey")
val OPEN_AI = OpenAI(OPENAI_API_TOKEN)

fun main(): Unit = runBlocking {
    if (TELEGRAM_API_TOKEN.isEmpty()) {
        println("Could not find API TOKEN")
        return@runBlocking
    }

    if (OPENAI_API_TOKEN.isEmpty()) {
        println("Could not find API TOKEN")
        return@runBlocking
    }

    val bot = TelegramBot(TELEGRAM_API_TOKEN, "com.tochanenko.controller")

    setWebhook("https://" + parseEnvVar("HOST") + "/" + parseEnvVar("apiKey")).send(bot)

    bot.update.setListener {
        handle(it)
    }

    io.ktor.server.engine.embeddedServer(Netty, port = parseEnvVar("PORT").toInt()) {
        routing {
            post("/" + parseEnvVar("apiKey")) {
                bot.update.parseAndHandle(call.receiveText())
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}

