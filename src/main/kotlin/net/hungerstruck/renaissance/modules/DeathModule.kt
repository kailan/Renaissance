package net.hungerstruck.renaissance.modules

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.player.RPlayerJoinMatchEvent
import net.hungerstruck.renaissance.getIgnoreBounds
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.teleportable
import net.hungerstruck.renaissance.xml.module.Dependencies
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleDamageEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.jdom2.Document
import java.util.*

/**
 * Handles player death, respawning and observer interaction prevention.
 *
 * Created by molenzwiebel on 03-01-16.
 */
@Dependencies
class DeathModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {
    private val watching: WeakHashMap<RPlayer, Int> = WeakHashMap()

    init {
        registerEvents()
    }

    private fun isSpectator(p: Player): Boolean {
        return p.rplayer.match == match && (p.rplayer.state == RPlayer.State.SPECTATING || p.rplayer.match!!.state != RMatch.State.PLAYING)
    }

    // ===========================================================
    // ================= Death and Respawning ====================
    // ===========================================================

    @EventHandler
    fun onPlayerJoin(event: RPlayerJoinMatchEvent) {
        if (!isMatch(event.match)) return
        if (match.state != RMatch.State.PLAYING) return

        event.player.state = RPlayer.State.SPECTATING
        event.player.reset()
        // TODO: use StruckBukkit collision API
        //event.player.spigot().collidesWithEntities = false
        event.player.allowFlight = true
        event.player.inventory.setItem(0, ItemStack(Material.COMPASS, 1))
        event.player.teleport(match.world.spawnLocation.teleportable)

        RPlayer.updateVisibility()
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!isMatch(event.entity)) return
        if (match.state != RMatch.State.PLAYING) return

        val victim = event.entity.rplayer
        victim.state = RPlayer.State.SPECTATING
        victim.reset(false)
        // TODO: use StruckBukkit collision API
        //victim.spigot().collidesWithEntities = false
        victim.gameMode = GameMode.SPECTATOR //TEMP

        victim.allowFlight = true

        val message = if (victim.killer != null) RConfig.Match.playerDeathByPlayerMessage else RConfig.Match.playerDeathByOtherMessage
        match.sendMessage(message.replace("%0\$s", victim.displayName).replace("%1\$c", (victim.killer?.displayName).toString()))

        if (match.endCheck()) {
            var winner: RPlayer

            if (match.alivePlayers.size == 1) {
                winner = match.alivePlayers[0]
            } else {
                winner = victim
            }

            match.announceWinner(winner)
        } else {
            match.sendMessage(RConfig.Match.playerRemainMessage.replace("%0\$d", match.alivePlayers.size.toString()))
        }

        match.world.strikeLightningEffect(victim.location)
        event.deathMessage = null
        RPlayer.updateVisibility()
    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        if (!isSpectator(event.player)) return

        event.respawnLocation = match.world.spawnLocation.teleportable
        event.player.inventory.setItem(0, ItemStack(Material.COMPASS, 1))
    }

    // ===========================================================
    // ================ Spectator Teleportation ==================
    // ===========================================================

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (match.state != RMatch.State.PLAYING || !isMatch(e.player) || e.player.rplayer.state != RPlayer.State.SPECTATING) return

        if (e.player.itemInHand.type == Material.COMPASS) {
            if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.RIGHT_CLICK_AIR) {
                val prev = (watching[e.player.rplayer] ?: 0) - 1
                watching[e.player.rplayer] = prev

                e.player.teleport(match.alivePlayers.getIgnoreBounds(prev))
            }

            if (e.action == Action.LEFT_CLICK_BLOCK || e.action == Action.LEFT_CLICK_AIR) {
                val next = (watching[e.player.rplayer] ?: 0) + 1
                watching[e.player.rplayer] = next

                e.player.teleport(match.alivePlayers.getIgnoreBounds(next))
            }
        }
    }

    // ===========================================================
    // ============= Spectator Player Inventories ================
    // ===========================================================

    @EventHandler
    fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {
        if (!isMatch(event.player)) return
        if (match.state != RMatch.State.PLAYING || event.player.rplayer.state != RPlayer.State.SPECTATING) return
        if (event.rightClicked !is Player) return

        val clicked = event.rightClicked as Player
        val inv = Bukkit.createInventory(null, 9 * 6, "${clicked.displayName}'s Inventory")

        // Main inv
        for (i in 0..27) {
            val it = clicked.inventory.getItem(i + 9)
            inv.setItem(i + 18, if (it == null) null else ItemStack(it))
        }

        // Hotbar
        for (i in 0..8) {
            val it = clicked.inventory.getItem(i)
            inv.setItem(i + 18 + 27, if (it == null) null else ItemStack(it))
        }

        val hp = ItemStack(Material.SPECKLED_MELON, clicked.health.toInt())
        var im = hp.itemMeta
        im.displayName = "${ChatColor.RED}Health"
        hp.setItemMeta(im)
        inv.setItem(0, hp)

        val hunger = ItemStack(Material.GRILLED_PORK, clicked.foodLevel)
        im = hunger.itemMeta
        im.displayName = "${ChatColor.GREEN}Hunger"
        hunger.setItemMeta(im)
        inv.setItem(1, hunger)

        if (clicked.inventory.helmet != null) inv.setItem(5, ItemStack(clicked.inventory.helmet))
        if (clicked.inventory.chestplate != null) inv.setItem(6, ItemStack(clicked.inventory.chestplate))
        if (clicked.inventory.leggings != null) inv.setItem(7, ItemStack(clicked.inventory.leggings))
        if (clicked.inventory.boots != null) inv.setItem(8, ItemStack(clicked.inventory.boots))

        event.player.openInventory(inv)
    }

    // ===========================================================
    // ================ Interaction Cancelling ===================
    // ===========================================================

    @EventHandler
    fun onFoodLevelChange(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            event.isCancelled = isSpectator(event.entity as Player)
        }
    }

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        event.isCancelled = isSpectator(event.player)
    }

    @EventHandler
    fun onItemPickup(event: PlayerPickupItemEvent) {
        event.isCancelled = isSpectator(event.player)
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        event.isCancelled = isSpectator(event.player)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        event.isCancelled = isSpectator(event.player)
    }

    @EventHandler
    fun onEntityTarget(event: EntityTargetEvent) {
        if (event.target is Player && isSpectator(event.target as Player)) {
            event.target = null
        }
    }

    @EventHandler
    fun onEntityDamageByEntityEvent(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && isSpectator(event.entity as Player)) {
            event.isCancelled = true
        } else if (event.damager is Player && isSpectator(event.damager as Player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity.type != EntityType.PLAYER) return
        if (event.isCancelled) return
        event.isCancelled = isSpectator(event.entity as Player)
    }

    @EventHandler
    fun onInteractEvent(event: PlayerInteractEvent) {
        event.isCancelled = isSpectator(event.player)
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player) return

        val player = (event.whoClicked as Player).rplayer
        if (player.match == null || player.match != match) return

        if (match.state == RMatch.State.PLAYING || match.state == RMatch.State.STARTING) {
            if (isSpectator(player)) {
                if (event.inventory.type != InventoryType.PLAYER) event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteractEvent(e: PlayerInteractEvent) {
        if (isSpectator(e.player) && e.action == Action.RIGHT_CLICK_BLOCK) {
            val blockState = e.clickedBlock.state
            if (blockState is InventoryHolder) {
                val player = e.player
                val initInventory = blockState.inventory
                val dupeInventory = Bukkit.createInventory(player, blockState.inventory.type, "Spectating")
                dupeInventory.contents = initInventory.contents.copyOf()
                player.openInventory(dupeInventory)
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onVehicleDamage(event: VehicleDamageEvent) {
        if (event.attacker is Player) {
            event.isCancelled = isSpectator(event.attacker as Player)
        }
    }

    @EventHandler
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        if (event.attacker is Player) {
            event.isCancelled = isSpectator(event.attacker as Player)
        }
    }

    @EventHandler
    fun onVehicleEnter(event: VehicleEnterEvent) {
        if (event.entered is Player) {
            event.isCancelled = isSpectator(event.entered as Player)
        }
    }
}