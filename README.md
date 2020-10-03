## Mechanica, a 2D Game Engine in Kotlin

A powerful 2D Game Engine written in Kotlin. This project is still under development and it is not 
thoroughly tested or documented but feel free to try it out nonetheless.

Get started coding games by checking out the [Wiki](https://github.com/DominicDolan/Mechanica/wiki)
or consider [Contributing](https://github.com/DominicDolan/Mechanica/blob/master/CONTRIBUTING.md)

Examples can be seen in the `samples` module in Mechanica

### Building from source with Gradle

Clone or download the repository and build with gradle.

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
