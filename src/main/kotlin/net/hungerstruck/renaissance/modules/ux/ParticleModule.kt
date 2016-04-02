package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchCountdownTickEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.PedestalModule
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitTask
import org.jdom2.Document
import java.util.*

/**
 * Created by teddy on 31/03/2016.
 */
class ParticleModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {

    private val random: Random

    private var timer: BukkitTask? = null


    init {
        registerEvents()
        this.random = Random()
    }

    @EventHandler
    fun onMatchCountdownTick(event: RMatchCountdownTickEvent) {
        if (event.timeLeft == 5) {
            timer = ParticlePedestalRunnable(event.match.moduleContext.getModule<PedestalModule>() as PedestalModule, true).runTaskTimer(Renaissance.plugin, 0, 3)
        } else if (event.timeLeft > 5 && event.timeLeft % 10 == 0) {
            timer = ParticlePedestalRunnable(event.match.moduleContext.getModule<PedestalModule>() as PedestalModule, false).runTaskTimer(Renaissance.plugin, 0, 3)
        }
    }

    @EventHandler
    fun onMatchStart(event: RMatchStartEvent) {
        timer?.cancel()

        val fireworkEffect = RFirework.randomEffect
        for (pedistal in (event.match.moduleContext.getModule<PedestalModule>())?.pedestals!!) {
            var fwork = RFirework(1, fireworkEffect).play(pedistal.loc.toLocation(match.world))
            Bukkit.getScheduler().runTaskLater(Renaissance.plugin, { fwork.detonate() }, 20)
        }
    }
}
