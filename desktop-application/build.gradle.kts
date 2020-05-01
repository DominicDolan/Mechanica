
plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    api(project(":common"))
    implementation(project(":application-interface"))
}

