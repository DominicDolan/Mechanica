plugins { kotlin("multiplatform") }

kotlin {
    linuxX64 {
        binaries.executable { entryPoint = "com.mechanica.samples.engineMain" }
    }
    sourceSets {
        linuxX64Main.dependencies {
            implementation(project(":native-application"))
            implementation(project(":mechanica"))
        }
    }
}
