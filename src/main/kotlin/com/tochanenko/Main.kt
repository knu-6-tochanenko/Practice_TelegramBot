package com.tochanenko

import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.botactions.setWebhook
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    println("Bot is Up and Running...")

    val apiToken = if (System.getProperty("apiKey") != null)
        System.getProperty("apiKey")
    else if (System.getenv("apiKey") != null)
        System.getenv("apiKey") else ""

    if (apiToken == "") {
        println("Could not find API TOKEN")
        return@runBlocking
    }

    val bot = TelegramBot(apiToken, "com.tochanenko.controller")

    setWebhook(System.getenv("HOST") + "/" + System.getenv("apiKey")).send(bot)

    bot.update.setBehaviour {
        handle(it)
    }

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        routing {
            post("/" + System.getenv("apiKey")) {
                bot.update.parseAndHandle(call.receiveText())
                call.respond(HttpStatusCode.OK)
            }
        }
    }.start(wait = true)
}

