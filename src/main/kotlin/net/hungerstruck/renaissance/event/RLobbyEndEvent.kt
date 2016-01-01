package net.hungerstruck.renaissance.event

import net.hungerstruck.renaissance.lobby.RLobby
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 01-01-16.
 */
class RLobbyEndEvent(val lobby: RLobby) : Event() {
    companion object {
        val handlers = HandlerList()

        @JvmStatic fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    override fun getHandlers(): HandlerList {
        return handlers
    }
}