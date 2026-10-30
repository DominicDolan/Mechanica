plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("multiplatform") version "2.2.0" apply false
    kotlin("plugin.serialization") version "2.2.0"
    `java-library`
}

allprojects {
    group = "com.mechanica.engine"
    version = "0.1"
    repositories { mavenLocal(); mavenCentral() }
}

// JVM-only backend, samples and the root tests retain their JVM dependencies.
allprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        dependencies {
            add("implementation", "org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
            add("implementation", "org.jetbrains.kotlin:kotlin-reflect:2.2.0")
            add("implementation", "org.jetbrains.kotlinx:kotlinx-io-core:0.3.0")
            add("implementation", "com.cave.library:Kotlin-CAVE:0.2")
        }
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(22)
        }
    }
}

dependencies {
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":mechanica"))
    testImplementation(project(":desktop-application"))
}
