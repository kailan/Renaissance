package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 01-02-16.
 */
class RPlayerJoinMatchEvent(player: RPlayer, val match: RMatch) : RPlayerEvent(player) {

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