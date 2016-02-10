package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.clamp
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.toInt
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.jdom2.Document
import java.util.*

/**
 * Created by molenzwiebel on 21-12-15.
 */
class SanityModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val airHeight: Int
    val overallLightLevel: Int

    val sanityChange = 2
    val sanityDamage = 1.0
    val sanityTick = 10L

    val playerSanity: WeakHashMap<Player, Int> = WeakHashMap()

    enum class Cause(val messages: Map<Int, String>) {
        HEIGHT(mapOf(
            75 to "The air is thin and hard to breathe!",
            50 to "The air is thin and hard to breathe!",
            25 to "The air is thin and hard to breathe!"
        )),
        CAVE(mapOf(
            75 to "The air is stale and hard to breathe!",
            50 to "The air is stale and hard to breathe!",
            25 to "The air is stale and hard to breathe!"
        )),
        LIGHT(mapOf(
            75 to "This place is dark and crazy!",
            50 to "You start to go insane from the darkness!",
            25 to "Find some light, you're going insane!"
        )),
        RADIUS(mapOf());
    }

    init {
        val el = document.rootElement.getChild("sanity")
        if (el == null) {
            this.airHeight = 150
            this.overallLightLevel = 11
        } else {
            this.airHeight = el.getChild("heightSettings")?.getChildTextNormalize("airHeight").toInt(defaultValue = 150)
            this.overallLightLevel = el.getChild("lightSettings")?.getChildTextNormalize("overallLightLevel").toInt(defaultValue = 11)
        }

        registerEvents()
    }

    @EventHandler
    public fun onMatchStart(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        var id = 0
        id = Bukkit.getScheduler().runTaskTimer(Renaissance.plugin, {
            if (match.state != RMatch.State.PLAYING) {
                Bukkit.getScheduler().cancelTask(id)
            } else {
                refreshSanity()
            }
        }, 0, sanityTick * 20).taskId
    }

    private fun refreshSanity() {
        for (player in match.alivePlayers) {
            val initial = playerSanity.getOrPut(player, { 100 })
            var cause: Cause? = null
            var level = initial

            // Air height decrement.
            if (player.location.y > airHeight) {
                level -= sanityChange
                cause = Cause.HEIGHT
            }

            // Lighting decrement.
            if (player.location.y < player.world.maxHeight) {
                val block = player.location.block
                if (block != null && block.lightLevel < overallLightLevel) {
                    level -= sanityChange
                    cause = Cause.LIGHT
                }
            }

            // Block decrement.
            if (player.location.y < player.world.maxHeight) {
                var stone = 0
                var count = 0

                for (x in player.location.blockX - 3..player.location.blockX + 3) {
                    for (y in player.location.blockY..player.location.blockY + 5) {
                        if (y >= player.world.maxHeight) continue
                        for (z in player.location.blockZ - 3..player.location.blockZ + 3) {
                            val block = player.world.getBlockAt(x, y, z)
                            if (block != null) {
                                count++
                                if (block.type in arrayOf(Material.SAND, Material.STONE, Material.DIRT)) stone++
                            }
                        }
                    }
                }

                // If more than 85% of the area is stone.
                if (stone.toDouble() / count.toDouble() >= 0.85) {
                    level -= sanityChange
                    cause = Cause.CAVE
                }
            }

            // Radius decrement.
            if (false) {
                // Do stuff
                level -= sanityChange
                cause = Cause.RADIUS
            }

            if (cause != null) {
                playerSanity[player] = level.clamp(0, 100)
                player.level = level.clamp(0, 100)

                for ((threshold, message) in cause.messages) {
                    if (initial > threshold && level <= threshold) player.sendMessage(message)
                }

                if (level < 10) {
                    player.damage(sanityDamage)
                }
            } else {
                playerSanity[player] = level.clamp(0, 100 - sanityChange) + sanityChange
                player.level = level.clamp(0, 100 - sanityChange) + sanityChange
            }
        }
    }
}