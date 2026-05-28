rootProject.name = "sodium"

pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.neoforged.net/releases/") }
        gradlePluginPortal()
    }
}

include("common")
// TODO: Implement fabric rendering api properly

//include("frapi")
include("fabric")