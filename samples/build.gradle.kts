
plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    application
}

application {
    mainClass.set("com.mechanica.engine.samples.drawer.DrawerMiniDemoKt")
}

dependencies {
    implementation(project(":application-interface"))
    implementation(project(":desktop-application"))
    implementation(project(":mechanica"))
    implementation(project(":mechanica-ui"))
}
// The DukeCompose sample. `application` can only carry one mainClass, and that is the drawer demo.
tasks.register<JavaExec>("runComposeUI") {
    group = "application"
    description = "Runs the DukeCompose UI sample scene."
    mainClass.set("com.mechanica.engine.samples.ui.ComposeUISceneKt")
    classpath = sourceSets["main"].runtimeClasspath
}
