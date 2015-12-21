package net.hungerstruck.renaissance

import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Player object for our needs.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RPlayer {
    companion object : Listener {
        final var INSTANCES: MutableMap<Player, RPlayer> = hashMapOf()

        fun getPlayers(fn: (RPlayer) -> Boolean = { true }): Collection<RPlayer> {
            return INSTANCES.values.filter { fn(it) }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        fun onLogout(event: PlayerQuitEvent) {
            RPlayer.INSTANCES.remove(event.player)
        }
    }

    private val bukkit: Player
    var match: RMatch? = null

    constructor(bukkit: Player) {
        this.bukkit = bukkit
    }
}