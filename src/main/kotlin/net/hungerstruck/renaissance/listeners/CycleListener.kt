package net.hungerstruck.renaissance.listeners

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.countdown.CountdownManager
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.player.RPlayerJoinMatchEvent
import net.hungerstruck.renaissance.rplayer
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CycleListener : Listener {
    @EventHandler
    public fun onMatchEndEvent(event: RMatchEndEvent) {
        for (player in event.match.players) {
            player.match = null
            player.lobby = null
            player.kickPlayer("Thanks for playing! The server will now prepare for the next match. Join back in a few minutes!");
            //lobby.join(player)
        }

        assert(event.match.players.size == 0, { "Still players left in match after cycle." })

        Renaissance.matchManager.unloadMatch(event.match)
        Renaissance.countdownManager.cancelAll();

        Renaissance.lobbyManager.createLobbyFor(Renaissance.mapContext.getMaps().first { it.mapInfo.lobbyProperties == null })
    }
}