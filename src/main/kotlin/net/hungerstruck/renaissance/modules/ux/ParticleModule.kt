package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.event.match.RMatchCountdownTickEvent
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.PedestalModule
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitTask
import java.util.*

class ParticleModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    private val random: Random = Random()
    private var timer: BukkitTask? = null

    override fun init() {
        registerEvents()
    }

    @EventHandler
    fun onMatchCountdownTick(event: RMatchCountdownTickEvent) {
        if (event.timeLeft == 5) {
            timer = ParticlePedestalRunnable(event.match.moduleContext.getModule<PedestalModule>() as PedestalModule, true).runTaskTimer(Renaissance.plugin, 0, 8)
        } else if (event.timeLeft > 5 && event.timeLeft % 10 == 0) {
            timer = ParticlePedestalRunnable(event.match.moduleContext.getModule<PedestalModule>() as PedestalModule, false).runTaskTimer(Renaissance.plugin, 0, 8)
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

    @EventHandler
    fun onMatchEnd(event: RMatchEndEvent) {
        if(event.winner == null) return

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Renaissance.plugin, {
            for (i in 0..4) {
                RFirework.playRandom(event.winner.world.getHighestBlockAt(event.winner.location.add(randomValue(), 0.0, randomValue())).location.add(0.0, 1.0, 0.0))
            }
        }, 0, 40)
    }

    private fun randomValue(): Double {
        var i = random.nextInt(15)
        if (random.nextBoolean()) i *= -1

        return i.toDouble()
    }
}
