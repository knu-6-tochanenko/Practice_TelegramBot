package com.tochanenko.tools

fun parseEnvVar(name: String): String =
    if (System.getProperty(name) != null)
        System.getProperty(name)
    else if (System.getenv(name) != null)
        System.getenv(name) else ""
