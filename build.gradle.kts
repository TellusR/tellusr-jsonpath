plugins {
    kotlin("jvm") version "2.1.20"
}

group = "com.tellusr"
version = "0.9"

repositories {
    mavenCentral()
}

dependencies {
    val ktor_version = "3.1.2"
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation(kotlin("reflect"))

    val serialization_version = "1.6.2"
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:$serialization_version")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}