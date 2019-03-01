package ui
var i = 0
fun main(args: Array<String>) {
    ui {
        div {

            div {
                div {
                    CustomDiv() {
                        div {

                        }
                    }
                }
                div {

                }
            }
        }
    }.render()
}

open class DIV {
    var layout: Layout? = null
    var style: Style? = null
    fun div(block: DIV.() -> Unit) {
        var j = i
        var tab = ""
        for (k in 0..j) {
            tab += "    "
        }
        println("$tab<div>")
        i++
        block()
        i--
        println("$tab</div>")
    }
}

class CustomDiv : DIV() {
    operator fun invoke(block: DIV.() -> Unit) {
        super.div(block)
    }
}

fun ui(block: DIV.() -> Unit): uiRenderer {
    block(DIV())
    return uiRenderer()
}

class uiRenderer {
    fun render() {

    }
}

class Style {

}

class Layout {

}