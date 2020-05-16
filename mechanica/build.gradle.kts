
plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":common"))
    api(project(":application-interface"))
    implementation(project(":desktop-application"))
}
