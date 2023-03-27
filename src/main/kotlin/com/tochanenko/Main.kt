package com.tochanenko

import com.tochanenko.runner.runLocal
import com.tochanenko.runner.runRemote
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
val apiToken = parseEnvVar("apiKey")

fun main(): Unit = runBlocking {
    println("Bot is Up and Running...")

    if (apiToken == "") {
        println("Could not find API TOKEN")
        return@runBlocking
    }

    val bot = TelegramBot(apiToken, "com.tochanenko.controller")

    if (local) {
        runLocal(bot)
    } else {
        runRemote(bot)
    }
}

