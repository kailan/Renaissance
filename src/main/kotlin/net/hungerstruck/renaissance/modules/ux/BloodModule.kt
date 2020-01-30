package net.hungerstruck.renaissance.modules.ux

/***import co.enviark.struckbukkit.effects.Particle
import co.enviark.struckbukkit.effects.ParticleBuilder**/
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext

/**
 *
 *
 * STUBBED
 * needs to be reimplemented as part of twenty-twenty
 */

class BloodModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    //private lateinit var particle: ParticleBuilder

    override fun init() {
        //this.particle = ParticleBuilder().setParticle(Particle.BLOCK_CRACK).setOffset(0.25f).setCount(30).setData(Material.REDSTONE_BLOCK.id)
        registerEvents()
    }

   /** @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerHit(event: EntityDamageEvent) {
        if (event.isCancelled || !isMatch(event.entity)) return
        match.players.filter { it.getSetting<Boolean>(Settings.BLOOD_OPTIONS) == true }.forEach { it.bukkit.particles().play(particle.setLocation(event.entity.location.add(0.0, 1.0, 0.0))) }
    }**/
}
