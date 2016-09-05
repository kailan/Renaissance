package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer
import org.bukkit.event.HandlerList

/**
 * Created by teddy on 30/03/2016.
 */
class RPlayerThirstUpdateEvent(player: RPlayer, val thirst: Int) : RPlayerEvent(player) {
    private val handlers = HandlerList()
    override fun getHandlers(): HandlerList {
        return handlers
    }
}
