
plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.mechanica.engine.samples.color.MainKt"
}

dependencies {
    implementation(project(":common"))
    implementation(project(":backend-lwjgl"))
    implementation(project(":mechanica"))
}