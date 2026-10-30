plugins { kotlin("multiplatform") }

kotlin {
    linuxX64 {
        binaries.executable { entryPoint = "com.mechanica.samples.main" }
    }
    sourceSets {
        linuxX64Main.dependencies { implementation(project(":native-application")) }
    }
}
