package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toLong
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.jdom2.Document

/**
 * Created by molenzwiebel on 21-12-15.
 */
class TimeSetModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val value: Long

    init {
        value = document.rootElement?.getChild("timestart")?.textNormalize.toLong()
        registerEvents()
    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
         event.match.world.setTime(value)
    }

    @EventHandler
    public fun onMatchStart(event: RMatchStartEvent) {
        event.match.world.setTime(value)
    }
}
