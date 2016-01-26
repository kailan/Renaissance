package net.hungerstruck.renaissance.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class StruckEvent : Event() {

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