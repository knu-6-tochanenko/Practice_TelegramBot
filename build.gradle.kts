plugins {
    application
    kotlin("jvm") version "1.8.0"
}

group = "com.tochanenko"
version = "1.0"

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
    implementation("io.ktor:ktor-server-core-jvm:2.2.4")
    implementation("io.ktor:ktor-server-netty-jvm:2.2.4")
    implementation("com.aallam.openai:openai-client:3.2.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

tasks.named<JavaExec>("run") {
    systemProperty("apiKey", System.getProperty("apiKey"))
    systemProperty("OpenAIApiKey", System.getProperty("OpenAIApiKey"))
}

tasks.create("stage").dependsOn("installDist")

tasks {
    val fatJar = register<Jar>("fatJar") {
        dependsOn.addAll(listOf("compileJava", "compileKotlin", "processResources"))
        archiveClassifier.set("practice-tochanenko-m2")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest { attributes(mapOf("Main-Class" to application.mainClass)) }
        val sourcesMain = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } +
                sourcesMain.output
        from(contents)
    }
    build {
        dependsOn(fatJar)
    }
}