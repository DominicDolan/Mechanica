
plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":common"))
    api(project(":application-interface"))

    testImplementation(kotlin("test-junit"))
}
