package net.hungerstruck.renaissance.listeners

import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.getRPlayer
import net.hungerstruck.renaissance.lobby.RLobby
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.AsyncPlayerChatEvent

/**
 * As lobbies cannot contain modules (they are designed for matches), this listener handles block breaking, damage, etc.
 *
 * Created by molenzwiebel on 01-01-16.
 */
class LobbyListener : Listener {
    //FIXME: Maybe cancel interacts (chests etc)?

    // Redirect chat to only the lobby.
    @EventHandler
    public fun onChat(event: AsyncPlayerChatEvent) {
        if (event.message.startsWith("/")) return // Ignore commands.
        val lobby = event.player.getRPlayer().lobby ?: return

        lobby.sendMessage(RConfig.Lobby.chatFormat.format(event.player.name, event.message))
        event.isCancelled = true
    }

    @EventHandler
    public fun onBlockBreak(event: BlockBreakEvent) {
        val lobby = getLobby(event.block.world) ?: return

        event.isCancelled = lobby.lobbyMap.mapInfo.lobbyProperties!!.canBreakBlocks
    }

    @EventHandler
    public fun onPlayerDamageEvent(event: EntityDamageEvent) {
        val lobby = getLobby(event.entity.world) ?: return

        if (event is EntityDamageByEntityEvent) {
            assert(lobby == getLobby(event.damager.world), { "Somehow, these entities are not in the same lobby." })

            // Cancel any non-player damage
            if (event.damager !is Player) {
                event.isCancelled = true
                return
            }

            event.isCancelled = lobby.lobbyMap.mapInfo.lobbyProperties!!.canTakeDamage
        } else {
            // Always cancel any non-player damage. Teleport them to spawn if it is void damage.
            event.isCancelled = true
            if (event.cause == EntityDamageEvent.DamageCause.VOID) {
                event.entity.teleport(event.entity.world.spawnLocation)
            }
        }
    }

    private fun getLobby(world: World): RLobby? {
        return Renaissance.lobbyManager.findLobby(world)
    }
}