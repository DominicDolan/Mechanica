package util

import java.util.*

class FrameQueue(private val capacity: Int, private val initialValue: Double = 17.0/1000.0) :  Queue<Double> {
    override val size: Int
        get() = frames.size
    private val frames: Queue<Double> = ArrayDeque<Double>(capacity)
    var average = initialValue
        private set

    init {
        initializeQueue()
    }

    private fun initializeQueue() {
        for (i in 1..capacity) {
            frames.add(initialValue)
        }
    }

    private fun addAndCalculateAverage(value: Double) {
        val poll = frames.poll()
        frames.add(value)
        average += (value - poll) / capacity.toDouble()
    }

    private fun removeAndCalculateAverage(): Double {
        val poll = frames.poll()
        frames.add(initialValue)
        average += (initialValue - poll) / capacity.toDouble()
        return poll
    }

    override fun contains(element: Double): Boolean {
        return frames.contains(element)
    }

    override fun isEmpty(): Boolean {
        return frames.isEmpty()
    }

    override fun addAll(elements: Collection<Double>): Boolean {
        for (element in elements) {
            addAndCalculateAverage(element)
        }
        return true
    }

    override fun clear() {
        frames.clear()
        initializeQueue()
    }

    override fun element(): Double {
        return frames.element()
    }

    override fun remove(): Double {
        return removeAndCalculateAverage()
    }

    override fun containsAll(elements: Collection<Double>): Boolean {
        return frames.containsAll(elements)
    }

    override fun iterator(): MutableIterator<Double> {
        return frames.iterator()
    }

    override fun remove(element: Double): Boolean {
        removeAndCalculateAverage()
        return true
    }

    override fun removeAll(elements: Collection<Double>): Boolean {
        for (element in elements) {
            removeAndCalculateAverage()
        }
        return true
    }

    override fun add(element: Double): Boolean {
        addAndCalculateAverage(element)
        return true
    }

    override fun offer(e: Double): Boolean {
        addAndCalculateAverage(e)
        return true
    }

    override fun retainAll(elements: Collection<Double>): Boolean {
        return frames.retainAll(elements)
    }

    override fun peek(): Double {
        return frames.peek()
    }

    override fun poll(): Double {
        return removeAndCalculateAverage()
    }

}