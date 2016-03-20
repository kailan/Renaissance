package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
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
    }

    class TimeSetTask(val match: RMatch, val time: Long) : BukkitRunnable() {

        override fun run() {
            if(match.state == RMatch.State.LOADED)
                match.world.setTime(time);
            else
                this.cancel();
        }

    }

    @EventHandler
    public fun onMatchLoad(event: RMatchLoadEvent) {
         var task : BukkitTask =  TimeSetTask(event.match, value).runTaskTimer(Renaissance.plugin, 0, 20*10);
    }
}
