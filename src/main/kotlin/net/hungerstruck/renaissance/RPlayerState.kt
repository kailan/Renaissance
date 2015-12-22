package net.hungerstruck.renaissance

import com.google.common.collect.Lists
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect

/**
 * Created by molenzwiebel on 22-12-15.
 */
class RPlayerState internal constructor() {
    private var inventory: Array<ItemStack>? = null
    private var armorSlots: Array<ItemStack>? = null

    private var potionEffects: List<PotionEffect>? = null
    private var playerGamemode: GameMode? = null
    private var playerLocation: Location? = null

    private var health: Double = 0.0
    private var maxHealth: Double = 0.0
    private var food: Int = 0
    private var saturation: Float = 0.0f
    private var fire: Int = 0

    fun restore(target: Player) {
        target.inventory.contents = inventory
        target.inventory.armorContents = armorSlots

        for (existingPotionEffect in target.activePotionEffects) {
            target.removePotionEffect(existingPotionEffect.type)
        }
        for (potionEffect in potionEffects!!) {
            target.addPotionEffect(potionEffect)
        }
        target.gameMode = playerGamemode
        target.teleport(playerLocation)

        target.health = health
        target.maxHealth = maxHealth
        target.foodLevel = food
        target.saturation = saturation
        target.fireTicks = fire
    }

    companion object {
        fun create(player: Player): RPlayerState {
            val ret = RPlayerState()

            ret.inventory = player.inventory.contents
            ret.armorSlots = player.inventory.armorContents

            ret.potionEffects = Lists.newArrayList(player.activePotionEffects)
            ret.playerGamemode = player.gameMode
            ret.playerLocation = player.location

            ret.health = player.health
            ret.maxHealth = player.maxHealth
            ret.food = player.foodLevel
            ret.saturation = player.saturation
            ret.fire = player.fireTicks

            return ret
        }
    }
}