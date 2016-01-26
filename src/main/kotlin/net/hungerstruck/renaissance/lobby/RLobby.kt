package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.RPlayerState
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.lobby.RLobbyEndEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.xml.RMap
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
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

        updateInformation()

        if (members.size >= RConfig.Lobby.minimumPlayerStartCount && members.size <= RConfig.Lobby.maximumPlayerStartCount && RConfig.Lobby.autoStart) {
            startCountdown()
        }
    }

    public fun startCountdown() {
        if (!Renaissance.countdownManager.hasCountdown(RLobbyEndCountdown::class.java)) {
            //FIXME: Config value, not hardcoded to 10s.
            Renaissance.countdownManager.start(RLobbyEndCountdown(this), 10)
        }
    }

    private fun updateInformation() {
        for (player in members) {
            player.actionBarMessage = "${if (lobbyMap.mapInfo.lobbyProperties!!.canTakeDamage) GREEN else RED}PVP $GRAY| ${if (lobbyMap.mapInfo.lobbyProperties.canBreakBlocks) GREEN else RED}Block Breaking $GRAY| $BLACK${members.size}/${RConfig.Lobby.maximumPlayerStartCount} players"
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
