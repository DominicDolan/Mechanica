package com.mechanica.engine.samples.triangulation

import com.cave.library.vector.vec2.Vector2
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

private const val EPSILON = 1e-9

/** A single triangle, for drawing. Physics only ever sees [WallQuad.corners]. */
data class Triangle(val a: Vector2, val b: Vector2, val c: Vector2)

/**
 * One wall segment: the quad lining the inside of a single outline edge.
 *
 * [outerFrom] and [outerTo] are the outline edge itself, so they are shared exactly with the
 * neighbouring quads; [innerFrom] and [innerTo] are the corners those two ends were pushed in to.
 * The four are in order round the quad, which is what a physics polygon shape wants.
 */
class WallQuad(
    val outerFrom: Vector2,
    val outerTo: Vector2,
    val innerTo: Vector2,
    val innerFrom: Vector2,
) {
    val corners: List<Vector2>
        get() = listOf(outerFrom, outerTo, innerTo, innerFrom)

    /**
     * The quad as triangles, for drawing it.
     *
     * Normally that is the two halves either side of a diagonal. But in a pinched region the two
     * ends can be pushed in far enough to cross over, leaving the quad a bowtie; splitting a bowtie
     * across its diagonal covers neither lobe and leaves a hole in the middle. Splitting it at the
     * crossing instead gives exactly the two lobes. A physics engine that hulls the corners never
     * sees the difference — see [toFixture] — but a renderer does.
     */
    val triangles: List<Triangle>
        get() {
            val crossing = seamCrossing(outerFrom, innerFrom, outerTo, innerTo)
            return if (crossing == null) {
                listOf(Triangle(outerFrom, outerTo, innerTo), Triangle(outerFrom, innerTo, innerFrom))
            } else {
                listOf(Triangle(outerFrom, outerTo, crossing), Triangle(crossing, innerTo, innerFrom))
            }
        }
}

/** A wall lining the inside of a polygon outline. */
class WallBand(
    /**
     * The wall along the outline edges, in order. One per edge, except where an edge had to be
     * broken to step around something — see [addEdgeQuads].
     */
    val quads: List<WallQuad>,
    /** The quads bridging the turn at each reflex corner. Empty on a fully convex outline. */
    val caps: List<WallQuad>,
    /** The inner boundary of the wall: `inner[i]` is outline vertex `i` pushed inwards. */
    val inner: List<Vector2>,
    /** Set where a corner had to be cut short of its true miter point. */
    val limited: BooleanArray,
) {
    /** Everything that becomes a physics fixture. */
    val pieces: List<WallQuad>
        get() = quads + caps

    val triangles: List<Triangle>
        get() = pieces.flatMap { it.triangles }

    val limitedCount: Int
        get() = limited.count { it }

    companion object {
        val empty = WallBand(emptyList(), emptyList(), emptyList(), BooleanArray(0))
    }
}

/**
 * Default cap on how far a corner may reach, as a multiple of the wall thickness.
 *
 * A corner needs `1 / sin(θ/2)` thicknesses to close properly, so this is the sharpest corner that
 * still closes: about 29°. Below that the corner is cut short and a corner cap covers the rest.
 * Tighter than this starts opening gaps at the points of a star; looser mostly costs spill.
 */
const val DEFAULT_MITER_LIMIT = 4.0

/** Widest turn one corner cap may span, so a wide reflex corner gets several rather than one. */
private const val MAX_CAP_SWEEP = PI / 3.0

/**
 * Lines the inside of [outline] with a wall of the given [thickness], as quads.
 *
 * [outline] must be a simple polygon — it must not cross itself. See [selfIntersections] for what
 * goes wrong if it does, and why that is worth checking before calling this rather than after.
 *
 * The two long sides of a quad are its outline edge and that same edge pushed [thickness] straight
 * inwards. The short sides are the interesting part. Rather than giving every quad its own pair of
 * end caps, the corner where two neighbouring quads meet is a *single* point: the miter point,
 * where the two inset edges intersect. Both quads are built from that one point, so the seam
 * between them is one shared edge rather than two edges that nearly line up, and the wall closes up
 * with no seam to slip through. Nothing about that depends on the corner being convex — a reflex
 * corner finds its miter point exactly the same way, on the other side of the vertex.
 *
 * A miter point sits `thickness / sin(θ/2)` from a vertex whose interior angle is θ, so it runs
 * away as the corner sharpens — unbounded at a needle — and on a polygon thinner than the wall it
 * lands outside. Two things bound it. It is cut to the first piece of outline its own bisector
 * reaches, which is what stops the wall spilling into open air, and [miterLimit] caps it at a
 * multiple of [thickness] regardless, which catches needles. Both cut along the same bisector, so
 * the point stays shared and the quads stay well formed.
 *
 * Reflex corners are the exception to that sharing, and get quads of their own in [WallBand.caps]
 * to bridge the turn — see the reflex branch in the body for why sharing goes wrong there.
 *
 * An edge is also broken into more than one quad wherever the polygon doubles back within
 * [thickness] of it, so the wall steps around the obstruction rather than ploughing through it.
 * See [addEdgeQuads]. Straight, open edges — nearly all of them — stay a single quad.
 *
 * ### What this optimises for
 *
 * This is built for collision, not for drawing, and those want opposite things. Overlapping pieces
 * cost a physics engine nothing, while a gap is the whole bug being fixed — something thin slips
 * through it. So everything here is biased toward covering too much.
 *
 * That bias is also why nothing tries to keep the quads convex, or even un-crossed, where the
 * polygon is pinched: the consumer replaces each one with its convex hull, which covers more, not
 * less. See [toFixture]. Handing a renderer these quads works too, but only because
 * [WallQuad.triangles] handles the crossed case separately.
 */
fun buildWalls(
    outline: List<Vector2>,
    thickness: Double,
    miterLimit: Double = DEFAULT_MITER_LIMIT
): WallBand {
    val count = outline.size
    if (count < 3 || thickness <= 0.0) return WallBand.empty

    // Work counter-clockwise so the interior is always on the left of a directed edge, which is
    // what makes the inward normal below unambiguous.
    val ring = if (isCounterClockwise(outline)) outline else outline.reversed()

    // Inward unit normal of edge i, running from ring[i] to ring[i + 1].
    val normalX = DoubleArray(count)
    val normalY = DoubleArray(count)
    for (i in 0 until count) {
        val from = ring[i]
        val to = ring[(i + 1) % count]
        val length = hypot(to.x - from.x, to.y - from.y)
        if (length > EPSILON) {
            normalX[i] = -(to.y - from.y) / length
            normalY[i] = (to.x - from.x) / length
        }
    }

    val maxReach = miterLimit * thickness
    val inner = ArrayList<Vector2>(count)
    val limited = BooleanArray(count)
    val reflex = BooleanArray(count)
    // The corner the quad arriving at each vertex ends on, and the one the quad leaving it starts
    // from. The same point in every ordinary case — see the reflex branch below for when it isn't.
    val arriving = arrayOfNulls<Vector2>(count)
    val leaving = arrayOfNulls<Vector2>(count)

    for (i in 0 until count) {
        val previous = (i + count - 1) % count
        val alignment = normalX[previous] * normalX[i] + normalY[previous] * normalY[i]
        val turn = normalX[previous] * normalY[i] - normalY[previous] * normalX[i]

        var directionX = 0.0
        var directionY = 0.0
        var distance = 0.0

        if (1.0 + alignment > EPSILON) {
            // Scaling the sum of the two normals like this lands exactly `thickness` from both
            // edges at once — dotting the result with either normal gives back `thickness`,
            // whatever the angle between them. That is the miter point.
            val scale = thickness / (1.0 + alignment)
            val offsetX = (normalX[previous] + normalX[i]) * scale
            val offsetY = (normalY[previous] + normalY[i]) * scale
            distance = hypot(offsetX, offsetY)
            if (distance > EPSILON) {
                directionX = offsetX / distance
                directionY = offsetY / distance
            } else {
                distance = 0.0
            }
        } else {
            // The two edges double straight back on each other, so their inset edges are parallel
            // and never meet: a zero-width spike. Head back down the spike, as far as allowed.
            val from = ring[previous]
            val to = ring[i]
            val length = hypot(to.x - from.x, to.y - from.y)
            if (length > EPSILON) {
                directionX = -(to.x - from.x) / length
                directionY = -(to.y - from.y) / length
                distance = maxReach
            }
        }

        val cutTo = minOf(maxReach, distanceToOutline(ring, i, directionX, directionY))
        if (cutTo < distance) {
            distance = cutTo
            limited[i] = true
        }
        // Every reflex corner, whether or not its miter had to be cut. Capping cut *convex*
        // corners as well was measured and was pure cost: no gaps closed, three times the spill.
        if (turn < 0.0) reflex[i] = true
        if (!distance.isFinite()) distance = thickness

        val miter = Vector2.create(ring[i].x + directionX * distance, ring[i].y + directionY * distance)

        if (reflex[i]) {
            // Reflex corners do not get to share a corner point, for two reasons that both end in
            // the wall going somewhere it should not.
            //
            // A wide reflex corner's bisector points away from both edges, so a point on it adds no
            // thickness to either neighbour: share it and both quads taper to nothing along their
            // whole length, not just at the corner, which is what tears the wall open beside a slit.
            //
            // And even at an ordinary right-angled one, the miter sits out diagonally, so a quad
            // anchored to it runs diagonally too — across the notch beside it and out the far side.
            // That was most of the spill around a comb's teeth.
            //
            // Both go away if each quad simply takes its own edge's perpendicular offset and keeps
            // full thickness, with the caps below bridging the turn between them.
            arriving[i] = offsetAlong(ring, i, normalX[previous], normalY[previous], thickness)
            leaving[i] = offsetAlong(ring, i, normalX[i], normalY[i], thickness)
        } else {
            arriving[i] = miter
            leaving[i] = miter
        }
        inner.add(miter)
    }

    val quads = ArrayList<WallQuad>(count)
    for (i in 0 until count) {
        val next = (i + 1) % count
        addEdgeQuads(ring, i, leaving[i]!!, arriving[next]!!, normalX[i], normalY[i], thickness, quads)
    }

    val caps = ArrayList<WallQuad>()
    for (i in 0 until count) {
        if (!reflex[i]) continue
        val previous = (i + count - 1) % count
        addCaps(ring, i, normalX[previous], normalY[previous], normalX[i], normalY[i], thickness, caps)
    }

    return WallBand(quads, caps, inner, limited)
}

/**
 * Covers the turn a cut-short reflex corner left open, as quads.
 *
 * A reflex corner has to sweep the whole turn between the two edge normals, and a point only covers
 * a wedge, so once the corner has been cut short the rest of that sweep is simply missing — which
 * is how a slit almost closed on itself tears open at its tip. The sweep is filled by walking round
 * it at [thickness] from the vertex, cutting each step to stay inside the outline, and taking the
 * steps in threes so every piece is a quad: the vertex, the two ends, and the point between them.
 * A wide corner needs several, which is why [MAX_CAP_SWEEP] exists — one quad spanning 180° would
 * be a flat sliver covering almost none of it.
 */
private fun addCaps(
    ring: List<Vector2>,
    vertexIndex: Int,
    fromNormalX: Double,
    fromNormalY: Double,
    toNormalX: Double,
    toNormalY: Double,
    thickness: Double,
    into: MutableList<WallQuad>
) {
    val start = atan2(fromNormalY, fromNormalX)
    val alignment = fromNormalX * toNormalX + fromNormalY * toNormalY
    val turn = fromNormalX * toNormalY - fromNormalY * toNormalX
    // Signed, and negative here because the corner is reflex: the sweep goes the short way round.
    val sweep = atan2(turn, alignment)

    val pieces = maxOf(1, ceil(abs(sweep) / MAX_CAP_SWEEP).toInt())
    for (piece in 0 until pieces) {
        val from = start + sweep * piece / pieces
        val to = start + sweep * (piece + 1) / pieces
        val middle = (from + to) / 2.0
        into.add(
            WallQuad(
                ring[vertexIndex],
                offsetAlong(ring, vertexIndex, cos(from), sin(from), thickness),
                offsetAlong(ring, vertexIndex, cos(middle), sin(middle), thickness),
                offsetAlong(ring, vertexIndex, cos(to), sin(to), thickness),
            )
        )
    }
}

/**
 * Lays the wall along one outline edge, bending it around anything in the way.
 *
 * A quad is a straight-sided slab spanning a whole edge, so where the polygon bends back on itself
 * within [thickness] — the far side of a comb tooth, the other wall of a narrow passage — the slab
 * runs straight through it and out the far side. That is collision in open air, and it is the one
 * thing clamping the corners cannot fix, because the corners are fine and it is the middle of the
 * slab that escapes.
 *
 * So the edge is broken at every vertex lying inside the slab, and each piece is taken only as deep
 * as there is polygon to take it, measured straight in. The wall then steps around the obstruction
 * instead of ploughing through it. Any number of obstructions works; each adds one more piece.
 *
 * The pieces are kept separate rather than joined into one bent polygon on purpose. Bending inwards
 * makes a *concave* shape, and jbox2d would hull it straight again — restoring the exact leak this
 * removes. Convex pieces survive hulling untouched. They still share their edges, so the wall has
 * no seam through it.
 */
private fun addEdgeQuads(
    ring: List<Vector2>,
    edgeIndex: Int,
    innerStart: Vector2,
    innerEnd: Vector2,
    normalX: Double,
    normalY: Double,
    thickness: Double,
    into: MutableList<WallQuad>
) {
    val count = ring.size
    val from = ring[edgeIndex]
    val to = ring[(edgeIndex + 1) % count]
    val edgeX = to.x - from.x
    val edgeY = to.y - from.y
    val lengthSquared = edgeX * edgeX + edgeY * edgeY
    if (lengthSquared < EPSILON) return

    // Breaking the edge too close to an end, or twice in nearly the same place, leaves a piece with
    // no area in it. jbox2d cannot hull three points that are all but coincident, and quietly
    // substitutes a 1x1 box when it fails — so a break has to be worth making or not made at all.
    val length = sqrt(lengthSquared)
    val minimumPiece = 0.05 * thickness

    // Every vertex sitting inside the slab is somewhere the wall has to step around.
    val breaks = ArrayList<Double>()
    for (k in 0 until count) {
        if (k == edgeIndex || k == (edgeIndex + 1) % count) continue
        val point = ring[k]
        val relativeX = point.x - from.x
        val relativeY = point.y - from.y

        val depth = relativeX * normalX + relativeY * normalY
        if (depth <= EPSILON || depth >= thickness) continue

        val along = (relativeX * edgeX + relativeY * edgeY) / lengthSquared
        if (along * length <= minimumPiece || (1.0 - along) * length <= minimumPiece) continue
        breaks.add(along)
    }

    breaks.sort()
    var lastKept = Double.NEGATIVE_INFINITY
    breaks.retainAll { along ->
        if ((along - lastKept) * length <= minimumPiece) false else { lastKept = along; true }
    }

    if (breaks.isEmpty()) {
        into.add(WallQuad(from, to, innerEnd, innerStart))
        return
    }

    fun depthAt(along: Double) =
        minOf(
            thickness,
            distanceFromPoint(
                ring,
                edgeIndex,
                Vector2.create(from.x + edgeX * along, from.y + edgeY * along),
                normalX,
                normalY
            )
        )

    // Breaking at the obstructing vertices is not on its own enough. Between two of them the piece
    // is still a straight slab, and where the outline curves away in between — the inside of a
    // star's point, say — the slab cuts the corner and escapes again. So each piece is checked in
    // the middle, and broken again if there is less room there than the piece assumes. Twice is
    // enough to catch it without turning one edge into a dozen fixtures.
    val startDepth = (innerStart.x - from.x) * normalX + (innerStart.y - from.y) * normalY
    val endDepth = (innerEnd.x - to.x) * normalX + (innerEnd.y - to.y) * normalY

    repeat(2) {
        val refined = ArrayList<Double>(breaks.size * 2)
        var previous = 0.0
        var previousDepth = startDepth

        for (index in 0..breaks.size) {
            val next = if (index < breaks.size) breaks[index] else 1.0
            val nextDepth = if (index < breaks.size) depthAt(next) else endDepth

            val middle = (previous + next) / 2.0
            val assumed = (previousDepth + nextDepth) / 2.0
            if ((next - previous) * length > 2.0 * minimumPiece && depthAt(middle) < assumed - EPSILON) {
                refined.add(middle)
            }
            if (index < breaks.size) refined.add(next)

            previous = next
            previousDepth = nextDepth
        }

        if (refined.size == breaks.size) return@repeat
        breaks.clear()
        breaks.addAll(refined.sorted())
    }

    // Walk the edge, and at each break take the wall only as deep as the polygon allows there. The
    // ends keep the corner points they already had, so the seams stay shared with the neighbours.
    var previousOuter = from
    var previousInner = innerStart
    for (along in breaks) {
        val outer = Vector2.create(from.x + edgeX * along, from.y + edgeY * along)
        val depth = depthAt(along)
        val inner = Vector2.create(outer.x + normalX * depth, outer.y + normalY * depth)

        into.add(WallQuad(previousOuter, outer, inner, previousInner))
        previousOuter = outer
        previousInner = inner
    }
    into.add(WallQuad(previousOuter, to, innerEnd, previousInner))
}

/**
 * How far a ray leaving a point *on* edge [edgeIndex] gets before reaching the outline again.
 *
 * The edge it starts from is skipped, since the ray begins sitting on it. Unlike the version that
 * starts at a vertex, the edges either side are not skipped: from the middle of an edge they are
 * ordinary obstacles like any other.
 */
private fun distanceFromPoint(
    ring: List<Vector2>,
    edgeIndex: Int,
    origin: Vector2,
    directionX: Double,
    directionY: Double
): Double {
    val count = ring.size
    var nearest = Double.POSITIVE_INFINITY

    for (i in 0 until count) {
        if (i == edgeIndex) continue

        val from = ring[i]
        val to = ring[(i + 1) % count]
        val edgeX = to.x - from.x
        val edgeY = to.y - from.y

        val denominator = directionX * edgeY - directionY * edgeX
        if (abs(denominator) < EPSILON) continue

        val offsetX = origin.x - from.x
        val offsetY = origin.y - from.y
        val alongRay = -(offsetX * edgeY - offsetY * edgeX) / denominator
        val alongEdge = -(offsetX * directionY - offsetY * directionX) / denominator

        if (alongRay > EPSILON && alongEdge >= 0.0 && alongEdge <= 1.0 && alongRay < nearest) {
            nearest = alongRay
        }
    }
    return nearest
}

/** A point [thickness] from vertex [vertexIndex] along a direction, cut to stay inside the outline. */
private fun offsetAlong(
    ring: List<Vector2>,
    vertexIndex: Int,
    directionX: Double,
    directionY: Double,
    thickness: Double
): Vector2 {
    val distance = minOf(thickness, distanceToOutline(ring, vertexIndex, directionX, directionY))
    val from = ring[vertexIndex]
    return Vector2.create(from.x + directionX * distance, from.y + directionY * distance)
}

/**
 * How far a ray leaving vertex [vertexIndex] along the given unit direction travels before it
 * reaches the outline again, or [Double.POSITIVE_INFINITY] if it never does. The two edges meeting
 * at that vertex are skipped, since the ray starts out sitting on both of them.
 *
 * This is what keeps the wall inside the polygon: a corner is never pushed further in than there is
 * polygon to push into.
 */
private fun distanceToOutline(
    ring: List<Vector2>,
    vertexIndex: Int,
    directionX: Double,
    directionY: Double
): Double {
    val count = ring.size
    val origin = ring[vertexIndex]
    var nearest = Double.POSITIVE_INFINITY

    for (i in 0 until count) {
        if (i == vertexIndex || i == (vertexIndex + count - 1) % count) continue

        val from = ring[i]
        val to = ring[(i + 1) % count]
        val edgeX = to.x - from.x
        val edgeY = to.y - from.y

        val denominator = directionX * edgeY - directionY * edgeX
        if (kotlin.math.abs(denominator) < EPSILON) continue // running parallel to this edge

        val offsetX = origin.x - from.x
        val offsetY = origin.y - from.y
        val alongRay = -(offsetX * edgeY - offsetY * edgeX) / denominator
        val alongEdge = -(offsetX * directionY - offsetY * directionX) / denominator

        if (alongRay > EPSILON && alongEdge >= 0.0 && alongEdge <= 1.0 && alongRay < nearest) {
            nearest = alongRay
        }
    }

    return nearest
}

/**
 * Every point where [outline] crosses itself. Empty for a well formed outline.
 *
 * A simple outline — one that never crosses itself — is a hard precondition of [buildWalls], not a
 * nicety. Everything it does rests on knowing which side is inside, and it works that out from the
 * outline's winding. Cross two edges and there is no consistent answer: the winding says one thing
 * while part of the boundary runs the other way, so the inward normals along that part point
 * outward and the wall is built into open air. Measured on a hexagon with a single crossed edge,
 * 45% of the collision area ended up outside the shape.
 *
 * The failure is silent, and it looks like a bug in the wall builder rather than in its input,
 * which is exactly why it is worth checking for and saying so out loud.
 */
fun selfIntersections(outline: List<Vector2>): List<Vector2> {
    val crossings = ArrayList<Vector2>()
    val count = outline.size

    for (i in 0 until count) {
        for (j in i + 1 until count) {
            // Edges that share a vertex always touch there; that is not a crossing.
            if (j == i + 1 || (i == 0 && j == count - 1)) continue

            val crossing = seamCrossing(
                outline[i], outline[(i + 1) % count],
                outline[j], outline[(j + 1) % count]
            )
            if (crossing != null) crossings.add(crossing)
        }
    }
    return crossings
}

/** Where two seams cross, or `null` if they don't properly cross each other. */
internal fun seamCrossing(fromA: Vector2, toA: Vector2, fromB: Vector2, toB: Vector2): Vector2? {
    val aX = toA.x - fromA.x
    val aY = toA.y - fromA.y
    val bX = toB.x - fromB.x
    val bY = toB.y - fromB.y

    val denominator = aX * bY - aY * bX
    if (kotlin.math.abs(denominator) < EPSILON) return null // parallel seams never cross

    val offsetX = fromB.x - fromA.x
    val offsetY = fromB.y - fromA.y
    val alongA = (offsetX * bY - offsetY * bX) / denominator
    val alongB = (offsetX * aY - offsetY * aX) / denominator

    // Strictly interior to both seams: touching at an endpoint is not a bowtie.
    if (alongA <= EPSILON || alongA >= 1.0 - EPSILON) return null
    if (alongB <= EPSILON || alongB >= 1.0 - EPSILON) return null

    return Vector2.create(fromA.x + alongA * aX, fromA.y + alongA * aY)
}
