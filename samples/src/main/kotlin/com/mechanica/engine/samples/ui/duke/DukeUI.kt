package com.mechanica.engine.samples.ui.duke

import com.mechanica.engine.samples.ui.duke.builder.NodeBuilder
import com.mechanica.engine.samples.ui.duke.builder.NodeBuilderImpl
import com.mechanica.engine.samples.ui.duke.builder.ContentBuilder
import com.mechanica.engine.samples.ui.duke.builder.LayoutBuilderImpl
import com.mechanica.engine.samples.ui.duke.builder.StyleBuilderImpl
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
    private val rootLayout = object : StaticLayout(context) {
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
    val builderMap = mutableMapOf<KClass<*>, Any>()

    var cursor = baseNode
    var currentContentBuilder: ContentBuilder? = null

    inline fun build(builder: ContentBuilder.() -> Unit) {
        dukeBuilder.createNode().content(builder)
    }

    inner class DukeBuilder {
        val currentLayouts: NodeLayouts
            get() = cursor.layouts
        val currentStyle: Style
            get() = cursor.style
        val currentThemeHooks: MutableSet<String>
            get() = cursor.themeHooks
        val currentContentBuilder: ContentBuilder
            get() {
                val value = this@DukeUI.currentContentBuilder
                require(value != null) { "Tried to get the current content builder but it is null"}
                return value
            }

        val layout = LayoutBuilderImpl(this)
        val style = StyleBuilderImpl(this)

        fun matchThemeHook(hook: String) {
            val nodes = context.theme.getMatchedNodes(hook)
            if (nodes.isEmpty()) return

            if (nodes.size == 1) {
//                nodes[0].builder(currentStyle)
                return
            }

        }

        inline fun <reified T> useBuilder(constructor: (DukeBuilder) -> T): T {
            val builderClass = T::class

            if (builderMap.containsKey(builderClass)) {
                return builderMap[builderClass] as T
            } else {
                val builder = constructor(this)
                builderMap[builderClass] = builder as Any
                return builder
            }
        }

        inline fun <reified T> useBuilder(): T {
            val builderClass = T::class

            if (builderMap.containsKey(builderClass)) {
                return builderMap[builderClass] as T
            } else {
                throw IllegalStateException("Tried to get a builder of type ${T::class} but a builder of that type has not been instantiated")
            }
        }

        inline fun <reified C : ContentBuilder> createNodeWithBuilder(
            contentBuilderConstructor: (DukeBuilder) -> C, prepareBuilder: ((C) -> Unit) = {}): NodeBuilder<C> {
            nextChild()
//            currentContentBuilder.triggerOnPostAppend(cursor.currentChildIndex)
            val contentBuilder = useBuilder(contentBuilderConstructor)
            prepareBuilder(contentBuilder)
            this@DukeUI.currentContentBuilder = contentBuilder

            return useBuilder { NodeBuilderImpl(it) as NodeBuilder<C> }
        }

        fun createNode(): NodeBuilder<ContentBuilder> {
            return createNodeWithBuilder({ ContentBuilder(it) })
        }

        inline fun <C> buildContent(build: C.() -> Unit) {
            val oldCursor = cursor
            cursor = cursor.nextChild()
            build(currentContentBuilder as C)
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
        baseNode.layouts.layout.width = rootLayout.width
        baseNode.layouts.layout.height = rootLayout.height

        renderNode(baseNode)
        reset()
    }

    inner class Node(val parent: Node?, private val sibling: Node?) : RenderDescription {
        val children: ArrayList<Node> = ArrayList()
        val layout: MutableLayout = MutableLayout.create(context)
        override val style = Style()
        val themeHooks = mutableSetOf<String>()

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
            get() = parent?.layouts?.layout ?: rootLayout

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
            override val parent: Layout = this@Node.parent?.layouts?.layout ?: rootLayout
            override var sibling: Layout = this@Node.sibling?.layouts?.layout ?: firstLayout
        }
    }

}
