
plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.mechanica.engine.samples.color.MainKt"
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))
}