package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.util.FileUtil
import net.hungerstruck.renaissance.xml.RMap
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*

/**
 * Manages lobbies.
 *
 * Created by molenzwiebel on 29-12-15.
 */
class RLobbyManager {
    private val lobbies: MutableMap<World, RLobby> = hashMapOf()
    private var lobbyCount: Int = 0

    public fun createLobbyFor(map: RMap): RLobby {
        val lobbyMap = map.mapInfo.lobbyMap

        val worldName = "lobby-${lobbyCount++}"
        val worldFolder = File(Bukkit.getServer().worldContainer, worldName)
        FileUtil.copyWorldFolder(lobbyMap.location, worldFolder)

        val gen = WorldCreator(worldName).generator(object : ChunkGenerator() {}).generateStructures(false).environment(lobbyMap.mapInfo.dimension)
        val world = Bukkit.createWorld(gen)
        world.isAutoSave = false
        world.difficulty = lobbyMap.mapInfo.difficulty

        val lobby = RLobby(lobbyMap = lobbyMap, nextMap = map)
        lobbies[world] = lobby

        return lobby
    }

    // Note: May return null if there are no active lobbies.
    public fun findLobby(strategy: LobbyStrategy): RLobby? {
        if (lobbies.isEmpty()) return null

        return when (strategy) {
            LobbyStrategy.FIRST -> lobbies.values.first()
            LobbyStrategy.RANDOM -> lobbies.values.toArrayList()[Random().nextInt(lobbies.size)]
            LobbyStrategy.SMALLEST -> lobbies.values.minBy { it.members.size }
        }
    }

    /**
     * The strategy for which a player gets assigned a lobby when he/she first joins.
     * <b>NOTE:</b> This is only for _joining_, not after a cycle. After a cycle, the player will join the next lobby.
     */
    enum class LobbyStrategy {
        /**
         * The first lobby in the lobbies map will be the one joined.
         */
        FIRST,
        /**
         * A random lobby will be selected.
         */
        RANDOM,
        /**
         * The lobby with the lowest amount of participants will be selected.
         */
        SMALLEST;
    }
}