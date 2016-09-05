package net.hungerstruck.renaissance.modules

import com.google.common.collect.Sets
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.entity.ExplosionPrimeEvent
import java.util.*

class TNTSettingsModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    @inject val blockDamage = false
    @inject val instantIgnite = false
    @inject val damageUnderWater = false // Note: Damage under water cannot occur if blockDamage is false
    @inject val `yield`: Float = -1.0f

    private val entityPowerMap = HashMap<Int, Float>()
    private val random = Random()

    override fun init() {
        registerEvents()
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onExplosionPrime(event: ExplosionPrimeEvent) {
        if (damageUnderWater && !event.isCancelled()) {
            entityPowerMap.put(Integer.valueOf(event.getEntity().getEntityId()), java.lang.Float.valueOf(event.getRadius()))
        }
    }

    @EventHandler
    fun onEntityExplodeEvent(event: EntityExplodeEvent) {
        if (!isMatch(event.world)) return
        if (!(event.getEntity() is TNTPrimed)) return;

        // Runs simulated explosion for every explosion if damage under water is enabled, the simulated explosion treats water like air
        if (damageUnderWater && !event.isCancelled && event.entityType !== EntityType.ENDER_DRAGON && entityPowerMap.containsKey(Integer.valueOf(event.entity.entityId))) {
            correctExplosion(event, (entityPowerMap.get(Integer.valueOf(event.entity.entityId)) as Float).toFloat())
            entityPowerMap.remove(Integer.valueOf(event.entity.entityId))
        }

        if(!blockDamage)
            event.blockList().clear()

        if(`yield` != -1.0f)
            event.`yield` = `yield`.toFloat()

    }

    @EventHandler
    fun onTNTPlaceEvent(event: BlockPlaceEvent) {
        if (!isMatch(event.world)) return
        if (!event.block.type.equals(Material.TNT)) return


        if (instantIgnite) {
            event.block.type = Material.AIR
            event.block.world.spawnEntity(Location(event.block.world, event.block.x + 0.5, event.block.y + 0.5, event.block.z + 0.5), EntityType.PRIMED_TNT)
            // TODO: Does spawning it call ExplosionPrime (and thus allow for fun underwater explosions?), or does this need to manually add itself to entityPowerMap?
        }
    }

    /**
     * Code taken from decompiled nms code at the Explosion patch, method a()
     * 1.8.8
     *
     * Modified to treat stationary water and water as air blocks during an explosion
     */
    fun correctExplosion(event: EntityExplodeEvent, power: Float) {
        if (power < 0.1f) {
            return
        }
        event.blockList().clear()
        val hashset = Sets.newHashSet<Block>()
        for (k in 0..15) {
            for (i in 0..15) {
                for (j in 0..15) {
                    if (k == 0 || k == 15 || i == 0 || i == 15 || j == 0 || j == 15) {
                        var d0 = (k / 15.0f * 2.0f - 1.0f).toDouble()
                        var d1 = (i / 15.0f * 2.0f - 1.0f).toDouble()
                        var d2 = (j / 15.0f * 2.0f - 1.0f).toDouble()
                        val d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)

                        d0 /= d3
                        d1 /= d3
                        d2 /= d3
                        var f = power * (0.7f + random.nextFloat() * 0.6f)
                        var d4 = event.location.x
                        var d5 = event.location.y
                        var d6 = event.location.z
                        while (f > 0.0f) {
                            val block = event.world.getBlockAt(Math.floor(d4).toInt(), Math.floor(d5).toInt(), Math.floor(d6).toInt())
                            if (block.type != Material.AIR && block.type != Material.WATER && block.type != Material.STATIONARY_WATER ) {
                                val f2 = block.durability // relies on Strukkit durability api patch
                                f -= (f2 + 0.3f) * 0.3f
                            }
                            if (f > 0.0f && d5 < 256 && d5 >= 0) {
                                hashset.add(block)
                            }
                            d4 += d0 * 0.30000001192092896
                            d5 += d1 * 0.30000001192092896
                            d6 += d2 * 0.30000001192092896
                            f -= 0.22500001f
                        }
                    }
                }
            }
        }

        event.blockList().addAll(hashset)
    }
}