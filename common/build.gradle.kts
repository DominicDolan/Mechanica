dependencies {
    testImplementation(kotlin("test-junit"))
    testImplementation(project(":mechanica"))
    testImplementation(project(":desktop-application"))

}

plugins {
    kotlin("jvm")
}