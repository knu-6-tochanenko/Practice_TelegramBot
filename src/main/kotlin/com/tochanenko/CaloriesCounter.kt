package com.tochanenko

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId

suspend fun getIngredientsCaloriesGPT(input: String): String {
    return chatGPTAnswer(
        userInput = input,
        systemInput = "Ти - корисний асистент, який спочатку вилучає список інгредієнтів з тексту, а також визначає їх калорійність, враховуючи їх кількість. Також ти визначаєш сумарну кількість калорій у страві, сумуючи калорійність кожного інгредієнта. Ти надаєш відповідь у такому форматі: Список інгредієнтів, кожен рядок з якого виглядає як \"- інгредієнт, кількість інгредієнту - калорійність cal.\", а останній рядок містить фразу \"Калорійність страви: x cal.\", де x - сума калорійностей кожного з інгредієнтів."
    )
}

suspend fun getIngredientsGPT(input: String): List<String> {
    return chatGPTAnswer(
        userInput = "Знайди інгредієнти з такого списку: $input",
        systemInput = "Ти - корисний асистент, який вилучає список інгредієнтів з тексту і пише їх у такому форматі: \"інгредієнт (кількість)\", якщо ж ти не можеш знайти список інгредієнтів, то напиши \"пустий список\""
    ).split("\n", ",")
}

suspend fun getCaloriesStringGPT(ingredientName: String): String {
    return chatGPTAnswer(
        userInput = "Знайди калорійність інгредієнта $ingredientName",
        systemInput = "Ти - корисний асистент, який визначає калорійність інгредієнта. Ти отримуєш на вхід назву інгредієнта і його кількість, а надаєш відповідь, що містить кількість калорій у цьому інгредієнті у такому форматі: \"інгредієнт - калорії\", якщо ж не можеш дати відповідь - пишеш \"0\", якщо ж не можеш дати точну кількість калорій, то пиши приблизну"
    )
}

suspend fun getIngredientsForDishGPT(input: String): String {
    return chatGPTAnswer(
        userInput = input,
        systemInput = "Ти - корисний асистент, який визначає калорійність страви за її назвою та кількістю. Ти отримуєш на вхід назву страви та її кількість, якщо кількість не вказана, то вважай що страви одна порція. Ти видаєш відповідь у такому форматі: \"[назва страви] - [приблизна кількість калорій одним числом або проміжком]\"."
    )
}

suspend fun getCaloriesGPT(ingredientName: String): Int {
    val caloriesResponse = getCaloriesStringGPT(ingredientName)
    val calories = findNumbersInString(ingredientName)
    if (calories.isEmpty())
        return 0
    val ingredientAmount = calories[0]
    val caloriesResponseNumbers = findNumbersInString(caloriesResponse)

    return if (caloriesResponseNumbers.size > 1) {
        if (caloriesResponseNumbers[0] == ingredientAmount) caloriesResponseNumbers[1]
        else caloriesResponseNumbers[0]
    } else caloriesResponseNumbers[0]
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