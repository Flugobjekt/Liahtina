pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "liahtina"

include("liahtina-api")
include("liahtina-server")

gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val liahtinaVersionChannel = providers.gradleProperty("channel").get().trim()
    val liahtinaBuildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()?.toInt()
    val versionString = if (liahtinaBuildNumber == null) {
        "$mcVersion.0-R0.1-SNAPSHOT"
    } else {
        "$mcVersion.0-R0.1-build.$liahtinaBuildNumber-${liahtinaVersionChannel.lowercase()}"
    }
    version = versionString
}