# Native engine geometry experiment

This Linux Native sample initializes the Mechanica game loop and uses its Drawer
for a green rectangle and bitmap and signed-distance-field text.
It expects OpenGL 4.3, GLFW, OpenAL and STB development packages. From the
Mechanica repository:

```sh
./gradlew :samples:native-engine:runDebugExecutableLinuxX64
```

Use `ALSOFT_DRIVERS=null` before the command if testing on a system without an
audio device. Persistence is stored under `/tmp/mechanica-native-demo`.
