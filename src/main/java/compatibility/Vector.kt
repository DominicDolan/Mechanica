package compatibility

import com.vividsolutions.jts.geom.Coordinate
import org.jbox2d.common.Vec2
import org.joml.Vector2f
import org.joml.Vector3f


/**
 * Created by domin on 01/11/2017.
 */
class Vector(x: Double, y: Double) {
    var x = 0.0
        set(value) {
            coordinate.x = value
            vector2f.x = value.toFloat()
            vector3f.x = value.toFloat()
            vec2.x = value.toFloat()
            field = value
        }

    var y = 0.0
        set(value) {
            coordinate.y = value
            vector2f.y = value.toFloat()
            vector3f.y = value.toFloat()
            vec2.y = value.toFloat()
            field = value
        }

    private val coordinate: Coordinate
    private val vector2f: Vector2f
    private val vector3f: Vector3f
    private val vec2: Vec2

    constructor(coordinate: Coordinate) : this(coordinate.x,          coordinate.y         )
    constructor(vector2f: Vector2f)     : this(vector2f.x.toDouble(), vector2f.y.toDouble())
    constructor(vector3f: Vector3f)     : this(vector3f.x.toDouble(), vector3f.y.toDouble())
    constructor(vec2: Vec2)             : this(vec2.x.toDouble(),     vec2.y.toDouble()    )

    fun set(coordinate: Coordinate) { this.x = coordinate.x;          this.y = coordinate.y          }
    fun set(vector2f: Vector2f)     { this.x = vector2f.x.toDouble(); this.y = vector2f.y.toDouble() }
    fun set(vector3f: Vector3f)     { this.x = vector3f.x.toDouble(); this.y = vector3f.y.toDouble() }
    fun set(vec2: Vec2)             { this.x = vec2.x.toDouble();     this.y = vec2.y.toDouble()     }

    init {
        this.x = x
        this.y = y

        coordinate = Coordinate(x, y)
        vector2f = Vector2f(x.toFloat(), y.toFloat())
        vector3f = Vector3f(x.toFloat(), y.toFloat(), 0f)
        vec2 = Vec2(x.toFloat(), y.toFloat())
    }

    fun toCoordinate() = coordinate
    fun toVector2f() = vector2f
    fun toVector3f() = vector3f
    fun toVec2() = vec2
}