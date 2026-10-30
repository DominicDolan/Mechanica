# Linux Native OpenGL window

A standalone Kotlin/Native and GLFW smoke test. It creates an OpenGL 4.3 core
context, prints the OpenGL version, and draws a triangle through the Native draw interface. Hold Space to change
the background; Escape closes the resizable window.
It exercises Native input callbacks and draw commands; the full application backend is not implemented yet. Close the window to exit.
The linker permits unresolved symbols in the host shared libraries because Kotlin/Native
uses an older glibc sysroot; the host dynamic loader resolves them at runtime.

Install the GLFW and OpenGL development packages (on Debian/Ubuntu:
`sudo apt-get install libglfw3-dev libgl1-mesa-dev libstb-dev libopenal-dev`), then from the Mechanica
repository run:

```sh
./gradlew :samples:native-window:runDebugExecutableLinuxX64
```

The program needs a working graphical display and OpenGL 4.3. To build without
running, use `./gradlew :samples:native-window:linkDebugExecutableLinuxX64`.
