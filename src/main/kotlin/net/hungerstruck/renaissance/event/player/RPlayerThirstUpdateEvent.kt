package net.hungerstruck.renaissance.event.player

import net.hungerstruck.renaissance.RPlayer
import org.bukkit.event.HandlerList

/**
 * Created by teddy on 30/03/2016.
 */
class RPlayerThirstUpdateEvent(player: RPlayer, val thirst: Int) : RPlayerEvent(player) {

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
