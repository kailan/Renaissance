package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.modules.PedestalModule
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

/**
 * Created by teddy on 31/03/2016.
 */
class ParticlePedestalRunnable(private val module: PedestalModule, private val continuous: Boolean) : BukkitRunnable() {

    private var ped = 0
    private var fireworkEffect = RFirework.randomEffect

    override fun run() {
        var firework = RFirework(3, fireworkEffect).play(module.pedestals[ped].loc.toLocation(module.match.world).add(0.0, 6.5, 0.0))
        Bukkit.getScheduler().scheduleSyncDelayedTask(Renaissance.plugin, { firework.detonate() }, 3)

        ped += 2


        if (ped >= module.pedestals.size && ped % 2 == 0) {
            ped = 1
            fireworkEffect = RFirework.randomEffect
        } else if (ped >= module.pedestals.size && ped % 2 == 1) {
            ped = 0
            fireworkEffect = RFirework.randomEffect
            if (!continuous) cancel()
        }
    }

}
