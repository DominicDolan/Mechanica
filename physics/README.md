# Physics

This module starts a Kotlin Multiplatform wrapper around Box2D 3.1.1. It targets
JVM on Linux x64 and Kotlin/Native `linuxX64`, using the same pinned Box2D source.
The first build downloads the GitHub release archive and verifies its SHA-256;
subsequent builds can reuse the cached archive under `physics/build`. Building
requires a Linux x64 host, network access for the first download, CMake 3.22 or
newer, a C/C++ compiler, and JDK 22 for Gradle. The JVM target additionally
uses JNI headers and builds its JNI bridge only when that target is requested.

```sh
./gradlew :physics:jvmTest :physics:linuxX64Test
```

The JVM JAR includes the JNI shared library and extracts it to a temporary file
at runtime. Kotlin/Native links static Box2D and bridge libraries into the final
executable. Box2D is MIT licensed; its license is in `BOX2D-LICENSE`, is included
in the JVM JAR, and must accompany distributed native executables too.

Create bodies with a Kotlin builder. Shape centers and box angles are local to
the body; body position and angle are in world space. Angles use Kotlin-CAVE's
`Angle` type and may differ slightly from the input due to Box2D's approximations.

```kotlin
import com.cave.library.angle.radians

val world = PhysicsWorld(Vector2.create(0.0, -9.81))
val ground = world.buildBody {
    type = BodyType.Static
    angle = 0.2.radians
    buildShape { box(width = 30.0, height = 0.5); friction = 1.0 }
}
val block = world.buildBody {
    type = BodyType.Dynamic
    position.set(0.0, 4.0)
    angle = 0.2.radians
    buildShape { box(width = 1.0, height = 1.0); density = 2.0; friction = 1.0 }
}
val physics = FixedStep(world)
// For each frame: physics.update(frameSeconds)
block.linearVelocity.set(1.0, 0.0) // live view; writes through to Box2D
// When finished: world.close()
```

`buildShape` supports circles and boxes, with a local center, density, friction
and restitution. Box dimensions are full width and height. Every dynamic body
must have at least one shape with positive density. Definitions are validated
before a body is created. Position is a snapshot; `setTransform` explicitly
teleports the body. `linearVelocity` is a stable live view: component writes,
`set`, and `scale` update Box2D immediately. Copy with `Vector2.create(view)` for
an independent snapshot. Static-body velocity writes fail rather than being
silently ignored. Destroying a body or closing its world invalidates its views.

`FixedStep.update` is a standalone physics clock: it accumulates frame time,
caps catch-up, and exposes `interpolationAlpha` for rendering. Do not nest it
inside a fixed-step game calculator: that would introduce two independent clocks.

For a Mechanica game, select the fixed-step calculator during configuration and
call `world.step(delta)` once from each scene update:

```kotlin
setDeltaTimeCalculator(DeltaCalculator.fixedStepCalculator())

// In the scene's update(delta):
world.step(delta)

// In render(draw), for each visible part:
val transform = body.renderTransform(Game.deltaCalculator.interpolationAlpha)
// Draw the part using transform.position and transform.angle.
```

The default calculator runs at 120 updates per second with at most eight updates
per frame; any excess elapsed time is discarded. It displays current transforms
while paused after a single-step or clock resynchronization. The world records
previous and current transforms for every dynamic body around each physics step;
static bodies need no per-step capture. Render transforms interpolate these
snapshots, with shortest-path rotation. New and teleported bodies reset both
snapshots to the same transform. Interpolation normally displays motion one
simulation step behind the latest state; use the same alpha for the camera and
all connected vehicle parts. Transform tracking is render-only and never changes
the physics simulation.

For suspended wheels, `world.buildWheelJoint(chassis, wheel) { ... }` connects
bodies in the same world. `anchor` and `axis` are in world coordinates at
creation; the anchor defaults to the wheel's position and the axis defaults to
up. Configure `springHertz`, `dampingRatio`, `lowerTranslation`, and
`upperTranslation` in the builder. The wheel can rotate freely; a powered motor
is not yet exposed. Destroying a body also destroys its attached joints.

A world owns its bodies; close it when finished. Individual bodies can be
`destroy()`ed. Angular velocity remains a `Double` in radians per second, since
Kotlin-CAVE does not define an angular-velocity type. All access, including vector views, must be confined to the
simulation thread. CAVE vectors use doubles; Box2D uses floats, so conversion
occurs at the boundary.

## Roadmap to a vehicle rolling down a hill

1. **Done:** pin Box2D, connect JVM and Linux Native, and run the same gravity
   simulation through both backends and through an independent composite consumer.
2. **Done:** body and shape builders, fixed-step scheduling, transforms and a
   live velocity view; a shared test settles a body on an inclined surface.
3. **Done:** wheel joints and suspension, with a two-wheel chassis tested rolling
   down a slope. Wheel motors and editable vehicle definitions remain future work.
4. **Done:** MechanicaGame renders the slope and interpolated body transforms,
   steps at a fixed rate, and cleans up its world on scene removal.


## Open terrain chains

`world.createTerrainChain(points, friction)` creates an owned static body with an
open Box2D 3 chain. Points use world coordinates; the collidable face is to the
**right** of each directed edge. Supply a right-to-left floor for upward collision.
The wrapper adds extrapolated ghost vertices so the first and last real edges
also collide. Points must be finite and consecutive points at least 1cm apart.
Callers must avoid self-intersections and overlapping/backtracking edges.
Destroy the returned body or close its world to release the chain.

`body.setFixedRotation(true)` keeps a character upright. `world.groundDistance`
casts downward against static surfaces whose normal has y >= 0.45, ignoring
dynamic bodies; it returns a distance or null. It is a small walkable-ground query,
not a general contact or sensor API.

Connected streamed chains require explicit shared neighbour points; extrapolated
endpoint ghosts alone do not provide continuity across independently loaded chains.
