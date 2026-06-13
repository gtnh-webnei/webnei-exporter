
pluginManagement {
    repositories {
        maven {
            // RetroFuturaGradle
            name = "GTNH Maven"
            url = uri("https://nexus.gtnewhorizons.com/repository/public/")
            mavenContent {
                includeGroup("com.gtnewhorizons")
                includeGroupByRegex("com\\.gtnewhorizons\\..+")
            }
        }
        maven {
            name = "jitpack.io"
            url = uri("https://jitpack.io")
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }

    plugins {
        id("com.github.ElytraServers.elytra-conventions") version("v1.1.2.3")
    }
}

plugins {
    id("com.gtnewhorizons.gtnhsettingsconvention") version("2.0.20")
}
