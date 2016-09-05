package net.hungerstruck.renaissance.event.match

import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.event.HandlerList

/**
 * Created by molenzwiebel on 01-01-16.
 */
class RMatchStartEvent(match: RMatch) : StruckMatchEvent(match) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}