package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.util.BlockOverride
import net.hungerstruck.renaissance.xml.builder.inject
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.minecraft.server.v1_8_R3.MathHelper
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
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
    @inject val damageUnderWater = true
    @inject val `yield`: Float = -1.0f

    private val entityPowerMap = HashMap<Int, Float>()
    private val random = Random()

    override fun init() {
        registerEvents()
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    fun onExplosionPrime(event: ExplosionPrimeEvent) {
        if (!event.isCancelled()) {
            entityPowerMap.put(Integer.valueOf(event.getEntity().getEntityId()), java.lang.Float.valueOf(event.getRadius()))
        }
    }

    @EventHandler
    fun onEntityExplodeEvent(event: EntityExplodeEvent) {
        if (!isMatch(event.world)) return
        if (!(event.getEntity() is TNTPrimed)) return;


        if (!event.isCancelled && event.entityType !== EntityType.ENDER_DRAGON && entityPowerMap.containsKey(Integer.valueOf(event.entity.entityId))) {
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
        }
    }

    fun correctExplosion(event: EntityExplodeEvent, power: Float) {
        val world = event.entity.world
        event.blockList().clear()
        for (i in 0..15) {
            for (j in 0..15) {
                for (k in 0..15) {
                    if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                        var d3 = (i / 15.0f * 2.0f - 1.0f).toDouble()
                        var d4 = (j / 15.0f * 2.0f - 1.0f).toDouble()
                        var d5 = (k / 15.0f * 2.0f - 1.0f).toDouble()
                        val d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5)

                        d3 /= d6
                        d4 /= d6
                        d5 /= d6
                        var f1 = power * (0.7f + random.nextFloat() * 0.6f)

                        var d0 = event.location.x
                        var d1 = event.location.y
                        var d2 = event.location.z
                        val f2 = 0.3f
                        while (f1 > 0.0f) {
                            val l = MathHelper.floor(d0)
                            val i1 = MathHelper.floor(d1)
                            val j1 = MathHelper.floor(d2)
                            val k1 = world.getBlockAt(1, i1, j1).typeId

                            if (k1 > 0 && k1 != 8 && k1 != 9 && k1 != 10 && k1 != 11) {
                                Bukkit.getLogger().info(Material.getMaterial(k1).name + " durability = " + BlockOverride(net.minecraft.server.v1_8_R3.Block.getById(k1)).get("durability"))
                                f1 -= (BlockOverride(net.minecraft.server.v1_8_R3.Block.getById(k1)).get("durability") as Float + 0.3f) * f2
                            }
                            if (f1 > 0.0f && i1 < 256 && i1 >= 0 && k1 != 8 && k1 != 9 && k1 != 10 && k1 != 11) {
                                val block = world.getBlockAt(l, i1, j1)
                                if (block.type != Material.AIR && !event.blockList().contains(block)) {
                                    event.blockList().add(block)
                                }
                            }
                            d0 += d3 * f2
                            d1 += d4 * f2
                            d2 += d5 * f2
                            f1 -= f2 * 0.75f
                        }
                    }
                }
            }
        }
    }
}