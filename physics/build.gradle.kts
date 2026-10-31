import java.net.URI
import java.security.MessageDigest

plugins { kotlin("multiplatform") }

val box2dVersion = "3.1.1"
val box2dSha256 = "fb6ef914b50f4312d7d921a600eabc12318bb3c55a0b8c0b90608fa4488ef2e4"
val box2dArchive = layout.buildDirectory.file("downloads/box2d-$box2dVersion.tar.gz")
val box2dSource = layout.buildDirectory.dir("box2d-source")
val nativeBuild = layout.buildDirectory.dir("native-build")
val generatedDef = layout.buildDirectory.file("generated/cinterop/physics.def")

val downloadBox2d by tasks.registering {
    outputs.file(box2dArchive)
    doLast {
        val destination = box2dArchive.get().asFile
        destination.parentFile.mkdirs()
        val temporary = destination.resolveSibling(destination.name + ".part")
        try {
            val connection = URI("https://github.com/erincatto/box2d/archive/refs/tags/v$box2dVersion.tar.gz").toURL().openConnection().apply {
                connectTimeout = 30_000
                readTimeout = 30_000
            }
            connection.getInputStream().use { input ->
                temporary.outputStream().use { input.copyTo(it) }
            }
            val digest = MessageDigest.getInstance("SHA-256").digest(temporary.readBytes()).joinToString("") { "%02x".format(it) }
            check(digest == box2dSha256) { "Box2D archive checksum mismatch: $digest" }
            temporary.copyTo(destination, overwrite = true)
        } finally { temporary.delete() }
    }
}

val unpackBox2d by tasks.registering(Sync::class) {
    dependsOn(downloadBox2d)
    from({ tarTree(resources.gzip(box2dArchive.get().asFile)) }) {
        eachFile { path = path.substringAfter('/') }
        includeEmptyDirs = false
    }
    into(box2dSource)
}

val configurePhysics by tasks.registering(Exec::class) {
    dependsOn(unpackBox2d)
    inputs.dir("native")
    inputs.dir(box2dSource)
    outputs.file(nativeBuild.map { it.file("CMakeCache.txt") })
    doFirst {
        check(System.getProperty("os.name").lowercase().contains("linux") && System.getProperty("os.arch") in setOf("amd64", "x86_64")) {
            "Building Mechanica physics currently requires a Linux x64 host"
        }
        nativeBuild.get().asFile.mkdirs()
    }
    commandLine("cmake", "-S", file("native"), "-B", nativeBuild.get().asFile,
        "-DBOX2D_SOURCE=${box2dSource.get().asFile.absolutePath}", "-DCMAKE_BUILD_TYPE=Release",
        "-DJAVA_HOME=${System.getProperty("java.home")}")
}

val buildPhysics by tasks.registering(Exec::class) {
    dependsOn(configurePhysics)
    inputs.dir("native")
    inputs.dir(box2dSource)
    outputs.files(nativeBuild.map { it.file("libmechanica_physics.a") },
        nativeBuild.map { it.file("box2d/src/libbox2d.a") },
        nativeBuild.map { it.file("libmechanica_physics_jni.so") })
    commandLine("cmake", "--build", nativeBuild.get().asFile, "--parallel", "2")
}

val generatePhysicsDef by tasks.registering {
    dependsOn(buildPhysics)
    inputs.files(buildPhysics)
    outputs.file(generatedDef)
    doLast {
        generatedDef.get().asFile.apply {
            parentFile.mkdirs()
            writeText("""
                headers = physics_bridge.h
                headerFilter = physics_bridge.h
                package = com.mechanica.engine.physics.binding
                compilerOpts = -I${file("native").absolutePath}
                staticLibraries = libmechanica_physics.a libbox2d.a
                libraryPaths = ${nativeBuild.get().asFile.absolutePath} ${nativeBuild.get().dir("box2d/src").asFile.absolutePath}
                linkerOpts = -lm
            """.trimIndent())
        }
    }
}

kotlin {
    jvmToolchain(22)
    jvm()
    linuxX64 {
        compilations.getByName("main") {
            cinterops.create("physics") { defFile(generatedDef.get().asFile) }
        }
    }
    sourceSets {
        commonMain.dependencies {
            api("com.cave.library:Kotlin-CAVE:0.2")
            implementation(project(":common"))
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":mechanica"))
        }
    }
}

tasks.named("cinteropPhysicsLinuxX64") {
    dependsOn(generatePhysicsDef)
    // The archives are embedded in the cinterop klib, even when the header and
    // generated definition have not changed.
    inputs.files(nativeBuild.map { it.file("libmechanica_physics.a") },
        nativeBuild.map { it.file("box2d/src/libbox2d.a") })
}
tasks.named<ProcessResources>("jvmProcessResources") {
    dependsOn(buildPhysics)
    from(nativeBuild.map { it.file("libmechanica_physics_jni.so") }) {
        into("com/mechanica/engine/physics/linux-x64")
    }
    from("BOX2D-LICENSE") { into("META-INF/licenses") }
}
