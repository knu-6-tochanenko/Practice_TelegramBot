plugins {
    application
    kotlin("jvm") version "1.8.0"
}

group = "com.tochanenko"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.tochanenko.MainKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://search.maven.org/")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("eu.vendeli:telegram-bot:2.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}