package net.hungerstruck.renaissance.event.lobby

import net.hungerstruck.renaissance.lobby.RLobby
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 01-01-16.
 */
class RLobbyEndEvent(lobby: RLobby) : StruckLobbyEvent(lobby) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}
