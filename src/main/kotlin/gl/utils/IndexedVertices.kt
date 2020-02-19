package gl.utils

data class IndexedVertices(val vertices: FloatArray, val indices: ShortArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IndexedVertices

        if (!vertices.contentEquals(other.vertices)) return false
        if (!indices.contentEquals(other.indices)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vertices.contentHashCode()
        result = 31 * result + indices.contentHashCode()
        return result
    }
}