package net.hungerstruck.renaissance.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
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
        var noMatchesMessage: String by path("general.no-matches", "There are no lobbies or matches available.\n\nRejoin later!")
    }

    object Maps {
        var mapDir: String by path("maps.dir", "maps")
        var mapFileName: String by path("maps.map-file", "map.xml")
        var rotationFileName: String by path("maps.rotation-file", "rotation.txt")
        var worldPrefix: String by path("maps.world-prefix", "match-")
    }

    object Lobby {
        var defaultLobby: String by path("lobby.default-lobby")
        var joinStrategy: JoinStrategy by path("lobby.join-strategy", { JoinStrategy.valueOf(it) }, { it.name })

        var chatFormat: String by path("lobby.chat-format", "<%s> %s")

        var autoStart: Boolean by path("lobby.auto-start", true)
        var minimumPlayerStartCount: Int by path("lobby.minimum-players", 2)
        var maximumPlayerStartCount: Int by path("lobby.maximum-players", 24)

        var tickMessage: String by path("lobby.start-countdown.tick-message", "The lobby will end in %s seconds")
    }

    object Match {
        var joinStrategy: JoinStrategy by path("match.join-strategy", { JoinStrategy.valueOf(it) }, { it.name })
        var tickMessage: String by path("match.start-countdown.tick-message", "The games will begin in %s seconds")

        var playerDeathByPlayerMessage: String by path("match.death-player-message", "%0\$s was slain by %1\$s. %2\$d players remain.")
        var playerDeathByOtherMessage: String by path("match.death-other-message", "%0\$s mysteriously died. %2\$d players remain.")
        var playerWinMessage: String by path("match.win-message", "%s won!")
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

    /**
     * The strategy for which a player gets assigned a lobby/match when he/she first joins.
     * <b>NOTE:</b> This is only for _joining_, not after a cycle. After a cycle, the player will join the next lobby.
     */
    enum class JoinStrategy {
        /**
         * The first lobby or map in the manager will be the one joined.
         */
        FIRST,
        /**
         * A random lobby/map will be selected.
         */
        RANDOM,
        /**
         * The lobby/map with the lowest amount of participants will be selected.
         */
        SMALLEST;
    }
}