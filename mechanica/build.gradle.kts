
plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":common"))
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
}
