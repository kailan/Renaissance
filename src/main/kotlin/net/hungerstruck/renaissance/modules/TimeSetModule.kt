package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toLong
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.jdom2.Document

@Dependencies(TimeLockModule::class)
class TimeSetModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val value: Long

    init {
        value = document.rootElement?.getChild("timestart")?.textNormalize.toLong()
        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
        if (!isMatch(event.match)) return
        
        event.match.world.setTime(value)
        event.match.world.setGameRuleValue("doDaylightCycle", "false"); // stop time during load, will be set to true if the lock module determines it should be
    }
}
