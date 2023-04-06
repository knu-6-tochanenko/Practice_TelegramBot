package com.tochanenko.log

import eu.vendeli.tgbot.types.User
import java.text.SimpleDateFormat
import java.util.*

fun log(user: User, input: String, output: String) {
    val time = System.currentTimeMillis()
    val date = Date(time)
    val sdf = SimpleDateFormat("dd MMMM yyyy HH:mm:ss")
    java.io.File("./out/$time-${user.username}.txt").writeText(
        "Time: ${sdf.format(date)}\n\nName: ${user.firstName} ${user.lastName} (${user.username})\n\n" +
                "Request:\n$input\n\n" +
                "Response:\n$output"
    )
}