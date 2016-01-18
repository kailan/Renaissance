package net.hungerstruck.renaissance.event

import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.ChatColor
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 03-01-16.
 */
class RMatchEndEvent(val match: RMatch) : Event() {
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