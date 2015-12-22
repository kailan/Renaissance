package net.hungerstruck.renaissance.config

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.util.Vector
import java.io.File

/**
 * Bukkit config ftw.
 *
 * Created by molenzwiebel on 22-12-15.
 */
object RConfig {
    val configFile: File = File("plugins/Renaissance/config.yml")
    val config: FileConfiguration = YamlConfiguration.loadConfiguration(configFile)

    object General {
        var debugging: Boolean by path("general.debugging")
    }

    object Maps {
        var mapDir: String by path("maps.dir", "maps")
        var mapFileName: String by path("maps.map-file", "map.xml")
        var rotationFileName: String by path("maps.rotation-file", "rotation.txt")
        var worldPrefix: String by path("maps.world-prefix", "match-")
    }

    object Lobby {
        var autoStart: Boolean by path("lobby.auto-start", true)
        var minimumPlayerStartCount: Int by path("lobby.minimum-player-start-count", 4)
        var maximumPlayerStartCount: Int by path("lobby.maximum-player-start-count", 24)

        var spawnLocation: Location by path("lobby.spawn-vector", {
            val parts = it.split(",")
            Vector(parts[1].toInt(), parts[2].toInt(), parts[3].toInt()).toLocation(Bukkit.getWorld(parts[0]))
        }, { "${it.world.name},${it.blockX},${it.blockY},${it.blockZ}" })
    }
}