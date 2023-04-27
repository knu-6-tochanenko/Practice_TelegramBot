package com.tochanenko.tools

fun addMarkdown(text: String): String {
    val lines: List<String> = text.split("\n", "\r")
    val numberRegex = "\\d+".toRegex()
    val resArray: ArrayList<String> = arrayListOf()

    if (numberRegex.findAll(text).none()) {
        return text
    }

    for (line in lines) {
        val numberSequences: Sequence<MatchResult> = numberRegex.findAll(line)

        if (numberSequences.none()) {
            if (line.isNotBlank()) resArray.add(line)
            continue
        }

        val firstNumber: Int = numberRegex.findAll(line).last().range.first
        var newLine = line.addCharToIndex('*', firstNumber)
        val caloriesPosition: Int = newLine.indexOf("cal.")
        newLine = newLine.addCharToIndex('*', caloriesPosition + "cal.".length)

        resArray.add(newLine)
    }

    var res = resArray.joinToString("\n")

    if (res.contains("-")) {
        res = res.addCharToIndex('\n', res.indexOf('-'))
    }

    if (res.contains("Калорійність страви:")) {
        res = res.addCharToIndex('\n', res.indexOf("Калорійність страви:"))
    }

    return res
}

fun String.addCharToIndex(char: Char, index: Int) =
    StringBuilder(this).apply { insert(index, char) }.toString()