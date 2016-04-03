package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.clamp
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.event.player.RPlayerSanityUpdateEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.minecraft.server.PacketPlayOutWorldBorder
import net.minecraft.server.WorldBorder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

/**
 * Created by molenzwiebel on 21-12-15.
 */
@Dependencies(BoundaryModule::class)
class SanityModule(match: RMatch, modCtx: RModuleContext) : RModule(match, modCtx) {
    val airHeight: Int = 0
    val overallLightLevel: Int = 0

    val sanityChange = 2
    val sanityDamage = 1.0
    val sanityTick = 6L

    val playerSanity: WeakHashMap<Player, Int> = WeakHashMap()

    var radius = 0
    var radiusDecrease = 0

    enum class Cause(val messages: Map<Int, String>) {
        HEIGHT(mapOf(
            75 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.YELLOW}The air is thin and hard to breathe!",
            50 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.RED}The air is thin and hard to breathe!",
            25 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.DARK_RED}The air is thin and hard to breathe!"
        )),
        CAVE(mapOf(
            75 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.YELLOW}The air is stale and hard to breathe!",
            50 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.RED}The air is stale and hard to breathe!",
            25 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.DARK_RED}The air is stale and hard to breathe!"
        )),
        LIGHT(mapOf(
            75 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.YELLOW}This place is dark and crazy!",
            50 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.RED}You start to go insane from the darkness!",
            25 to "${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}${ChatColor.DARK_RED}Find some light, you're going insane!"
        )),
        RADIUS(mapOf());
    }

    init {
        registerEvents()
    }

    @EventHandler
    public fun onMatchStart(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        val boundaryMod = getModule<BoundaryModule>()!!
        val diffX = (boundaryMod.region.max.blockX - boundaryMod.region.min.blockX) / 2
        val diffZ = (boundaryMod.region.max.blockZ - boundaryMod.region.min.blockZ) / 2

        radius = Math.max(diffX, diffZ)
        radiusDecrease = if (match.alivePlayers.size > 1) Math.max(0, (radius - 60) / (match.alivePlayers.size - 1)) else 0
        match.alivePlayers.forEach { sendWarningRadius(it) }

        var id = 0
        id = Bukkit.getScheduler().runTaskTimer(Renaissance.plugin, {
            if (match.state != RMatch.State.PLAYING) {
                Bukkit.getScheduler().cancelTask(id)
            } else {
                refreshSanity()
            }
        }, 0, sanityTick * 20).taskId
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!isMatch(event.entity)) return
        if (match.state != RMatch.State.PLAYING) return

        radius -= radiusDecrease
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
            val boundaryMod = getModule<BoundaryModule>()!!
            val isInside = Math.abs(player.location.blockX - boundaryMod.center.blockX) < radius && Math.abs(player.location.blockZ - boundaryMod.center.blockZ) < radius
            if (!isInside) {
                // Do stuff
                level -= sanityChange
                cause = Cause.RADIUS
            }
            sendWarningRadius(player, isInside)

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

            Bukkit.getPluginManager().callEvent(RPlayerSanityUpdateEvent(player, playerSanity[player]!!))
        }
    }

    public fun sendWarningRadius(player: RPlayer, inside: Boolean = true) {
        val boarder = WorldBorder()
        boarder.setCenter(0.0, 0.0)
        boarder.size = if (inside) 10000000.0 else -1.0
        boarder.warningDistance = 1

        player.bukkit as CraftPlayer

        player.bukkit.handle.playerConnection.sendPacket(PacketPlayOutWorldBorder(boarder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE))
        player.bukkit.handle.playerConnection.sendPacket(PacketPlayOutWorldBorder(boarder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER))
    }
}