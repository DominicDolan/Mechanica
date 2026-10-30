
plugins {
    kotlin("jvm")
    `java-library`
}

val lwjglVersion = "3.3.4"
val lwjglNatives = providers.gradleProperty("lwjgl_natives").get()

val coreLwjgl: DependencyHandlerScope.() -> Unit = {
    //lwjgl
    api(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-opengl")
    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
}

val supplementaryLwjgl: DependencyHandlerScope.() -> Unit = {
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-stb")
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
}

dependencies {
    api(project(":common"))
    implementation(project(":application-interface"))
    coreLwjgl()
    supplementaryLwjgl()
    testImplementation(kotlin("test-junit"))
}

// Reuse the bundled engine font as a packing fixture without needing an OpenGL context.
sourceSets.test {
    resources.srcDir("../mechanica/src/main/resources")
    resources.include("res/fonts/roboto/Roboto-Regular.ttf")
}

