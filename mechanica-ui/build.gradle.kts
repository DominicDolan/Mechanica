plugins {
    kotlin("jvm")
    `java-library`
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))

    // DukeCompose, resolved by the composite build declared in settings.gradle.kts — the version
    // is only there to satisfy the notation, dependency substitution ignores it.
    //
    // `api`, not `implementation`: an application writing a UI names Element, ElementTree and the
    // layout expressions directly, so they belong on its compile classpath. `:layout` re-exports
    // `:reactivity` for the same reason.
    api("com.dubulduke:layout:1.0-SNAPSHOT")
    // The authoring surface. `api` for the same reason: a scene names ElementScope directly.
    api("com.dubulduke:dsl:1.0-SNAPSHOT")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
