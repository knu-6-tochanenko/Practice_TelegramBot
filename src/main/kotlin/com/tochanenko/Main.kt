package com.tochanenko

import com.tochanenko.tools.parseEnvVar
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

val local: Boolean = parseEnvVar("local") != ""

fun main(): Unit = runBlocking {
    println("Bot is Up and Running...")

    val apiToken = parseEnvVar("apiKey")

    if (apiToken == "") {
        println("Could not find API TOKEN")
        return@runBlocking
    }

    val bot = TelegramBot(apiToken, "com.tochanenko.controller")

    if (local) {
        bot.update.setListener {
            handle(it)
        }
    } else {
        setWebhook("https://" + parseEnvVar("HOST") + "/" + parseEnvVar("apiKey")).send(bot)

        bot.update.setBehaviour {
            handle(it)
        }

        embeddedServer(Netty, port = parseEnvVar("PORT").toInt()) {
            routing {
                post("/" + parseEnvVar("apiKey")) {
                    bot.update.parseAndHandle(call.receiveText())
                    call.respond(HttpStatusCode.OK)
                }
            }
        }.start(wait = true)
    }
}

