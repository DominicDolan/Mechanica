package triangulator

import com.cave.library.vector.vec2.Vector2
import com.cave.library.vector.vec2.vec
import com.mechanica.engine.geometry.shapes.Triangle
import com.mechanica.engine.geometry.triangulation.delaunay.DelaunayTriangle
import com.mechanica.engine.geometry.triangulation.delaunay.SuperPoint
import com.mechanica.engine.geometry.triangulation.delaunay.isOnRightOf
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TriangleTests {
    lateinit var triangle: Triangle
    @Before
    fun instanitiateTriangle() {
        triangle = Triangle.create(vec(0.0, 0.0), vec(1.0, 2.0), vec(2.0, 0.0))
    }
    
    @Test
    fun triangleContainsPoint() {
        assertTrue("Failed for (1.0, 1.0)") { triangle.contains(vec(1.0, 1.0)) == 1 }
        assertTrue("Failed for (1.0, 0.1)") { triangle.contains(vec(1.0, 0.1)) == 1 }
        assertTrue("Failed for (1.0, 1.9)") { triangle.contains(vec(1.0, 1.9)) == 1 }
        
        assertTrue("Failed for (0.2, 0.1)") { triangle.contains(vec(0.2, 0.1)) == 1 }
        assertTrue("Failed for (1.0, 0.1)") { triangle.contains(vec(1.0, 0.1)) == 1 }
        assertTrue("Failed for (1.8, 0.1)") { triangle.contains(vec(1.8, 0.1)) == 1 }
        
        assertFalse("Failed for (1.0, 2.1)") { triangle.contains(vec(1.0, 2.1)) == 1 }
        assertFalse("Failed for (1.0, -0.1)") { triangle.contains(vec(1.0, -0.1)) == 1 }
        
        assertFalse("Failed for (-0.2, 0.1)") { triangle.contains(vec(-0.2, 0.1)) == 1 }
        assertFalse("Failed for (2.1, 0.1)") { triangle.contains(vec(2.1, 0.1)) == 1 }
    }

    @Test
    fun triangleContainsColinear() {
        assertFalse { triangle.contains(vec(1.0, 0.0)) == 1 }
        assertFalse { triangle.contains(vec(0.5, 1.0)) == 1 }
        assertFalse { triangle.contains(vec(1.5, 1.0)) == 1 }
    }

    @Test
    fun superTriangleContainsPoint() {
        val triangle = DelaunayTriangle(
            SuperPoint.TopPoint,
            Vector2.create(0.0, 0.0),
            Vector2.create(2.0, -1.0)
        )

        assertTrue { triangle.contains(Vector2.create(1.0, 0.5)) == 1 }
    }

    @Test
    fun testIsOnRight() {
        val p1 = Vector2.create(0.0, 0.0)
        val p2 = Vector2.create(3.0, 0.0)
        val p3 = Vector2.create(4.0, 2.0)
        val p4 = Vector2.create(4.0, -2.0)
        val p5 = Vector2.create(4.0, -2.0)
        val p = Vector2.create(1.5, 1.0)

        assertFalse { p.isOnRightOf(p1, p2) == 1 }
        assertTrue { p.isOnRightOf(p2, p1) == 1 }

        assertFalse { p.isOnRightOf(SuperPoint.TopPoint, p1) == 1 }
        assertTrue { p.isOnRightOf(SuperPoint.BottomPoint, p1) == 1 }

        assertTrue { p.isOnRightOf(SuperPoint.TopPoint, p3) == 1 }
        assertFalse { p.isOnRightOf(SuperPoint.TopPoint, SuperPoint.BottomPoint) == 1 }
        assertTrue { p.isOnRightOf(SuperPoint.BottomPoint, SuperPoint.TopPoint) == 1 }
        assertFalse { p4.isOnRightOf(SuperPoint.TopPoint, SuperPoint.BottomPoint) == 1 }
        assertFalse { p5.isOnRightOf(SuperPoint.TopPoint, SuperPoint.BottomPoint) == 1 }
    }

    @Test
    fun superPointTests() {
        val p00 = Vector2.create(0.0, 0.0)
        val minus1 = Vector2.create(-1.0, 0.0)
        val pm1 = SuperPoint.TopPoint
        val pm2 = SuperPoint.BottomPoint

        assertTrue { p00.isOnRightOf(pm2, minus1) == 1 }
        assertTrue { p00.isOnRightOf(minus1, pm1) == 1 }
        assertTrue { p00.isOnRightOf(pm2, pm1) == 1 }

        assertFalse { p00.isOnRightOf(pm1, pm2) == 1 }
        assertFalse { p00.isOnRightOf(pm1, minus1) == 1 }
        assertFalse { p00.isOnRightOf(minus1, pm2) == 1 }
    }
    
}