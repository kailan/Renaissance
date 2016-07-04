package net.hungerstruck.renaissance.match

import com.google.common.base.Strings
import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.commands.CommandUtils
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.util.TitleUtil
import net.hungerstruck.renaissance.xml.RMap
import net.hungerstruck.renaissance.xml.module.RModuleContext
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.util.ChatPaginator

/**
 * Represents a match.
 *
 * Created by molenzwiebel on 20-12-15.
 */
class RMatch {
    val id: Int
    val map: RMap
    val world: World
    var state: State = State.LOADED

    val moduleContext: RModuleContext

    val players: List<RPlayer>
        get() = RPlayer.getPlayers() { it.match == this }

    val alivePlayers: List<RPlayer>
        get() = RPlayer.getPlayers() { it.match == this && it.state == RPlayer.State.PARTICIPATING }

    val shouldEnd: Boolean
        get() = alivePlayers.size <= 1

    constructor(id: Int, map: RMap, world: World) {
        this.id = id
        this.map = map
        this.world = world

        this.moduleContext = RModuleContext(this)

        Bukkit.getPluginManager().callEvent(RMatchLoadEvent(this))
        RPlayer.updateVisibility()
    }

    public fun sendMessage(msg: String, f: (RPlayer) -> Boolean = { true }) {
        Bukkit.getConsoleSender().sendMessage("[match-$id] $msg")
        players.filter(f).forEach { it.sendMessage(RConfig.General.mainMessagePrefix + msg) }
    }

    public fun sendPrefixlessMessage(msg: String, f: (RPlayer) -> Boolean = { true }) {
        Bukkit.getConsoleSender().sendMessage("[match-$id] $msg")
        players.filter(f).forEach { it.sendMessage(msg) }
    }

    public fun sendTitle(title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int, f: (RPlayer) -> Boolean = { true }) {
        Bukkit.getConsoleSender().sendMessage("[match-$id] $title $subtitle")
        players.filter(f).forEach { TitleUtil.sendTitle(it, title, subtitle, fadeIn, stay, fadeOut) }
    }

    /**
     * Begins the starting countdown for this match.
     */
    public fun beginCountdown() {
        assert(state == State.LOADED, { "Cannot begin countdown from state $state" })
        state = State.STARTING
        Renaissance.countdownManager.start(RMatchStartCountdown(this), RConfig.Match.countdownTime)
    }

    /**
     * Starts the match.
     */
    public fun startMatch() {
        state = State.PLAYING

        Bukkit.getPluginManager().callEvent(RMatchStartEvent(this))
        RPlayer.updateVisibility()
        sendMapInfo()

        if (shouldEnd) {
            if (alivePlayers.size == 1) {
                announceWinner(alivePlayers[0])
            } else {
                endMatch()
                sendMessage("${ChatColor.RED}No players are playing! Ending the game.")
            }
        }
    }

    /**
     * Sends map info to players
     */
    private fun sendMapInfo() {
        sendPrefixlessMessage(CommandUtils.formatHeader(ChatColor.GOLD.toString() + map.mapInfo.name + " " + ChatColor.GRAY.toString() + map.mapInfo.version, ChatColor.YELLOW))
        sendPrefixlessMessage(ChatColor.YELLOW.toString() + map.mapInfo.objective)
        sendPrefixlessMessage(ChatColor.YELLOW.toString() + "Author" + (if (map.mapInfo.authors.count() > 1) "s" else "") + ": " + map.mapInfo.authors.map { ChatColor.GOLD.toString() + it.name }.joinToString(", "))
        if(map.mapInfo.contributors.count() > 0) sendPrefixlessMessage(ChatColor.YELLOW.toString() + "Contributor" + (if (map.mapInfo.contributors.count() > 1) "s" else "") + ": " + map.mapInfo.contributors.map { ChatColor.GOLD.toString() + it.name }.joinToString(", "))
        sendPrefixlessMessage(CommandUtils.formatHeader(ChatColor.GOLD.toString() + "HungerStruck", ChatColor.YELLOW))
    }

    /**
     * Ends the match
     */
    public fun endMatch() {
        state = State.ENDED
        Bukkit.getPluginManager().callEvent(RMatchEndEvent(this, null))
    }

    public fun endMatch(winner: RPlayer) {
        state = State.ENDED
        Bukkit.getPluginManager().callEvent(RMatchEndEvent(this, winner))
    }

    /**
     * Performs any unloading and cleanup that this map might want to do.
     */
    public fun cleanup() {
        for (module in moduleContext.modules) {
            module.cleanup()
        }
    }

    fun announceWinner(player: RPlayer) {
        sendTitle(RConfig.Match.matchEndMessageTitle.format(player.displayName), RConfig.Match.matchEndMessageSubTitle, RConfig.Match.matchEndMessageFadeIn, RConfig.Match.matchEndMessageDuration, RConfig.Match.matchEndMessageFadeOut)

        sendPrefixlessMessage("\n")
        sendMessage("${ChatColor.DARK_PURPLE}${player.displayName}${ChatColor.WHITE} has won the game!")
        sendPrefixlessMessage("\n")

        endMatch(player)

        if (player.isOnline) player.allowFlight = true
        RPlayer.updateVisibility()
    }

    public enum class State {
        // Loaded. Players are not in already, they are still in the lobby for this match.
        LOADED,
        // Countdown for start is running, players are in.
        STARTING,
        // The match is currently in progress.
        PLAYING,
        // The match has ended but has not yet been unloaded. When unloaded, the RMatch gets gcd.
        ENDED;
    }
}