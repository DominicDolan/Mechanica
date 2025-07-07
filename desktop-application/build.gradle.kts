
plugins {
    kotlin("jvm")
    `java-library`
}

val lwjglVersion = "3.3.4"
val lwjglNatives = "natives-windows"


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
}

