package util.extensions

import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.Fixture

fun Array<out Body>.setMaskBits(i: Int) {
    this.forEach { it.setMaskBits(i) }
}

fun Body.setMaskBits(i: Int) {
    this.m_fixtureList.forEach { it.m_filter.maskBits = i; it.refilter() }
}

fun Fixture.forEach(operation: (f: Fixture) -> Unit) {
    var f: Fixture? = this
    while (f != null) {
        operation(f)
        f = f.m_next
    }
}