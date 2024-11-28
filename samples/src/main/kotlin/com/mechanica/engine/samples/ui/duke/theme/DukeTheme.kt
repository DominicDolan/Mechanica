package com.mechanica.engine.samples.ui.duke.theme

class ThemeNodeImpl(
    private val themeMap: HashMap<String, MutableSet<ThemeNodeImpl>>,
    val ancestorHooks: ArrayList<String>,
    val builder: ThemeNode.() -> Unit)
: TrackedThemeNode() {
    private val childMap: HashMap<String, ThemeNodeImpl> = hashMapOf()
    private val childList: ArrayList<ThemeNodeImpl> = arrayListOf()

    override fun hook(themeHook: String, builder: ThemeNode.() -> Unit): ThemeNode {
        val childAncestorHooks = ArrayList(ancestorHooks)
        childAncestorHooks.add(themeHook)

        val child = ThemeNodeImpl(themeMap, childAncestorHooks, builder)

        themeMap.getOrPut(themeHook) { mutableSetOf() }.add(child)
        childMap[themeHook] = child
        childList.add(child)

        return child
    }

    fun has(hook: String): Boolean {
        if (childMap.containsKey(hook)) {
            return true
        }

        for (child in childList) {
            if (child.has(hook)) {
                return true
            }
        }

        return false
    }
}

class DukeTheme(rootBuilder: ThemeNode.() -> Unit) {
    private val themeMap = HashMap<String, MutableSet<ThemeNodeImpl>>()
    private val baseNode = ThemeNodeImpl(themeMap, arrayListOf(), rootBuilder)

    private var currentNode: ThemeNode? = null

    private val nodesArray = arrayListOf<ThemeNodeImpl>()
    fun getMatchedNodes(hook: String): ArrayList<ThemeNodeImpl> {
        nodesArray.clear()
        val set = themeMap[hook]

        if (set == null) {
            return nodesArray
        }

        set.forEach { node ->
            nodesArray.add(node)
        }

        return nodesArray
    }

}

fun buildTheme(builder: ThemeNode.() -> Unit): DukeTheme {
    return DukeTheme(builder)
}

val theme = buildTheme {
    hook("app") {
        color.set(0xFF00FF00)

        hook("main") {
            hook("container") {
                hook("list") {

                }
            }
        }

        hook("list") {

        }
    }

    hook("list") {

    }
}
