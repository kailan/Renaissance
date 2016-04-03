package net.hungerstruck.renaissance

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

/**
 * Created by molenzwiebel on 20-12-15.
 */
private fun Player.getRPlayer(): RPlayer {
    if (RPlayer.INSTANCES[this] != null) {
        return RPlayer.INSTANCES[this]!!
    }
    RPlayer.INSTANCES[this] = RPlayer(this)
    return RPlayer.INSTANCES[this]!!
}

public val Player.rplayer: RPlayer
    get() = getRPlayer()

fun <K> MutableMap<K, Int>.incrementBy(key: K, value: Int, defaultValue: Int = 0) {
    val existing = getOrPut(key, { defaultValue })
    put(key, existing + value)
}

infix fun Double.pow(x: Double): Double {
    return Math.pow(this, x)
}

public val Location.teleportable: Location
    get() = world.getHighestBlockAt(this).location.add(0.5, 0.5, 0.5)

fun <E> List<E>.getIgnoreBounds(i: Int): E {
    var index = i
    if (i < 0) {
        index = if ((i * -1) % size == 0) 0 else size - ((i * -1) % size)
    } else if (i >= size) {
        index = i % size
    }
    return get(index)
}

fun Int.times(f: () -> Unit) {
    var i = 0
    while (i < this) {
        f()
        i++
    }
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

fun <T : Comparable<T>> T.between(a: T, b: T, including: Boolean = true): Boolean {
    if (including) {
        return this >= a && this <= b
    } else return this > a && this < b
}

fun <T : Comparable<T>> T.clamp(min: T, max: T): T {
    if (this <= min) return min
    if (this >= max) return max
    return this
}

public fun <T, R> Collection<T>.mapAs(fn: T.() -> R) = map { it.fn() }

var random = Random()

fun <T> Array<T>.randomElement(): T{
    return this[random.nextInt(this.size)]
}