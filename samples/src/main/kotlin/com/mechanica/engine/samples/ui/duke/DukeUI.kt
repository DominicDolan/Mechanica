package com.mechanica.engine.samples.ui.duke

import com.mechanica.engine.samples.ui.duke.component.ComponentBuilder
import com.mechanica.engine.samples.ui.duke.component.ComponentBuilderImpl
import com.mechanica.engine.samples.ui.duke.component.ContentBuilder
import com.mechanica.engine.samples.ui.duke.component.LayoutBuilderImpl
import com.mechanica.engine.samples.ui.duke.component.StyleBuilderImpl
import com.mechanica.engine.samples.ui.duke.context.DukeContext
import com.mechanica.engine.samples.ui.duke.context.RenderDescription
import com.mechanica.engine.samples.ui.duke.layout.Layout
import com.mechanica.engine.samples.ui.duke.layout.MutableLayout
import com.mechanica.engine.samples.ui.duke.layout.NodeLayouts
import com.mechanica.engine.samples.ui.duke.layout.StaticLayout
import com.mechanica.engine.samples.ui.duke.theme.Style
import kotlin.math.abs
import kotlin.reflect.KClass


class DukeUI(private val context: DukeContext) {

    private val baseNode = Node(null, null)
    private val grandParentLayout = object : StaticLayout(context) {
        override val x: Double
            get() = context.viewport.x
        override val y: Double
            get() = context.viewport.y
        override val width: Double
            get() = context.viewport.width
        override val height: Double
            get() = abs(context.viewport.height)
    }


    private var widthScale: Double = 1.0
    private var heightScale: Double = 1.0
    private var xAdjust: Double = 0.0
    private var yAdjust: Double = 0.0

    init {
        recalculateWindowViewportValues()
    }

    private fun recalculateWindowViewportValues() {
        val window = context.window
        val viewport = context.viewport
        widthScale = window.width / viewport.width
        heightScale = window.height / viewport.height
        val signAdjustX = if (widthScale < 0.0) window.width else 0.0
        val signAdjustY = if (heightScale < 0.0) window.height else 0.0
        xAdjust = window.x - widthScale * viewport.x + signAdjustX
        yAdjust = window.y - heightScale * viewport.y + signAdjustY
    }


    val dukeBuilder = DukeBuilder()
    val componentBuilder = ComponentBuilderImpl(dukeBuilder)
    val builderMap = mutableMapOf<KClass<*>, Any>()

    var cursor = baseNode

    inline fun build(builder: ContentBuilder.() -> Unit) {
        dukeBuilder.appendContent(builder)
    }

    inner class DukeBuilder {
        val layout = LayoutBuilderImpl(this)
        val style = StyleBuilderImpl(this)

        inline fun <reified T> useBuilder(constructor: (dukeBuilder: DukeBuilder) -> T): T {
            val builderClass = T::class

            if (builderMap.containsKey(builderClass)) {
                return builderMap[builderClass] as T
            } else {
                val builder = constructor(this)
                builderMap[builderClass] = builder as Any
                return builder
            }
        }

        fun appendComponent(): ComponentBuilder {
            nextChild()
            componentBuilder.prepare(cursor.layouts, cursor.style)
            return componentBuilder
        }

        inline fun content(build: ContentBuilder.() -> Unit) {
            appendContent(build)
        }

        inline fun appendContent(build: ContentBuilder.() -> Unit) {
            val contentBuilder = useBuilder { ContentBuilder(it) }
            val oldCursor = cursor
            cursor = cursor.nextChild()
            build(contentBuilder)
            cursor = oldCursor
        }
    }

    fun nextChild() {
        val parent = cursor.parent
        if (parent != null) {
            cursor = parent.nextChild()
        }
    }

    fun reset() {
        baseNode.resetPointer()
    }

    private fun renderNode(node: Node) {
        context.renderElement(node)
        for (child in node.children) {
            renderNode(child)
        }
    }

    fun render() {
        baseNode.layouts.layout.x = 0.0
        baseNode.layouts.layout.y = 0.0
        baseNode.layouts.layout.width = grandParentLayout.width
        baseNode.layouts.layout.height = grandParentLayout.height

        renderNode(baseNode)
        reset()
    }

    inner class Node(val parent: Node?, private val sibling: Node?) : RenderDescription {
        val children: ArrayList<Node> = ArrayList()
        val layout: MutableLayout = MutableLayout.create(context)
        override val style = Style()

        override val x: Double
            get() = layout.x * widthScale + xAdjust

        override val y: Double
            get() = layout.y * heightScale + yAdjust

        override val width: Double
            get() {
                return layout.width * widthScale
            }

        override val height: Double
            get() = layout.height * heightScale


        var currentChildIndex: Int = 0

        val parentLayout: Layout
            get() = parent?.layouts?.layout ?: grandParentLayout

        val firstLayout = object : StaticLayout(context) {
            override val x: Double
                get() = parentLayout.x
            override val y: Double
                get() = parentLayout.y
            override val width: Double = 0.0
            override val height: Double = 0.0
        }

        fun nextChild(): Node {
            val newChild = if (currentChildIndex < children.size) {
                children[currentChildIndex]
            } else {
                val previousNode = if (currentChildIndex > 0) children[currentChildIndex - 1] else null
                val newNode = Node(this, previousNode)
                children.add(currentChildIndex, newNode)
                newNode
            }

            currentChildIndex++
            return newChild
        }

        fun resetPointer() {
            currentChildIndex = 0
            for (child in children) {
                child.resetPointer()
            }
        }

        val layouts: NodeLayouts = object : NodeLayouts {
            override val layout: MutableLayout = this@Node.layout
            override val parent: Layout = this@Node.parent?.layouts?.layout ?: grandParentLayout
            override var sibling: Layout = this@Node.sibling?.layouts?.layout ?: firstLayout
        }
    }

}
