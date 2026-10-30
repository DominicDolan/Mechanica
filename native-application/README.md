# Linux Native backend

This experimental Kotlin/Native backend uses GLFW and OpenGL, STB for images,
fonts and Vorbis, and OpenAL for audio. `NativeApplication` takes an unpacked
asset root, a persistence root, and a persistence path; it can also take a custom
font factory. The sample `:samples:native-engine` exercises the engine's drawer,
bitmap and SDF fonts. MechanicaGame has a Native run task and asset staging.

The backend currently targets Linux x86-64 with system libraries in the Debian
multiarch locations specified by the C interop definitions. The linker permits
unresolved references in host shared libraries because the Kotlin/Native sysroot
uses an older glibc; the host loader must provide compatible shared libraries.

Known limitations:

- Multi-draw and tessellation shader creation are not implemented (the JVM
  backend also leaves these operations unimplemented).
- Font atlas packing uses a simple row-based algorithm, so a configured atlas may
  need to be larger than the JVM backend's rectangle-packed atlas. Native font
  metadata and GPU textures currently live for the lifetime of the process;
  the engine does not yet expose a font disposal API.
- Persistence keys for top-level properties use a `static` prefix because Kotlin
  Native does not expose the JVM declaring-field reflection used by the JVM
  backend. Such keys may collide, and existing JVM top-level keys are not portable.
- Packaged assets must be provided as files; the Native executable does not embed
  them. A missing audio device causes audio context initialization to fail.
