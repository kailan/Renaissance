package net.hungerstruck.renaissance.lobby

import net.hungerstruck.renaissance.config.RConfig
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

        val lobby = RLobby(world, lobbyMap = lobbyMap, nextMap = map)
        lobbies[world] = lobby

        return lobby
    }

    public fun unloadLobby(lobby: RLobby) {
        val dir = lobby.lobbyWorld.worldFolder
        Bukkit.unloadWorld(lobby.lobbyWorld, true)
        FileUtil.delete(dir)
        lobbies.remove(lobby.lobbyWorld)
    }

    // Note: May return null if there are no active lobbies.
    public fun findLobby(strategy: RConfig.JoinStrategy): RLobby? {
        if (lobbies.isEmpty()) return null

        return when (strategy) {
            RConfig.JoinStrategy.FIRST -> lobbies.values.first()
            RConfig.JoinStrategy.RANDOM -> lobbies.values.toArrayList()[Random().nextInt(lobbies.size)]
            RConfig.JoinStrategy.SMALLEST -> lobbies.values.minBy { it.members.size }
        }
    }

    public fun findLobby(world: World): RLobby? {
        return lobbies[world]
    }
}