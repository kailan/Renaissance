package net.hungerstruck.renaissance.xml

import com.google.common.collect.ImmutableList
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.util.LiquidMetal
import java.io.File

/**
 * Manages maps.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class RMapContext {
    protected final val maps: MutableMap<String, RMap> = hashMapOf()

    public fun getMapExact(name: String) = maps[name]
    public fun getMaps() = ImmutableList.copyOf(maps.values)

    public fun loadMaps(directory: File) {
        if (!directory.exists() || !directory.isDirectory) throw IllegalArgumentException("Illegal map path: ${directory.absolutePath}")

        for (f in directory.listFiles()) {
            if (!f.isDirectory) continue

            if (File(f, RConfig.Maps.mapFileName).exists()) {
                val map = RMap(f)
                maps.put(map.mapInfo.name, map)
            }
        }
    }

    public fun resolveLobbies() {
        for (map in maps.values) {
            // Ignore lobbies, those can't reference lobbies.
            if (map.mapInfo.lobbyProperties != null) continue

            val lobbyName = map.mapInfo.lobby ?: RConfig.Lobby.defaultLobby
            map.mapInfo.lobbyMap = matchMap(lobbyName) ?: throw IllegalArgumentException("Unknown lobby $lobbyName, (implicitly) referenced by ${map.mapInfo.friendlyDescription}")
            if (map.mapInfo.lobbyMap.mapInfo.lobby == null) throw IllegalArgumentException("Lobby $lobbyName, (implicitly) referenced by ${map.mapInfo.friendlyDescription}, is not a lobby.")
        }
    }

    public fun matchMap(query: String): RMap? {
        return LiquidMetal.fuzzyMatch(getMaps(), query, { it.mapInfo.name }, 0.9)
    }
}