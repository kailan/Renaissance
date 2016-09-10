package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler

data class RScheduledEvent(val duration : Long, val type: Type, val f: (RMatch, RModuleContext) -> Unit) {
    enum class Type {
        REPEATED, SINGLE
    }
}

class EventModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject var timerMap: MutableList<RScheduledEvent> = arrayListOf()
    val scheduledTasks: MutableList<Int> = arrayListOf()

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onMatchStartEvent(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        timerMap.forEach {
            when(it.type) {
                RScheduledEvent.Type.SINGLE ->
                        scheduledTasks += Bukkit.getScheduler().scheduleSyncDelayedTask(Renaissance.plugin, {
                            it.f(match, moduleContext)
                        }, it.duration)
                RScheduledEvent.Type.REPEATED ->
                    scheduledTasks += Bukkit.getScheduler().scheduleSyncRepeatingTask(Renaissance.plugin, {
                        it.f(match, moduleContext)
                    }, it.duration, it.duration)
            }
        }

    }

    @EventHandler
    fun onMatchEndEvent(event: RMatchEndEvent) {
        scheduledTasks.forEach {
            Bukkit.getScheduler().cancelTask(it)
        }
    }

}

