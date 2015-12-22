package net.hungerstruck.renaissance

import net.hungerstruck.renaissance.match.RLobby
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

/**
 * Player object for our needs.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RPlayer {
    companion object : Listener {
        final var INSTANCES: MutableMap<Player, RPlayer> = hashMapOf()

        fun getPlayers(fn: (RPlayer) -> Boolean = { true }): Collection<RPlayer> {
            return INSTANCES.values.filter { fn(it) }
        }

        fun updateVisibility() {
            for (p1 in Bukkit.getOnlinePlayers()) {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (p1.getRPlayer().canSee(p2.getRPlayer())) {
                        p1.showPlayer(p2)
                    } else {
                        p1.hidePlayer(p2)
                    }

                    if (p2.getRPlayer().canSee(p1.getRPlayer())) {
                        p2.showPlayer(p1)
                    } else {
                        p2.hidePlayer(p1)
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        fun onLogout(event: PlayerQuitEvent) {
            RPlayer.INSTANCES.remove(event.player)
        }
    }

    val bukkit: Player

    var match: RMatch? = null
    var lobby: RLobby? = null
    var previousState: RPlayerState? = null

    var state = State.NONE
        set(x) {
            assert(x == State.NONE || match != null, { "Cannot set state to ${x} if not in a match" })
            field = x
        }

    constructor(bukkit: Player) {
        this.bukkit = bukkit
    }

    public fun reset() {
        bukkit.health = 20.0
        bukkit.saturation = 20.0f
        bukkit.exhaustion = 20.0f
        bukkit.fireTicks = 0
        bukkit.foodLevel = 20
        bukkit.exp = 0.0f
        bukkit.level = 0
        bukkit.noDamageTicks = 0
        bukkit.isSneaking = false
        bukkit.isSprinting = false
        bukkit.fallDistance = 0.0f

        for (effect in bukkit.activePotionEffects)
            bukkit.removePotionEffect(effect.type)

        bukkit.inventory.clear()
        bukkit.inventory.armorContents = arrayOfNulls<ItemStack>(bukkit.inventory.armorContents.size)

        bukkit.updateInventory()
    }

    /**
     * @return if this player can see the provided player.
     */
    public fun canSee(other: RPlayer): Boolean {
        return other.match == match && (other.state == state || (other.state == State.ALIVE && state == State.SPECTATING))
    }

    enum class State {
        // Not in a match at the moment.
        NONE,
        // Still alive.
        ALIVE,
        // Dead or joined later, spectating in a match.
        SPECTATING;
    }
}