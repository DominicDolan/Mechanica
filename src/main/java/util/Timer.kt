package util

object Timer {
    private val ids = HashMap<Int, Double>()
    val now get() = (System.nanoTime()/1000000L)/1000.0
    val begin = now
    val elapsed get() = now - begin

    fun start(id: Int) {
        ids[id] = now
    }

    fun end(id: Int): Double {
        val start = ids[id]
        return if (start != null) {
            now - start
        } else -1.0
    }
}