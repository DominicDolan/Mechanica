package com.mechanica.engine.compose

import com.cave.library.vector.vec2.MutableVector2
import com.dubulduke.layout.FontRef
import com.dubulduke.layout.TextMetrics
import com.dubulduke.layout.TextSize
import com.mechanica.engine.shaders.text.Font
import com.mechanica.engine.text.CharacterCursor

/**
 * A font at a size, as the layout graph sees it.
 *
 * Mechanica's [Font] has no size of its own: `FontMetrics` scales everything by
 * `stbtt_ScaleForPixelHeight(info, 1f)`, so every metric it reports is *per unit of size* and the
 * drawer's `size` argument is a plain scale factor. That is convenient here — measurement scales
 * linearly, so two sizes of one typeface share all the expensive work and differ by a multiply.
 *
 * **Hold the instance.** The font slot compares with `RefEquality.IDENTITY`, so a fresh
 * `MechanicaFont` per composition would report "changed" every frame and re-trigger measurement,
 * the most expensive input the layout graph has. Create these once, alongside the [Font].
 */
class MechanicaFont(val font: Font, val size: Double) : FontRef {
    override fun toString(): String = "MechanicaFont(size=$size)"
}

/**
 * Text measurement backed by Mechanica's fonts. `dsl-and-engine.md` §6.1.
 *
 * ### Why this does not go through `Text`
 *
 * `TextDrawerImpl.bottomRight` already measures, and its inputs — `Text.getEndOfLinePosition` and
 * `Text.lineCount` — are public, so a `TextMetrics` could have been a thin wrapper over a scratch
 * [com.mechanica.engine.shaders.text.Text]. Three reasons it is not:
 *
 *  - `Text.update` fills vertex buffers, growing `positions` and `texCoords` to `length * 12`
 *    `MutableVector2` objects that a measurement immediately discards. Fine for a label, wrong for
 *    anything that measures on every keystroke.
 *  - `Text` overwrites `carriageReturns` only up to the current line count and never clears the
 *    tail, so a two-line string measured on a scratch `Text` after a five-line one reads stale
 *    line data.
 *  - It cannot wrap. **Nothing in Mechanica wraps text** — `Text` breaks on explicit `\n` only —
 *    and [TextMetrics.measure] is inherently a wrapping API, because the whole point of
 *    `layout-design.md` §8.3 is that text height depends on the width it is given. So the wrap is
 *    ours to write regardless, and writing it needs per-character advances rather than per-line
 *    totals.
 *
 * So this drives [Font.addCharacterDataToArrays] directly with its own cursor, reading the advance
 * back off the cursor and throwing the glyph quad away. `copyToArrays` indexes at
 * `max(nonWhiteSpaceIndex - 1, 0) * 4`, so pinning that index each character keeps every write
 * inside a four-element scratch buffer: **allocation per measure is one `DoubleArray` growth at
 * most, and none in steady state.**
 *
 * The clean version of this belongs upstream — a `Font.advance(prev: Char, c: Char): Float` over
 * `stbtt_GetCodepointHMetrics` + `stbtt_GetCodepointKernAdvance` needs neither the atlas nor the
 * quad. This class is what can be written without changing Mechanica.
 *
 * ### Agreement with the drawer
 *
 * The height reported here is the em box for the first line plus one line advance for each after
 * it, which is exactly what `TextDrawerImpl.bottomRight` computes. That is not a coincidence to be
 * maintained by hand — it is the whole reason `bottomRight` was changed. It previously boxed text
 * at `lineCount`, one unit per line, while `Text.addNewLine` advanced glyphs by `font.lineHeight`,
 * so the box under-measured multi-line text by the line gap per line and anything anchored with
 * `origin.normalized` drifted as the line count grew. Single-line text was, and is, unaffected.
 *
 * If the two ever diverge again the symptom is multi-line text sitting wrong inside a correctly
 * sized box, which reads as a layout bug and is not one.
 *
 * ### Threading and lifetime
 *
 * Single-threaded, like the graph it feeds: the scratch buffers are shared across calls. And
 * constructing a [Font] ends in `LwjglImage.create(...)`, so **fonts require an OpenGL context** —
 * the layout graph cannot be built before `Game.create()`. Headless tests keep
 * [com.dubulduke.layout.HeadlessTextMetrics]; that the seam exists is what makes this a
 * scheduling constraint rather than a problem.
 */
class MechanicaTextMetrics(
    /**
     * The advance *between* consecutive lines, per unit of font size. `null` takes the font's own
     * `ascent - descent + lineGap`. It does not set the height of a single line, which is always
     * the em box — see `heightOf`.
     */
    private val lineHeightFactor: Double? = null,
) : TextMetrics {

    private val cursor = CharacterCursor()

    // Four is the whole quad: copyToArrays writes i..i+3 with i pinned to 0 below.
    private val scratchPositions = Array(4) { MutableVector2.create() }
    private val scratchTexCoords = Array(4) { MutableVector2.create() }

    /** Per-character advances of the paragraph being measured, already scaled to layout units. */
    private var advances = DoubleArray(256)

    override fun measure(text: String, font: FontRef, maxWidth: Double): TextSize {
        val f = font as? MechanicaFont ?: throw IllegalArgumentException(
            "MechanicaTextMetrics needs a MechanicaFont, got ${font::class.simpleName}"
        )
        var widest = 0.0
        var lines = 0
        for (paragraph in text.split('\n')) {
            measureAdvances(paragraph, f)
            lines += wrap(paragraph, maxWidth) { _, _, lineWidth ->
                if (lineWidth > widest) widest = lineWidth
            }
        }
        return TextSize(widest, heightOf(lines, f), lines)
    }

    /**
     * The box [lines] lines of text occupy: the em box for the first, plus one line advance for
     * each after it.
     *
     * Not `lines * lineHeight`. The first line is not preceded by a gap, and `Text.addNewLine`
     * only advances by `lineHeight` *between* lines, so counting a full line height for the first
     * one would reserve leading that no glyph ever occupies. This matches
     * `TextDrawerImpl.bottomRight` exactly, which is the point — layout deciding a box and the
     * drawer anchoring text inside it have to be the same arithmetic or multi-line text drifts.
     */
    private fun heightOf(lines: Int, f: MechanicaFont): Double {
        val em = f.font.ascent.toDouble() - f.font.descent.toDouble()
        val advance = lineHeightFactor ?: f.font.lineHeight.toDouble()
        return (em + (lines - 1) * advance) * f.size
    }

    /**
     * [text] with a newline inserted at every break [measure] would have counted.
     *
     * This exists because **the wrap has to be applied twice and must agree with itself.** Layout
     * asks [measure] how tall the text is, and reserves that height; the backend then has to draw
     * text that actually occupies it. Mechanica's `Text` breaks on explicit `\n` and nothing else,
     * so unless the breaks are materialised into the string, layout reserves three lines of height
     * and the drawer paints one long line running off the side of its box.
     *
     * Routing both through the same [wrap] is what keeps them consistent. Deriving the drawn
     * breaks separately — even from the same rules — would be two implementations to keep in step,
     * and the failure mode is a silent half-line of disagreement rather than an error.
     *
     * Returns [text] itself when nothing wraps, so the common case allocates nothing.
     */
    fun wrapped(text: String, font: MechanicaFont, maxWidth: Double): String {
        val sb = StringBuilder(text.length + 8)
        var brokeAnywhere = false
        for ((p, paragraph) in text.split('\n').withIndex()) {
            if (p > 0) sb.append('\n')
            measureAdvances(paragraph, font)
            var lineIndex = 0
            wrap(paragraph, maxWidth) { start, end, _ ->
                if (lineIndex > 0) {
                    sb.append('\n')
                    brokeAnywhere = true
                }
                sb.append(paragraph, start, end)
                lineIndex++
            }
        }
        return if (brokeAnywhere) sb.toString() else text
    }

    /**
     * Fill [advances] with the layout-unit advance of each character of [paragraph], kerned
     * against its predecessor.
     *
     * This is the same accumulation `Text.update` performs — one cursor carried across the string,
     * so kerning sees the real previous character — with the vertex output discarded. Whitespace
     * advances too: `addCharacterDataToArrays` updates the cursor before deciding not to emit a
     * quad for a space.
     */
    private fun measureAdvances(paragraph: String, f: MechanicaFont) {
        if (paragraph.length > advances.size) advances = DoubleArray(paragraph.length * 2)
        cursor.reset()
        for (i in paragraph.indices) {
            val before = cursor.xAdvance
            cursor.advance(paragraph[i])
            // Pin the glyph quad to slot 0 of the scratch buffers. Left alone this index grows
            // with the string and would run off the end of a four-element array.
            cursor.nonWhiteSpaceIndex = 1
            f.font.addCharacterDataToArrays(cursor, scratchPositions, scratchTexCoords)
            advances[i] = (cursor.xAdvance - before).toDouble() * f.size
        }
    }

    /**
     * Greedy word wrap over [advances], reporting each line's width to [line]. Returns the number
     * of lines, always at least one.
     *
     * Matches the behaviour [com.dubulduke.layout.HeadlessTextMetrics] documents, because golden
     * files encode it and the two implementations disagreeing on *where* lines break would make
     * headless tests meaningless:
     *
     *  - a word is never broken, so a word wider than [maxWidth] overflows on its own line;
     *  - empty text is one empty line, not zero — a text box with no text still occupies a line
     *    box, as every real text engine has it;
     *  - the space a line breaks at is not counted in that line's width.
     */
    private inline fun wrap(
        paragraph: String,
        maxWidth: Double,
        line: (start: Int, endExclusive: Int, width: Double) -> Unit,
    ): Int {
        var lineStart = 0
        var lineWidth = 0.0
        var lastSpace = -1
        var widthBeforeLastSpace = 0.0
        var lines = 0

        var i = 0
        while (i < paragraph.length) {
            if (paragraph[i] == ' ') {
                lastSpace = i
                widthBeforeLastSpace = lineWidth
            }
            val extended = lineWidth + advances[i]
            // `lastSpace > lineStart` rather than `>=`: a line beginning with a space has no
            // break opportunity on it, and treating one as available would emit an empty line.
            if (extended > maxWidth && lastSpace > lineStart) {
                // The break space ends the line and is not part of it, in width or in characters.
                line(lineStart, lastSpace, widthBeforeLastSpace)
                lines++
                lineStart = lastSpace + 1
                i = lineStart
                lineWidth = 0.0
                lastSpace = -1
                widthBeforeLastSpace = 0.0
                continue
            }
            lineWidth = extended
            i++
        }
        line(lineStart, paragraph.length, lineWidth)
        return lines + 1
    }
}
