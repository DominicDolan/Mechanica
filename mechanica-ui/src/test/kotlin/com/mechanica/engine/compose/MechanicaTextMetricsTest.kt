package com.mechanica.engine.compose

import com.cave.library.vector.vec2.MutableVector2
import com.dubulduke.layout.HeadlessTextMetrics
import com.dubulduke.layout.SimpleFont
import com.mechanica.engine.shaders.models.Image
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.text.CharacterCursor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * The wrap, tested without a GPU.
 *
 * A real [Font] cannot be built here — `FontAtlas`'s constructor ends in `LwjglImage.create(...)`,
 * so fonts need an OpenGL context. But wrapping is font-*independent*: it consumes per-character
 * advances and knows nothing about where they came from. Substituting a [Font] whose advances are
 * fixed therefore tests all of the logic that can actually be wrong, and the untested remainder is
 * one call into `stbtt`.
 *
 * The comparisons against [HeadlessTextMetrics] are the ones that matter most. Golden files encode
 * its wrap behaviour, so if the two implementations disagree about *where* lines break, headless
 * tests stop predicting what Mechanica draws.
 */
class MechanicaTextMetricsTest {

    /** Every character one unit wide at size 1, so widths are character counts times the size. */
    private class FixedFont(private val advance: Float = 1.0f) : Font() {
        override val atlas: Image = object : Image {
            override val id = 0
            override fun bind() {}
        }
        override val ascent = 0.8f
        override val descent = -0.2f
        override val lineGap = 0.4f

        var quadWrites = 0
            private set
        var maxIndexTouched = -1
            private set

        override fun addCharacterDataToArrays(
            cursor: CharacterCursor,
            positions: Array<MutableVector2>,
            texCoords: Array<MutableVector2>,
        ) {
            cursor.xAdvance += advance
            if (cursor.currentChar != ' ') {
                // Mirror copyToArrays' indexing so the scratch-buffer pinning is under test.
                val i = maxOf(cursor.nonWhiteSpaceIndex - 1, 0) * 4
                for (k in i until i + 4) {
                    positions[k].x = 0.0
                    texCoords[k].x = 0.0
                    if (k > maxIndexTouched) maxIndexTouched = k
                }
                quadWrites++
            }
        }
    }

    /**
     * Mechanica's `Font` reports ascent / descent / lineGap as `Float`, so a line height built
     * from them carries single-precision error into every height this returns — `1.4f` is
     * `1.39999997`. Widths do not: advances are summed, not derived from a sum of three floats.
     */
    private val EPS = 1e-5

    private val metrics = MechanicaTextMetrics()
    private val font = FixedFont()

    /**
     * size 10, so one character is 10 wide, the em box is `(0.8 + 0.2) * 10 = 10` and each line
     * after the first adds `(0.8 + 0.2 + 0.4) * 10 = 14`.
     */
    private val f10 = MechanicaFont(font, 10.0)

    private fun measure(text: String, maxWidth: Double = Double.POSITIVE_INFINITY) =
        metrics.measure(text, f10, maxWidth)

    // -------------------------------------------------------------------------------
    // Sizes
    // -------------------------------------------------------------------------------

    @Test
    fun `an unwrapped line is the sum of its advances`() {
        val s = measure("hello")
        assertEquals(50.0, s.width)
        assertEquals(1, s.lines)
        assertEquals(10.0, s.height, EPS, "one line is the em box, with no leading above it")
    }

    @Test
    fun `size scales measurement linearly, because Mechanica's metrics are per unit of size`() {
        val small = metrics.measure("hello", MechanicaFont(font, 10.0), Double.POSITIVE_INFINITY)
        val large = metrics.measure("hello", MechanicaFont(font, 30.0), Double.POSITIVE_INFINITY)
        assertEquals(small.width * 3.0, large.width)
        assertEquals(small.height * 3.0, large.height, EPS)
    }

    @Test
    fun `only the lines after the first cost a line height`() {
        // Not lines * lineHeight: nothing precedes the first line, so charging it a full line
        // advance would reserve leading no glyph occupies. Same arithmetic as
        // TextDrawerImpl.bottomRight, which is what keeps the box and the glyphs together.
        assertEquals(1.4, font.lineHeight.toDouble(), EPS)
        assertEquals(10.0, measure("one").height, EPS)
        assertEquals(24.0, measure("one\ntwo").height, EPS)
        assertEquals(38.0, measure("one\ntwo\nthree").height, EPS)
    }

    @Test
    fun `the line height factor can be overridden to match the drawer`() {
        val drawerLike = MechanicaTextMetrics(lineHeightFactor = 1.0)
        assertEquals(20.0, drawerLike.measure("a\nb", f10, Double.POSITIVE_INFINITY).height, EPS)
    }

    @Test
    fun `empty text is one empty line, not zero`() {
        val s = measure("")
        assertEquals(0.0, s.width)
        assertEquals(1, s.lines)
        assertEquals(10.0, s.height, EPS, "a text box with no text still occupies a line box")
    }

    @Test
    fun `spaces advance`() {
        assertEquals(30.0, measure("a b").width)
    }

    @Test
    fun `a non-Mechanica font is rejected by name`() {
        val e = assertFailsWith<IllegalArgumentException> {
            metrics.measure("x", SimpleFont("Inter", 10.0), Double.POSITIVE_INFINITY)
        }
        assertTrue(e.message!!.contains("SimpleFont"), e.message!!)
    }

    // -------------------------------------------------------------------------------
    // Wrapping
    // -------------------------------------------------------------------------------

    @Test
    fun `explicit newlines always break`() {
        val s = measure("ab\ncdef\ng")
        assertEquals(3, s.lines)
        assertEquals(40.0, s.width, "the widest line wins")
        assertEquals(38.0, s.height, EPS)
    }

    @Test
    fun `a paragraph wraps at spaces and the break space is not counted`() {
        // "aaa bbb ccc" at 70 wide: "aaa bbb" is 70, adding " ccc" exceeds it.
        val s = measure("aaa bbb ccc", maxWidth = 70.0)
        assertEquals(2, s.lines)
        assertEquals(70.0, s.width)
    }

    @Test
    fun `a word wider than the limit overflows on its own line rather than being broken`() {
        val s = measure("aa bbbbbbbb cc", maxWidth = 40.0)
        assertEquals(3, s.lines)
        assertEquals(80.0, s.width, "the overflowing word sets the measured width")
    }

    @Test
    fun `an infinite limit never breaks`() {
        val s = measure("aaa bbb ccc ddd eee")
        assertEquals(1, s.lines)
        assertEquals(190.0, s.width)
    }

    @Test
    fun `a limit narrower than one character still terminates`() {
        val s = measure("abc def", maxWidth = 1.0)
        assertEquals(2, s.lines, "there is one break opportunity and it is taken")
    }

    @Test
    fun `a leading space is not treated as a break opportunity`() {
        // Breaking there would emit an empty line rather than making progress.
        val s = measure(" aaaa", maxWidth = 20.0)
        assertEquals(1, s.lines)
        assertEquals(50.0, s.width)
    }

    // -------------------------------------------------------------------------------
    // Agreement with the headless stub, which golden files encode
    // -------------------------------------------------------------------------------

    @Test
    fun `line breaks land in the same places as HeadlessTextMetrics`() {
        // Both at "one character is 10 wide", so any disagreement is the wrap algorithm rather
        // than the metrics.
        val headless = HeadlessTextMetrics(charWidthRatio = 1.0, lineHeightRatio = 1.4)
        val stub = SimpleFont("Test", 10.0)
        val cases = listOf(
            "" to 100.0,
            "hello" to 100.0,
            "aaa bbb ccc" to 70.0,
            "aa bbbbbbbb cc" to 40.0,
            "the quick brown fox jumps over the lazy dog" to 100.0,
            "one\ntwo three four" to 60.0,
            "a b c d e f g h" to 30.0,
        )
        for ((text, width) in cases) {
            val mine = metrics.measure(text, f10, width)
            val theirs = headless.measure(text, stub, width)
            assertEquals(theirs.lines, mine.lines, "line count for ${text.replace("\n", "\\n")}")
            assertEquals(theirs.width, mine.width, 1e-9, "width for ${text.replace("\n", "\\n")}")
            // Heights are deliberately not compared. The two use different line-box models: the
            // stub charges every line a uniform lineHeight, while this one charges the first line
            // the em box and only the rest a line advance, so that it agrees with where Mechanica
            // actually puts the glyphs. Break positions are what golden files encode, and those
            // are what have to agree.
        }
    }

    // -------------------------------------------------------------------------------
    // `wrapped` — the same breaks, materialised for the drawer
    // -------------------------------------------------------------------------------

    @Test
    fun `wrapped inserts a newline at every break measure counted`() {
        assertEquals("aaa bbb\nccc", metrics.wrapped("aaa bbb ccc", f10, 70.0))
    }

    @Test
    fun `wrapped preserves explicit newlines and wraps within each paragraph`() {
        assertEquals("aaa bbb\nccc\nddd", metrics.wrapped("aaa bbb ccc\nddd", f10, 70.0))
    }

    @Test
    fun `wrapped returns the original string when nothing breaks`() {
        val text = "aaa bbb ccc"
        assertSame(text, metrics.wrapped(text, f10, 1000.0), "an unwrapped string was copied")
    }

    @Test
    fun `wrapped does not keep the space it broke at`() {
        // Keeping it would leave a trailing space that Mechanica's Text still advances over,
        // pushing a right-aligned line off by a space width.
        assertEquals("aa\nbb", metrics.wrapped("aa bb", f10, 20.0))
    }

    @Test
    fun `an overflowing word survives wrapped intact`() {
        assertEquals("aa\nbbbbbbbb\ncc", metrics.wrapped("aa bbbbbbbb cc", f10, 40.0))
    }

    @Test
    fun `what wrapped produces has the line count measure reserved height for`() {
        // The invariant the first Mechanica run violated: layout reserved three lines of height
        // and the drawer painted one long line, because the breaks existed only in the measure.
        // Mechanica's `Text` counts lines by splitting on '\n', so that is the comparison.
        val cases = listOf(
            "aaa bbb ccc" to 70.0,
            "aa bbbbbbbb cc" to 40.0,
            "the quick brown fox jumps over the lazy dog" to 100.0,
            "one\ntwo three four" to 60.0,
            "single" to 1000.0,
            "" to 100.0,
        )
        for ((text, width) in cases) {
            val measured = metrics.measure(text, f10, width)
            val drawn = metrics.wrapped(text, f10, width).split('\n')
            assertEquals(measured.lines, drawn.size, "line count for ${text.replace("\n", "\\n")}")
            // And every line that comes out actually fits the width it was wrapped to, except a
            // single word that could not be broken.
            for (line in drawn) {
                val lineWidth = metrics.measure(line, f10, Double.POSITIVE_INFINITY).width
                if (' ' in line) {
                    assertTrue(
                        lineWidth <= width + EPS,
                        "line '$line' is $lineWidth wide, over the $width limit",
                    )
                }
            }
        }
    }

    // -------------------------------------------------------------------------------
    // The scratch buffers
    // -------------------------------------------------------------------------------

    @Test
    fun `the glyph quad is pinned to slot zero, whatever the string length`() {
        // This is what lets the scratch buffers be four elements instead of length * 12. Without
        // the pin, nonWhiteSpaceIndex grows with the string and runs off the end.
        measure("a considerably longer string than four characters")
        assertEquals(3, font.maxIndexTouched, "a write landed outside the four-element scratch quad")
        assertTrue(font.quadWrites > 40, "the fake font was not actually driven")
    }

    @Test
    fun `measuring repeatedly does not grow anything`() {
        val long = "x".repeat(500)
        repeat(5) { assertEquals(5000.0, measure(long).width) }
        assertEquals(3, font.maxIndexTouched)
    }
}
