package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.*
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.RMatchStartEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.jdom2.Document
import java.util.*

/**
 * Created by molenzwiebel on 17-01-16.
 */
class ThirstModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    val playerThirst: WeakHashMap<Player, Int> = WeakHashMap()

    init {
        registerEvents()
    }

    private fun refreshThirst() {
        for (player in match.alivePlayers) {
            // FIXME: Maybe make these options configurable?

            val oldValue = playerThirst.getOrPut(player, { 100 })
            val difference = -1 * if (player.location.block.biome in arrayOf(Biome.DESERT, Biome.DESERT_HILLS, Biome.DESERT_MOUNTAINS)) 4 else 2

            // Increment by minus the amount of thirst they have
            playerThirst.incrementBy(player, difference, 100)

            if (oldValue > 25 && oldValue + difference <= 25) {
                player.sendMessage("${ChatColor.DARK_RED}My throat is rough and dry...")
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 60, 2))
            }

            if (oldValue > 50 && oldValue + difference <= 50) {
                player.sendMessage("${ChatColor.DARK_RED}My mouth feels dry...")
            }

            if (playerThirst[player]!! <= 0) {
                // Make sure we don't go negative
                playerThirst[player] = 0

                player.damage(RConfig.Thirst.healthDamage.toDouble())
                player.saturation = (player.saturation - RConfig.Thirst.hungerDamage).clamp(0f, 18f)
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 60, 2))
                player.sendMessage("You are dehydrated...")
            }

            player.exp = playerThirst[player]!! / 100f
        }
    }

    @EventHandler
    public fun onMatchStart(event: RMatchStartEvent) {
        if (!isMatch(event.match)) return

        // In seconds
        val refreshRate = 20L

        var id = 0
        id = Bukkit.getScheduler().runTaskTimer(Renaissance.plugin, {
            if (match.state != RMatch.State.PLAYING) {
                Bukkit.getScheduler().cancelTask(id)
            } else {
                refreshThirst()
            }
        }, 0, refreshRate * 20).taskId
    }

    @EventHandler
    public fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!isMatch(event.entity)) return
        playerThirst.remove(event.entity)
    }

    @EventHandler
    public fun onExp(event: PlayerExpChangeEvent) {
        if (!isMatch(event.player)) return
        event.amount = 0
    }

    @EventHandler
    public fun onInteractWithWater(event: PlayerInteractEvent) {
        if (!isMatch(event.player) || event.player.rplayer.state != RPlayer.State.PARTICIPATING || match.state != RMatch.State.PLAYING) return

        if (event.action == Action.RIGHT_CLICK_BLOCK &&
                (Material.WATER in event.player.getLineOfSight(setOf(Material.AIR), 4).map { it.type } ||
                 Material.STATIONARY_WATER in event.player.getLineOfSight(setOf(Material.AIR), 4).map { it.type })
                && playerThirst.getOrPut(event.player.rplayer, { 100 }) < 100) {
            // Completely refresh water
            playerThirst[event.player.rplayer] = 100

            event.player.sendMessage("You quench your thirst.")
            event.player.exp = 1f
        }
    }

    @EventHandler
    public fun onConsumeWaterBottle(event: PlayerItemConsumeEvent) {
        if (!isMatch(event.player) || event.player.rplayer.state != RPlayer.State.PARTICIPATING || match.state != RMatch.State.PLAYING) return
        if (event.item.type != Material.POTION) return
        if (playerThirst.getOrPut(event.player.rplayer, { 100 }) >= 100) return

        playerThirst[event.player.rplayer] = 100

        event.player.sendMessage("You quench your thirst.")
        event.player.exp = 1f
    }
}