plugins { kotlin("multiplatform") }

kotlin {
    jvmToolchain(22)
    jvm()
    linuxX64()
    sourceSets {
        commonMain {
            kotlin.srcDir("src/main/kotlin")
            resources.srcDir("src/main/resources")
            dependencies {
                api(project(":common"))
                api(project(":mechanica-shaders"))
                api("com.cave.library:Kotlin-CAVE:0.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
            }
        }
        jvmTest {
            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
            dependencies { implementation(kotlin("test-junit")) }
        }
    }
}

tasks.named<Test>("jvmTest") { useJUnit() }
tasks.register("test") { dependsOn("jvmTest") }
