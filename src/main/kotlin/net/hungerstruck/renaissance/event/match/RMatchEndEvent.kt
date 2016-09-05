package net.hungerstruck.renaissance.event.match

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.event.HandlerList

class RMatchEndEvent(match: RMatch, val winner: RPlayer?) : StruckMatchEvent(match) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}
