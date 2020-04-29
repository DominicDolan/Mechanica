
plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "temp.TempKt"
}

dependencies {
    implementation(project(":common"))
}

