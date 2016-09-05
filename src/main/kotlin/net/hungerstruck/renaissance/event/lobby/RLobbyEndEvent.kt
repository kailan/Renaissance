package net.hungerstruck.renaissance.event.lobby

import net.hungerstruck.renaissance.lobby.RLobby
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 01-01-16.
 */
class RLobbyEndEvent(lobby: RLobby) : StruckLobbyEvent(lobby) {

    companion object {
        val handlers = HandlerList()

        @JvmStatic fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return Companion.handlers
    }
}

