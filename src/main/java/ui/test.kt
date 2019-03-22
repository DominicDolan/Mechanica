package ui

import loader.ioResourceToByteBuffer

var i = 0
fun main(args: Array<String>) {
    val document = ui {
        div {
            div {
                div {
                    span {
                        div {
                            div {

                            }
                        }
                    }
                }
                div {

                }
            }
        }
    }

    var currentDoc = document
    var children = currentDoc.childCount
    var siblings = 1

    childRecursion(document)
}

fun childRecursion(element: TAG) : TAG? {
    if (element.childCount == 0) return null
    var i = 0
    println("child")
    while (childRecursion(element[i++]) != null) {
    }

    return childRecursion(element[element.childCount-1])
}

open class TAG(var tag: String = "") : Iterable<TAG>{
    private var children = ArrayList<TAG>()
    val childCount: Int
        get() = children.size

    var layout: Layout? = null
    var style: Style? = null
    fun tag(block: TAG.() -> Unit) {
        val tag = TAG()
        children.add(tag)
        block(TAG())
    }

    operator fun get(i: Int): TAG {
        return children[i]
    }


    override fun iterator(): Iterator<TAG> {
        return children.iterator()
    }


}

fun TAG.div(block: TAG.() -> Unit) {
    this.tag = "div"
    tag(block)
}

fun TAG.span(block: TAG.() -> Unit) {
    this.tag = "span"
    tag(block)
}

//open class DIV : TAG("div") {
//    fun div(block: DIV.() -> Unit) {
//        tag {
//            block()
//        }
//    }
//}


open class SPAN : TAG("span") {
    fun span(block: SPAN.() -> Unit) {
        tag {
            block()
        }
    }
}

//class CustomDiv : DIV() {
//    fun cDiv(block: CustomDiv.() -> Unit) {
////        div(block)
//    }
//}

fun ui(block: TAG.() -> Unit): TAG {
    val tag = TAG()
    block(tag)
    return tag
}

class uiRenderer {
    fun render() {

    }
}

class Style {

}

class Layout {

}