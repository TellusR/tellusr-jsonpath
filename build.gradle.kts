plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.tellusr"
version = "0.9"

repositories {
    mavenCentral()
}

dependencies {
    val slf4j_version = "2.0.12"
    implementation("org.slf4j:slf4j-api:$slf4j_version")

    val serialization_version = "1.8.1"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
    // implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:$serialization_version")

    testImplementation(kotlin("test"))
    val logback_version = "1.5.18"
    testImplementation("ch.qos.logback:logback-classic:${logback_version}")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}