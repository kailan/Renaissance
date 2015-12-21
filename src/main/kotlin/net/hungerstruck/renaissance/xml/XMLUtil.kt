package net.hungerstruck.renaissance.xml

import org.bukkit.util.Vector
import org.jdom2.Element

/**
 * Contains various XML parsing utilities.
 *
 * Created by molenzwiebel on 20-12-15.
 */
public fun Element.flatten(parentTag: String, childTag: String): List<Element> {
    val result: MutableList<Element> = arrayListOf()
    for (parent in getChildren(parentTag)) {
        result.addAll(copyAttributesTo(parent).flatten(parentTag, childTag))
    }
    for (child in getChildren(childTag)) {
        result.add(copyAttributesTo(child))
    }
    return result
}

public fun Element.copyAttributesTo(to: Element): Element {
    val result = to.clone()
    for (attribute in attributes) {
        if (result.getAttribute(attribute.name) == null) {
            result.setAttribute(attribute.name, attribute.value)
        }
    }
    return result
}

public fun String?.toBool(defaultValue: Boolean = true): Boolean {
    if (this == null) return false
    if (this.toLowerCase().equals("on") || this.toLowerCase().equals("true")
            || this.toLowerCase().equals("yes")) return true
    if (this.toLowerCase().equals("off") || this.toLowerCase().equals("false")
            || this.toLowerCase().equals("no")) return false
    return false
}

public fun String?.toLong(defaultValue: Long = 0): Long {
    if (this == null) return defaultValue
    if (this == "oo" || this == "∞") return java.lang.Long.MAX_VALUE
    if (this == "-oo" || this == "-∞") return java.lang.Long.MIN_VALUE

    var str = this
    if (this.length > 0 && this.get(0) == '@') str = substring(1)

    return try {
        str.toLong()
    } catch (nfe: NumberFormatException) {
        defaultValue
    }
}

public fun String?.toInt(defaultValue: Int = 0): Int {
    if (this == null) return defaultValue
    if (this == "oo" || this == "∞") return java.lang.Integer.MAX_VALUE
    if (this == "-oo" || this == "-∞") return java.lang.Integer.MIN_VALUE

    var str = this
    if (this.length > 0 && this.get(0) == '@') str = substring(1)

    return try {
        str.toInt()
    } catch (nfe: NumberFormatException) {
        defaultValue
    }
}

public inline fun <reified T : Enum<*>> String?.toEnum(defaultValue: T? = null): T? {
    if (this == null) return defaultValue
    val values = T::class.java.getDeclaredMethod("values").invoke(null) as Array<T>
    return values.find { it.name.toLowerCase().equals(toLowerCase()) } ?: defaultValue
}

public fun String?.parseVector3D(): Vector {
    if (this == null) throw IllegalArgumentException("Parsing vector on null string")
    if (split(",").size != 3) throw IllegalArgumentException("Vectors should contain 3 numbers. Given " + this)
    return Vector(split(",")[0].toDouble(), split(",")[1].toDouble(), split(",")[2].toDouble())
}

public fun String?.parseVector2D(): Vector {
    if (this == null) throw IllegalArgumentException("Parsing vector on null string")
    if (split(",").size != 3) throw IllegalArgumentException("2D Vector should contain 2 numbers. Given " + this)
    return Vector(split(",")[0].toDouble(), 0.0, split(",")[1].toDouble())
}