package com.tochanenko

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId

suspend fun getIngredients(input: String): List<String> {
    return chatGPTAnswer(
        userInput = "Знайди інгредієнти з такого списку: $input",
        systemInput = "Ти - корисний асистент, який вилучає список інгредієнтів з тексту і пише їх у такому форматі: \"інгредієнт (кількість)\", якщо ж ти не можеш знайти список інгредієнтів, то напиши \"пустий список\""
    ).split("\n", ",")
}

suspend fun getCaloriesString(ingredientName: String): String {
    return chatGPTAnswer(
        userInput = "Знайди калорійність інгредієнта $ingredientName",
        systemInput = "Ти - корисний асистент, який визначає калорійність інгредієнта. Ти отримує на вхід назву інгредієнта і його кількість, а надаєш відповідь, що містить кількість калорій у цьому інгредієнті у такому форматі: \"інгредієнт - калорії\", якщо ж не можеш дати відповідь - пишеш \"0\", якщо ж не можеш дати точну кількість калорій, то пиши приблизну"
    )
}

suspend fun getCalories(ingredientName: String): Int {
    val caloriesResponse = getCaloriesString(ingredientName)
    val calories = findNumbersInString(ingredientName)
    if (calories.isEmpty())
        return 0
    val ingredientAmount = calories[0]
    val caloriesResponseNumbers = findNumbersInString(caloriesResponse)

    if (caloriesResponseNumbers.size > 1) {
        return if (caloriesResponseNumbers[0] == ingredientAmount) caloriesResponseNumbers[1] else caloriesResponseNumbers[0]
    } else return caloriesResponseNumbers[0]
}

fun findNumbersInString(str: String): List<Int> =
    Regex("[0-9]+").findAll(str)
        .map(MatchResult::value)
        .map { Integer.parseInt(it) }
        .toList()

@OptIn(BetaOpenAI::class)
suspend fun chatGPTAnswer(userInput: String? = null, systemInput: String? = null): String {
    val messages: MutableList<ChatMessage> = mutableListOf<ChatMessage>().toMutableList()
    if (systemInput != null) messages += ChatMessage(
        role = ChatRole.System,
        content = systemInput
    )

    if (userInput != null) messages += ChatMessage(
        role = ChatRole.User,
        content = userInput
    )

    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = messages
    )

    val completion: ChatCompletion = OPEN_AI.chatCompletion(chatCompletionRequest)
    return completion.choices[0].message?.content ?: ""
}