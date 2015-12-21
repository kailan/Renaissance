package net.hungerstruck.renaissance

import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.jdom2.Element

/**
 * Created by molenzwiebel on 20-12-15.
 */
public fun Player.getRPlayer(): RPlayer {
    if (RPlayer.INSTANCES[this] != null) {
        return RPlayer.INSTANCES[this]!!
    }
    RPlayer.INSTANCES[this] = RPlayer(this)
    return RPlayer.INSTANCES[this]!!
}

infix fun Double.pow(x: Double): Double {
    return Math.pow(this, x)
}

operator fun Element.get(attrName: String): String? {
    return getAttributeValue(attrName)
}

fun Vector.minX(other: Vector): Double {
    return if (x <= other.x) x else other.x
}

fun Vector.minY(other: Vector): Double {
    return if (y <= other.y) y else other.y
}

fun Vector.minZ(other: Vector): Double {
    return if (z <= other.z) z else other.z
}

fun Vector.maxX(other: Vector): Double {
    return if (x >= other.x) x else other.x
}

fun Vector.maxY(other: Vector): Double {
    return if (y >= other.y) y else other.y
}

fun Vector.maxZ(other: Vector): Double {
    return if (z >= other.z) z else other.z
}

fun Double.between(a: Double, b: Double, including: Boolean = true): Boolean {
    if (including) {
        return this >= a && this <= b
    } else return this > a && this < b
}

public fun <T, R> Collection<T>.mapAs(fn: T.() -> R) = map { it.fn() }