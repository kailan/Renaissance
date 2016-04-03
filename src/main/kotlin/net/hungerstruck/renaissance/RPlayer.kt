package net.hungerstruck.renaissance

import me.anxuiz.settings.Setting
import net.hungerstruck.renaissance.lobby.RLobby
import net.hungerstruck.renaissance.match.RMatch
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import me.anxuiz.settings.bukkit.PlayerSettings

/**
 * Player object for our needs.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RPlayer(val bukkit: Player) : Player by bukkit {
    companion object : Listener {
        final var INSTANCES: MutableMap<Player, RPlayer> = hashMapOf()

        fun getPlayers(fn: (RPlayer) -> Boolean = { true }): List<RPlayer> {
            return INSTANCES.values.filter(fn)
        }

        fun updateVisibility() {
            for (p1 in Bukkit.getOnlinePlayers()) {
                for (p2 in Bukkit.getOnlinePlayers()) {
                    if (p1.rplayer.canSee(p2.rplayer)) {
                        p1.showPlayer(p2)
                    } else {
                        p1.hidePlayer(p2)
                    }

                    if (p2.rplayer.canSee(p1.rplayer)) {
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

    var match: RMatch? = null
    var lobby: RLobby? = null
    var previousState: RPlayerState? = null

    var actionBarMessage: String? = null

    var state = State.NONE
        set(x) {
            assert(x == State.NONE || match != null, { "Cannot set state to $x if not in a match" })
            field = x
        }

    /**
     * @return Setting value from Settings
     */
    inline fun <reified T : Any> getSetting(setting: Setting) = PlayerSettings.getManager(bukkit).getValue(setting, T::class.java)

    public fun reset(resetHealth: Boolean = true) {
        if (resetHealth) health = 20.0
        saturation = 20.0f
        exhaustion = 20.0f
        fireTicks = 0
        foodLevel = 20
        exp = 0.0f
        level = 0
        noDamageTicks = 0
        isSneaking = false
        isSprinting = false
        fallDistance = 0.0f
        isFlying = false
        allowFlight = false
        actionBarMessage = null

        for (effect in activePotionEffects)
            removePotionEffect(effect.type)

        inventory.clear()
        inventory.armorContents = arrayOfNulls<ItemStack>(inventory.armorContents.size)

        updateInventory()
    }

    /**
     * @return if this player can see the provided player.
     */
    public fun canSee(other: RPlayer): Boolean {
        return other.match == match && (other.state == state || (other.state == State.PARTICIPATING && state == State.SPECTATING) || match?.state != RMatch.State.PLAYING)
    }

    enum class State {
        // Not in a match at the moment.
        NONE,
        // Participating in the match.
        PARTICIPATING,
        // Not participating in the match, but still part of said match.
        SPECTATING;
    }
}