plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))

    api(files("libs/DukeUI-0.1.jar"))

}