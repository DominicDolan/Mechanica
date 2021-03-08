/*
 * This file was generated by the Gradle 'init' task.
 *
 * The settings file is used to specify which projects to include in your build.
 *
 * Detailed information about configuring a multi-project build in Gradle can be found
 * in the user manual at https://docs.gradle.org/6.0/userguide/multi_project_builds.html
 */

rootProject.name = "Mechanica"

includeBuild("F:/Src/Mechanica/Kotlin-CAVE")
include("common", "desktop-application", "application-interface", "mechanica-shaders", "shaders-interface", "mechanica", "samples", "mechanica-ui")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.jetbrains.kotlin.")) {
                useVersion("1.4.0")
            }
        }
    }
}
