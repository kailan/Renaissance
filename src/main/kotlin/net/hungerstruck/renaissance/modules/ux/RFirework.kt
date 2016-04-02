package net.hungerstruck.renaissance.modules.ux

import net.hungerstruck.renaissance.randomElement
import org.bukkit.DyeColor
import org.bukkit.FireworkEffect
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import java.util.*

/**
 * Created by teddy on 31/03/2016.
 */
class RFirework(private var power: Int, private var effect: FireworkEffect) {

    fun getEffect(): FireworkEffect {
        return effect
    }

    fun setEffect(effect: FireworkEffect): RFirework {
        this.effect = effect
        return this
    }

    fun getPower(): Int {
        return power
    }

    fun setPower(power: Int): RFirework {
        this.power = power
        return this
    }

    fun play(location: Location): Firework {
        val firework = location.world.spawnEntity(location, EntityType.FIREWORK) as Firework

        val meta = firework.fireworkMeta
        meta.addEffect(effect)
        meta.power = power
        firework.fireworkMeta = meta
        return firework
    }

    companion object {

        private var random: Random? = Random()

        fun playRandom(location: Location): Firework {
            return RFirework(random!!.nextInt(2) + 1, randomEffect).play(location)
        }

        val randomEffect: FireworkEffect
            get() = FireworkEffect.builder().flicker(random!!.nextBoolean()).with(FireworkEffect.Type.values().randomElement()).withColor(DyeColor.values().randomElement().color).withFade(DyeColor.values().randomElement().color).trail(random!!.nextBoolean()).build()
    }


}
