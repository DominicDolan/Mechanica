package util.extensions

import util.units.Vector

fun Iterable<Vector>.containsPoint(point: Vector): Boolean {
    var result = false
    var prev = this.last()
    
    for (vec in this){
        if ((vec.y > point.y) != (prev.y > point.y) &&
                (point.x < (prev.x - vec.x) * (point.y - vec.y) / (prev.y-vec.y) + vec.x)) {
            result = !result
        }
        prev = vec
    }
    return result
}