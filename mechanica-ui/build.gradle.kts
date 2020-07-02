
plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":mechanica"))

    api("com.dubulduke.ui:DukeUI:0.1")

}