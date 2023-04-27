# Calories Counter Bot

A bot that calculates calories based on the ingredients of user's meal using ChatGPT.

Developed 100% in Kotlin, deployed to Heroku. To test this bot you can ask [@tochanenko](https://t.me/tochanenko) for the access to deployed bot version.

Language of the bot: Ukrainian.

## Algorithm

> ⚠️ Algorithm is deprecated in v1.1. All operations are handled in ChatGPT since v1.1

1. Ask ChatGPT to find a list of ingredients in user input and their quantity
2. Ask ChatGPT for each ingredient it's calories for given amount
3. Calculate total amount of calories and display this result in Message

## Used Technologies

* [OpenAI Kotlin](https://github.com/aallam/openai-kotlin) - an OpenAI API client for Kotlin with multiplatform and coroutines capabilities, developed by [Mouaad Aallam](https://github.com/aallam).
* [Kotlin Telegram Bot](https://github.com/vendelieu/telegram-bot) - a Telegram Bot API wrapper with handy Kotlin DSL, developed by [Jey](https://github.com/vendelieu)
* [Heroku](https://heroku.com) - a cloud PaaS. Used to deploy bot to the cloud.

## Changelog

### v1.1

**New Features**

* `/changelog` command
* `/dish` command
* `/example` command
* Commands menu
* Bot Description on Start Screen

**Updates**

* Improve algorithm of ingredients calories. Works `N + 1` times faster than on 1.0 version, where `N` - is number of ingredients
* Improve Bot output texts

**Bug Fixes**

* Fix bug when list of ingredients have a total 0 calories

### v1.0

**New Features**

* `/about` command
* `/commands` command
* Response to list of ingredients
* Bot Image
* Bot Description

## About

Developed by Vladyslav Tochanenko as a part of University Internship on Faculty of Computer Science and Cybernetics, Taras Shevchenko National University of Kyiv.
