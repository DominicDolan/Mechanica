# Kotlin/Native OpenGL feasibility probe

This initial probe has been superseded by the working `samples/native-window` and `samples/native-engine` examples.

This is a standalone Linux off-screen OpenGL smoke test, not a Mechanica backend or a game port. It creates an OSMesa context, clears it red, and checks the resulting pixel. It uses Kotlin/Native's C interop instead of LWJGL. It requires Kotlin/Native, the compiler's downloaded LLVM/sysroot dependencies, and OSMesa development headers and library (for example `libosmesa6-dev` on Debian). Run from this directory:

```sh
cinterop -def osmesa.def -o osmesa
kotlinc-native main.kt -library osmesa.klib -o probe
./probe.kexe
```

This probe has not yet been compiled: in the environment where it was written, Kotlin/Native 1.9.0 needed to download its native dependencies and network socket creation was denied. The game uses Kotlin 2.2.0; a real port should use a matching Kotlin/Native toolchain.

## Porting work uncovered

The checked-out Mechanica and MechanicaGame builds apply the JVM plugin, not the multiplatform plugin. Replacing the desktop backend alone cannot compile the game for Native:

- `common`, `application-interface`, `mechanica-shaders`, and `mechanica` expose JVM types, including `java.nio` buffers, streams, filesystem APIs and AWT images. These need portable interfaces and platform implementations.
- `mechanica-ui` depends directly on `desktop-application` and uses `java.util.IdentityHashMap`; the game UI also uses ImageIO and LWJGL directly.
- The engine build supplies `libs/Kotlin-CAVE-jvm-0.2.jar`; a Native-compatible Kotlin-CAVE build or replacement is required. DukeCompose and the game modules also need Native targets.
- A real desktop backend still needs window/context creation, input, resource loading, fonts, audio, and OpenGL bindings; this probe only tests one graphics call path.

A practical next step is to build Kotlin-CAVE and the shared modules as multiplatform libraries, replacing JVM types at their boundaries, then implement a Linux backend using C interop for a windowing library and OpenGL. Keep the JVM targets working during the migration.
