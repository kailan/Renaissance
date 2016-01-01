package net.hungerstruck.renaissance.listeners

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.getRPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Handles connections. Basically just assigns a player to a lobby or match.
 *
 * Created by molenzwiebel on 01-01-16.
 */
class ConnectionListener : Listener {
    @EventHandler
    public fun onPlayerConnect(event: PlayerJoinEvent) {
        val lobby = Renaissance.lobbyManager.findLobby(RConfig.Lobby.joinStrategy)
        event.joinMessage = null

        if (lobby != null) {
            lobby.join(event.player.getRPlayer())
            return
        }

        val match = Renaissance.matchManager.findMatch(RConfig.Match.joinStrategy)
        if (match != null) {
            // FIXME: Add some clean way to notify modules of a new player.
            assert(false)
            return
        }

        event.player.kickPlayer(RConfig.General.noMatchesMessage)
    }
}