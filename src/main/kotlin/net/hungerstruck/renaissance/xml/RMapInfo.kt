package net.hungerstruck.renaissance.xml

import org.bukkit.Difficulty
import org.bukkit.World

/**
 * Contains information about a map.
 *
 * Created by molenzwiebel on 20-12-15.
 */
data class RMapInfo(
        val name: String,
        val version: String,
        val objective: String,
        val authors: List<Contributor>,
        val contributors: List<Contributor>,
        val rules: List<String>,
        val difficulty: Difficulty,
        val dimension: World.Environment,
        val friendlyFire: Boolean) {

    public val friendlyDescription: String
        get() = "$name by ${authors.map { it.name }.joinToString(", ")}"
}

/**
 * Simple contributor data class.
 */
data class Contributor(val name: String, val contribution: String? = null)