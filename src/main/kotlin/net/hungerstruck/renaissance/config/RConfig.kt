package net.hungerstruck.renaissance.config

import net.hungerstruck.renaissance.lobby.RLobbyManager
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
        var defaultLobby: String by path("lobby.default-lobby")
        var joinStrategy: RLobbyManager.LobbyStrategy by path("lobby.join-strategy", { RLobbyManager.LobbyStrategy.valueOf(it) }, { it.name })

        var autoStart: Boolean by path("lobby.auto-start", true)
        var minimumPlayerStartCount: Int by path("lobby.minimum-players", 2)
        var maximumPlayerStartCount: Int by path("lobby.maximum-players", 24)
        // needs the countdown

        var spawnLocation: Location by path("lobby.spawn-vector", {
            val parts = it.split(",")
            Vector(parts[1].toInt(), parts[2].toInt(), parts[3].toInt()).toLocation(Bukkit.getWorld(parts[0]))
        }, { "${it.world.name},${it.blockX},${it.blockY},${it.blockZ}" })
    }

    object Match {
        // not sure how to do the countdowns
    }

    object Thirst {
        var refreshRate: Int by path("thirst.refresh-rate", 250)
        var thirstDecrement: Int by path("thirst.thirst-decrement", 1)
        var dehydrationLevel: Int by path("thirst.dehydration-level", 10)
        var healthDamage: Int by path("thirst.health-damage", 1)
        var hungerDamage: Int by path("thirst.hunger-damage", 0)

        var waterBottleRegen: Int by path("thirst.container-settings.waterbottle-regened", 100)
        var waterBucketRegen: Int by path("thirst.container-settings.waterbucket-regened", 100)
    }

    object Chat {
        var radius: Int by path("chat.radius", 30)
    }
}