plugins {
    kotlin("jvm") version "2.1.20"
    `maven-publish`
}

group = "com.tellusr"
version = "0.9.4"

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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group as String
            artifactId = "tellusr-jsonpath"
            artifact(tasks.register("sourcesJar", Jar::class) {
                from(sourceSets["main"].allSource)
                archiveClassifier.set("sources")
            })
            version = version as String
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/tellusr/framework")
            credentials {
                username = project.findProperty("gpr.user")?.toString()
                    ?: System.getenv("GITHUB_USERNAME")
                            ?: System.getenv("GH_USERNAME")
                password = project.findProperty("gpr.key")?.toString()
                    ?: System.getenv("GITHUB_TOKEN")
                            ?: System.getenv("GH_TOKEN")
            }
        }
    }
}
