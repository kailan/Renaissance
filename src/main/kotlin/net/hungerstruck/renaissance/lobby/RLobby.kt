package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.RPlayerState
import net.hungerstruck.renaissance.xml.RMap
import org.bukkit.World

/**
 * Manages a simple match-specific lobby.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RLobby(val lobbyWorld: World, val lobbyMap: RMap, val nextMap: RMap) {
    val members: Collection<RPlayer>
        get() = RPlayer.getPlayers() { it.lobby == this }

    public fun join(player: RPlayer) {
        if (player.match != null || player.lobby != null) throw IllegalArgumentException("Player is already in match or lobby")

        player.lobby = this
        player.previousState = RPlayerState.create(player)
        player.reset()

        player.teleport(lobbyWorld.spawnLocation)

        //FIXME: Start countdown once enough players are in
    }

    public fun sendMessage(msg: String) {
        members.forEach { it.sendMessage(msg) }
    }
}
