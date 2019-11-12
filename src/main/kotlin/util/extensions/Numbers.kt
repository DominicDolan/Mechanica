@file:Suppress("unused") // There will be many functions here that go unused most of the time
package util.extensions

fun Number.isHigher(other: Number): Boolean {
    return this.toDouble() > other.toDouble()
}

fun Number.isHigher(other1: Number, other2: Number): Boolean {
    return this.isHigher(other1) && this.isHigher(other2)
}

fun Number.isHigher(other1: Number, other2: Number, other3: Number): Boolean {
    return this.isHigher(other1) && this.isHigher(other2) && this.isHigher(other3)
} 

fun Number.isHigher(other1: Number, other2: Number, other3: Number, vararg others: Number): Boolean {
    var isHigher = false
    if (this.isHigher(other1) && this.isHigher(other2) && this.isHigher(other3)){
        isHigher = true
    }
    for (number in others) {
        if (this.isHigher(number)) {
            isHigher = true
        }
    }
    return isHigher
}

fun Number.isLower(other: Number): Boolean {
    return this.toDouble() < other.toDouble()
}

fun Number.isLower(other1: Number, other2: Number): Boolean {
    return this.isLower(other1) && this.isLower(other2)
}

fun Number.isLower(other1: Number, other2: Number, other3: Number): Boolean {
    return this.isLower(other1) && this.isLower(other2) && this.isLower(other3)
} 

fun Number.isLower(other1: Number, other2: Number, other3: Number, vararg others: Number): Boolean {
    var isLower = false
    if (this.isLower(other1) && this.isLower(other2) && this.isLower(other3)){
        isLower = true
    }
    for (number in others) {
        if (this.isLower(number)) {
            isLower = true
        }
    }
    return isLower
} 