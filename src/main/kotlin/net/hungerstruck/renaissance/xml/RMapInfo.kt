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
        val lobby: RLobbyProperties?,
        val objective: String,
        val authors: List<Contributor>,
        val contributors: List<Contributor>,
        val rules: List<String>,
        val difficulty: Difficulty,
        val dimension: World.Environment) {

    lateinit var lobbyMap: RMap

    public val friendlyDescription: String
        get() = "$name by ${authors.map { it.name }.joinToString(", ")}"
}

/**
 * Contains lobby configuration, both for maps _referencing_ lobbies and for maps that _are_ lobbies.
 * lobbyName is only set if the map references a lobby, otherwise the others are set.
 *
 * Example:
 * <lobby>Foo</lobby> transforms into RLobbyProperties("Foo", false, false)
 * <lobby><name>Foo</name></lobby> transforms into RLobbyProperties("Foo", false, false)
 * <lobby><blockbreaking /><damage /></lobby> transforms into RLobbyProperties(null, true, true)
 */
data class RLobbyProperties(val lobbyName: String?, val canBreakBlocks: Boolean, val canTakeDamage: Boolean)

/**
 * Simple contributor data class.
 */
data class Contributor(val name: String, val contribution: String? = null)