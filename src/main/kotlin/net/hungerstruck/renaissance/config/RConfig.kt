package net.hungerstruck.renaissance.config

import org.bukkit.ChatColor
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
        var noMatchesMessage: String by path("general.no-matches", "§cThere are no lobbies or matches available.\n\nRejoin later!")
        var mainMessagePrefix: String by path("general.main-message-prefix", "${ChatColor.BOLD}»${ChatColor.RESET} ")
    }

    object Maps {
        var mapDir: String by path("maps.dir", "maps")
        var mapFileName: String by path("maps.map-file", "map.xml")
        var worldPrefix: String by path("maps.world-prefix", "match-")
    }

    object Lobby {
        var defaultLobby: String by path("lobby.default-lobby-map")
        var joinStrategy: JoinStrategy by path("lobby.join-strategy", { JoinStrategy.valueOf(it) }, { it.name })

        var chatFormat: String by path("lobby.chat-format", "%s ${ChatColor.GRAY}${RConfig.General.mainMessagePrefix}%s")

        var autoStart: Boolean by path("lobby.auto-start", true)
        var minimumPlayerStartCount: Int by path("lobby.minimum-players", 2)
        var maximumPlayerStartCount: Int by path("lobby.maximum-players", 24)

        var tickMessage: String by path("lobby.start-countdown.message", "${ChatColor.YELLOW}The lobby will end in ${ChatColor.GOLD}%s ${ChatColor.YELLOW}second(s).")
        var countdownTime: Int by path("lobby.start-countdown.time", 20)
    }

    object Match {
        var joinStrategy: JoinStrategy by path("match.join-strategy", { JoinStrategy.valueOf(it) }, { it.name })

        var tickMessage: String by path("match.start-countdown.message", "${ChatColor.YELLOW}The game will begin in ${ChatColor.GOLD}%s ${ChatColor.YELLOW}second(s).")
        var countdownTime: Int by path("match.start-countdown.time", 20)

        var feastTime: Int by path("match.feast.time", 300)
        var feastRarityIncrease: Double by path("match.feast.rarity-increase", 0.15)
        var feastMessage: String by path("match.feast.message", "${ChatColor.DARK_AQUA}There has been a feast!")

        var playerDeathByPlayerMessage: String by path("match.death-player-message", "${ChatColor.RED}%0\$s ${ChatColor.GRAY}was slain by ${ChatColor.RED}%1\$s${ChatColor.GRAY}.")
        var playerDeathByOtherMessage: String by path("match.death-other-message", "${ChatColor.RED}%0\$s ${ChatColor.GRAY}mysteriously died.")
        var playerRemainMessage: String by path("match.player-remain-message", "${ChatColor.GOLD}%0\$d ${ChatColor.GRAY}players remain.")
        var matchEndMessageTitle : String by path("math.end-message-title", "${ChatColor.GOLD}" + "%s")
        var matchEndMessageSubTitle : String by path("math.end-message-subtitle", "${ChatColor.DARK_PURPLE}" + " wins!")
        var matchEndMessageFadeIn : Int by path("math.end-message-fade-in", 2);
        var matchEndMessageDuration : Int by path("math.end-message-duration", 5);
        var matchEndMessageFadeOut : Int by path("math.end-message-fade-out", 2);
    }

    object Thirst {
        var healthDamage: Int by path("thirst.health-damage", 1)
        var hungerDamage: Int by path("thirst.hunger-damage", 0)
    }

    object Chat {
        var radius: Int by path("chat.radius", 30)
    }

    object Scoreboard {
        var timeString: String by path("scoreboard.time-string", "${ChatColor.GOLD}${ChatColor.BOLD}Time")
        var killsString: String by path("scoreboard.kills-string", "${ChatColor.DARK_RED}${ChatColor.BOLD}Kills")
        var aliveString: String by path("scoreboard.alive-string", "${ChatColor.DARK_GREEN}${ChatColor.BOLD}Alive")
        var sanityString: String by path("scoreboard.sanity-string", "${ChatColor.DARK_PURPLE}${ChatColor.BOLD}Sanity")
        var thirstString: String by path("scoreboard.thirst-string", "${ChatColor.GRAY}${ChatColor.BOLD}Thirst")
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