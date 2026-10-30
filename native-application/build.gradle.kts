plugins { kotlin("multiplatform") }

kotlin {
    linuxX64 {
        compilations.getByName("main") {
            cinterops.create("glfw") {
                defFile(project.file("src/nativeInterop/cinterop/glfw.def"))
            }
            cinterops.create("stb") {
                defFile(project.file("src/nativeInterop/cinterop/stb.def"))
            }
            cinterops.create("openal") {
                defFile(project.file("src/nativeInterop/cinterop/openal.def"))
            }
        }
    }
    sourceSets {
        linuxX64Test.dependencies { implementation(kotlin("test")) }
        linuxX64Main.dependencies {
            api(project(":application-interface"))
            api(project(":mechanica-shaders"))
        }
    }
}
