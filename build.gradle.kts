import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.intellij") version "1.17.4"
}

val pluginGroup: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project

group = pluginGroup
version = pluginVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(17)
}

intellij {
    version.set("2023.3")
    type.set("IC")
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set(pluginSinceBuild)
        untilBuild.set(pluginUntilBuild.ifBlank { null })
        changeNotes.set(
            """
                <ul>
                    <li>Initial Thrift language support with syntax highlighting and formatting.</li>
                </ul>
            """.trimIndent()
        )
    }

    runIde {
        autoReloadPlugins.set(true)
    }

    buildSearchableOptions {
        enabled = false
    }
}
