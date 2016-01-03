package net.hungerstruck.renaissance.util

import java.util.*

/**
 * Ported from HungerGames, idk who wrote it.
 *
 * Created by molenzwiebel on 03-01-16.
 */
class RandomCollection<E> {
    val map: NavigableMap<Double, E> = TreeMap()
    val random: Random = Random()
    var total: Double = 0.0

    public fun add(weight: Double, entry: E) {
        if (weight <= 0) return
        total += weight
        map.put(total, entry)
    }

    public fun next(): E {
        val value = random.nextDouble() * total
        return map.ceilingEntry(value).value
    }
}