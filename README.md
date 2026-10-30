## Mechanica, a 2D Game Engine in Kotlin

A powerful 2D Game Engine written in Kotlin. This project is still under development and it is not 
thoroughly tested or documented but feel free to try it out nonetheless.

Get started coding games by checking out the [Wiki](https://github.com/DominicDolan/Mechanica/wiki)
or consider [Contributing](https://github.com/DominicDolan/Mechanica/blob/master/CONTRIBUTING.md)

Examples can be seen in the `samples` module in Mechanica

### Fonts

Add a `.ttf` font to `src/main/resources/fonts/` and load it once, inside a scene
after the OpenGL context has been created. Mechanica builds its atlas at runtime;
no font conversion tool is needed.

```kotlin
import com.mechanica.engine.resources.Resource
import com.mechanica.engine.resources.create
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.shaders.text.Text

val font = Font.create(Resource("fonts/MyFont-Regular.ttf")) {
    characterSize = 48f
    configureSDF { } // omit this for a conventional antialiased bitmap font
}
val label = Text("Hello, Mechanica", font)
// In render(draw): draw.ui.white.text(label, size = 0.55, x = 1.0, y = 1.0)
```

`characterSize` is the atlas glyph height in pixels, independent of the text's
size in game/UI units. Bitmap fonts default to 48 pixels with an eight-pixel
gutter and up to three mip levels to filter smaller text without sampling nearby
glyphs. Prefer a bake size near the sizes you display. At a 720-pixel framebuffer
and a 20-unit viewport height, a text size of `0.55` is about 20 pixels.

SDF fonts use the same text API. The normal drawer detects them and reconstructs
antialiased edges using screen-space derivatives, so the transition follows the
displayed scale. The default distance range is `-4.0..4.0` atlas pixels; customize
it with `configureSDF { start = -4.0; end = 4.0 }`. It must straddle zero. Glyph
padding and bearings are included in the atlas; enlarging the range may require
a larger atlas. SDF textures use linear sampling without bitmap mipmaps.

The default character range is U+0020 through U+0080. Set `charRange` and, if
necessary, `width` / `height` when importing additional characters. This remains
a single-atlas renderer; complex-script shaping and font fallback are not provided.
Custom shaders must decode SDF samples themselves; `font.distanceFieldEdge` gives
the encoded contour threshold, or `null` for a coverage bitmap.

To make an SDF font slightly heavier, use
`configureSDF { weightAdjustment = 0.6 }`. The adjustment expands the contour in
atlas pixels (negative values make it thinner), and the drawer applies it when
reconstructing coverage. Glyph spacing, sizes and the atlas distance samples stay
unchanged. Keep the adjustment within the configured distance range.

### Building from source with Gradle

Clone or download the repository and build with gradle.

Add a `gradle.properties` which looks like this:

```properties
kotlin_cave_home=/home/user/Source/Mechanica/Kotlin-CAVE
lwjgl_natives=natives-linux
```

Building it will require java 12 or later and note that later versions of Java require the latest version of Gradle.

Try and run one of the samples in the samples module to check that everything is working.

To create a new project using this repository, create a new Gradle project and In the `settings.gradle.kts` file add:

```kotlin
includeBuild("C:/path/to/mechanica")
```
And in the `build.gradle.kts` file add a dependency on the project:
```kotlin
dependencies {
    implementation("com.mechanica.engine:mechanica:0.1")
}
```

### Building with JAR dependencies

Download the [latest release zip](https://github.com/DominicDolan/Mechanica/releases). The ZIP contains the Mechanica JARs with all of its dependencies.
Create a new project and set the dependencies to be whatever is needed from these JARs.

Building it will require java 12 or later.

### Running a Game

In the new project create a main method with something similar to the following:
```kotlin
fun main() {
    Game.configure {
        setViewport(height = 10.0)
    }

    // Create an instance of the Drawer class,
    // it can be used to draw anything from rounded rectangles to custom shaders
    val draw = Drawer.create()

    // Start the game loop and draw text to the screen
    Game.run {
        draw.centered.grey.text("Hello, Mechanica")
    }
}
```
