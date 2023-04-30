package com.tochanenko.test

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.tochanenko.CALORIE_NINJAS_API_TOKEN
import eu.vendeli.tgbot.TelegramBot
import eu.vendeli.tgbot.api.message
import eu.vendeli.tgbot.types.ParseMode
import eu.vendeli.tgbot.types.User
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.StringReader
import kotlin.math.min
import kotlin.math.max

val client = OkHttpClient()

suspend fun checkCalories(
    input: String,
    calories: Double,
    user: User,
    bot: TelegramBot
) {
    val urlBuilder: HttpUrl.Builder = "https://api.calorieninjas.com/v1/nutrition".toHttpUrlOrNull()!!.newBuilder()
    urlBuilder.addQueryParameter("query", input)

    val request = Request.Builder()
        .url(urlBuilder.build().toString())
        .header("X-Api-Key", CALORIE_NINJAS_API_TOKEN)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) throw IOException("Unexpected code $response")

        for ((name, value) in response.headers) {
            println("$name: $value")
        }

        val responseBody: String = response.body!!.string()

        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()


        val recipes = gson?.fromJson<Response>(
            JsonReader(StringReader(responseBody)),
            Response::class.java
        )!!

        var totalCalories = 0.0

        for (recipe in recipes.items) {
            totalCalories += recipe.calories
        }

        val accuracy = min(calories, totalCalories) / max(calories, totalCalories) * 100

        if (totalCalories < 1.0) {
            message {
                "Помилка перевірки коректності визначення калорій за допомогою CalorieNinjas"
            }.send(user, bot)
        } else {
            message {
                "Відповідь ChatGPT на *${"%.2f".format(accuracy)}%* співпадає з реальними значеннями:\n\n" +
                        "ChatGPT: *${"%.2f".format(calories)} cal.*\n" +
                        "CalorieNinjas: *${"%.2f".format(totalCalories)} cal.*"
            }.options { parseMode = ParseMode.Markdown }.send(user, bot)
        }
    }
}
