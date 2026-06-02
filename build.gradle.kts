import me.modmuss50.mpp.ReleaseType
import java.util.*

plugins {
    id("me.modmuss50.mod-publish-plugin") version("1.1.0")
}

gradle.projectsEvaluated {
    publishMods {
        val jar = project(":fabric").tasks.getByName("remapJar").outputs.files.singleFile
        file.set(jar)
        
        if (!project.hasProperty("build.release")) {
            return@publishMods println("Publishing is disabled, please use the CI publishing workflow")
        }

        val releasePlatform: String = project.providers.gradleProperty("build.release.platform").orNull
                ?: return@publishMods println("build.release.platform must be defined (expected: both, fabric, neoforge)")

        val modVersion = BuildConfig.createVersionString(project);

        type = when {
            modVersion.contains("alpha") -> ReleaseType.ALPHA
            modVersion.contains("beta") -> ReleaseType.BETA
            else -> ReleaseType.STABLE
        }
        changelog = BuildConfig.getChangelog(project)

        github {
            accessToken = project.providers.environmentVariable("GITHUB_TOKEN")
            repository = "MagicDevM/sodium-legacy"
            commitish = BuildConfig.calculateGitHash(project)
            version = BuildConfig.RELEASE_TAG
            displayName = "Sodium ${BuildConfig.MOD_VERSION} for Minecraft ${BuildConfig.MINECRAFT_VERSION}"
        }
    }
}