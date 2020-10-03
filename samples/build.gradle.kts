
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClassName = "com.mechanica.engine.samples.basic.BasicDemoKt"
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))
}