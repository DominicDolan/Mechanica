
plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.mechanica.engine.samples.temp.SmallKt"
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))
}