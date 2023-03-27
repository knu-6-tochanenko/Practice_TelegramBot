package com.tochanenko

import com.tochanenko.runner.runLocal
import com.tochanenko.runner.runRemote
import com.tochanenko.tools.parseEnvVar
import eu.vendeli.tgbot.TelegramBot
import kotlinx.coroutines.runBlocking

val local: Boolean = parseEnvVar("local").isNotEmpty()
val apiToken = parseEnvVar("apiKey")

fun main(): Unit = runBlocking {
    if (apiToken.isEmpty()) {
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

