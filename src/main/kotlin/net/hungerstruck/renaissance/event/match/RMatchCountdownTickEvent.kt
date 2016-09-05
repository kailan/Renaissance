package net.hungerstruck.renaissance.event.match

import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.event.HandlerList

/**
 * Created by teddy on 30/03/2016.
 */
class RMatchCountdownTickEvent(match: RMatch, val timeLeft: Int) : StruckMatchEvent(match) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}
