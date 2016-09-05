package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer
import org.bukkit.event.HandlerList

/**
 * Created by teddy on 30/03/2016.
 */
class RPlayerSanityUpdateEvent(player: RPlayer, val sanity: Int) : RPlayerEvent(player) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}