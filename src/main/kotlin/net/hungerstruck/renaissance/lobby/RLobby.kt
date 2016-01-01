package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.RPlayerState
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.RLobbyEndEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.RMap
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World

/**
 * Manages a simple match-specific lobby.
 *
 * Created by molenzwiebel on 22-12-15.
 */
class RLobby {
    val lobbyWorld: World
    val lobbyMap: RMap
    val nextMap: RMap
    val match: RMatch

    constructor(lobbyWorld: World, lobbyMap: RMap, nextMap: RMap) {
        this.lobbyWorld = lobbyWorld
        this.lobbyMap = lobbyMap
        this.nextMap = nextMap

        this.match = Renaissance.matchManager.constructMatch(nextMap)
    }

    val members: Collection<RPlayer>
        get() = RPlayer.getPlayers() { it.lobby == this }

    public fun join(player: RPlayer) {
        if (player.match != null || player.lobby != null) throw IllegalArgumentException("Player is already in match or lobby")

        player.lobby = this
        player.previousState = RPlayerState.create(player)
        player.reset()
        player.gameMode = GameMode.SURVIVAL

        player.teleport(lobbyWorld.spawnLocation)

        if (members.size >= RConfig.Lobby.minimumPlayerStartCount && members.size <= RConfig.Lobby.maximumPlayerStartCount && RConfig.Lobby.autoStart) {
            if (!Renaissance.countdownManager.hasCountdown(RLobbyEndCountdown::class.java)) {
                //FIXME: Config value, not hardcoded to 10s.
                Renaissance.countdownManager.start(RLobbyEndCountdown(this), 10)
            }
        }
    }

    public fun end() {
        Bukkit.getPluginManager().callEvent(RLobbyEndEvent(this))
        assert(members.size == 0, { "Still players left in lobby after end." })
        Renaissance.lobbyManager.unloadLobby(this)
    }

    public fun sendMessage(msg: String) {
        members.forEach { it.sendMessage(msg) }
    }
}
