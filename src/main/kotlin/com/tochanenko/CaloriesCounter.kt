package com.tochanenko

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId

suspend fun getIngredients(input: String): List<String> {
    return chatGPTAnswer(
        "Знайди інгредієнти з такого списку" +
                " і напиши їх у такому форматі \"інгредієнт : кількість\": $input"
    ).split("\n")
}

suspend fun getCalories(ingredientName: String): String {
    return chatGPTAnswer("Ти - система, яка визначає калорійність інгредієнта. Ти отримуєш на вхід назву інгредієнта і його кількість, а надаєш відповідь, що містить кількість калорій у цьому інгредієнті у такому форматі: \"інгредієнт - калорії\", якщо не можеш дати відповідь - просто пишеш \"0\", якщо не можеш дати точну кількість калорій - пиши приблизну. Дай відповідь на такий запит: $ingredientName")
}

@OptIn(BetaOpenAI::class)
suspend fun chatGPTAnswer(input: String): String {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = input
            )
        )
    )
    val completion: ChatCompletion = OPEN_AI.chatCompletion(chatCompletionRequest)
    return completion.choices[0].message?.content ?: ""
}