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
                api(project(":application-interface"))
                api(project(":mechanica"))
                api("com.cave.library:Kotlin-CAVE:0.2")
                api("com.dubulduke:layout:1.0-SNAPSHOT")
                api("com.dubulduke:dsl:1.0-SNAPSHOT")
                api("com.dubulduke:utils:1.0-SNAPSHOT")
            }
        }
        jvmTest {
            kotlin.srcDir("src/test/kotlin")
            resources.srcDir("src/test/resources")
            dependencies { implementation(kotlin("test")) }
        }
    }
}

tasks.named<Test>("jvmTest") { useJUnitPlatform() }
tasks.register("test") { dependsOn("jvmTest") }
