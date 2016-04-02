package net.hungerstruck.renaissance.modules.scoreboard

import net.hungerstruck.renaissance.RPlayer
import net.hungerstruck.renaissance.Renaissance
import net.hungerstruck.renaissance.RenaissancePlugin
import net.hungerstruck.renaissance.config.RConfig
import net.hungerstruck.renaissance.event.match.RMatchEndEvent
import net.hungerstruck.renaissance.event.match.RMatchLoadEvent
import net.hungerstruck.renaissance.event.match.RMatchStartEvent
import net.hungerstruck.renaissance.event.player.RPlayerSanityUpdateEvent
import net.hungerstruck.renaissance.event.player.RPlayerThirstUpdateEvent
import net.hungerstruck.renaissance.match.RMatch
import net.hungerstruck.renaissance.modules.SanityModule
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.xml.module.RModule
import net.hungerstruck.renaissance.xml.module.RModuleContext
import net.hungerstruck.renaissance.xml.module.RModuleInfo
import net.hungerstruck.renaissance.xml.module.RModuleRegistry
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent
import org.jdom2.Document

import java.util.HashMap
import java.util.UUID

/**
 * Created by teddy on 29/03/2016.
 */
class ScoreboardModule(match: RMatch, document: Document, modCtx: RModuleContext) : RModule(match, document, modCtx) {

    val scoreboardMap: MutableMap<UUID, RScoreboard>
    val killMap: MutableMap<UUID, Int>
    var timer = 0

    init {
        scoreboardMap = HashMap<UUID, RScoreboard>()
        killMap = HashMap<UUID, Int>()
        registerEvents()
    }

    @EventHandler
    fun onMatchStart(event: RMatchStartEvent) {
        for (p in event.match.players) {
            val scoreboard = RScoreboard("§e§lHungerStruck", p.uniqueId)
            setupScoreboard(scoreboard, p)
            scoreboard.show()
            scoreboardMap.put(p.uniqueId, scoreboard)
        }

        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(Renaissance.plugin, ScoreboardTimer(this), 0, 20)
    }

    @EventHandler
    fun onMatchEnd(event: RMatchEndEvent){
        for (p in event.match.players){
            p.player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        }

        scoreboardMap.clear()
        Bukkit.getScheduler().cancelTask(timer)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent){
        if (match.players.contains(event.entity.rplayer)){
            for (scoreboard in scoreboardMap.values){
                scoreboard.setScore(-9, (match.alivePlayers.size-1).toString()).show()
            }

            if (event.entity.killer is Player){
                val killer: Player = event.entity.killer

                if (killMap.containsKey(killer.uniqueId)){
                    killMap.put(killer.uniqueId, killMap[killer.uniqueId]?.and(1)!!)
                } else {
                    killMap.put(killer.uniqueId, 1)
                }

                scoreboardMap[killer.uniqueId]?.setScore(-6, killMap[killer.uniqueId].toString())?.show()
            }

            scoreboardMap[event.entity.uniqueId]?.removeScore(-10)?.removeScore(-11)?.removeScore(-12)?.removeScore(-13)?.removeScore(-14)?.removeScore(-15)?.show()
        }
    }

    @EventHandler
    fun onThirstUpdate(event: RPlayerThirstUpdateEvent){
        scoreboardMap[event.player.uniqueId]?.setScore(-15, event.thirst.toString() + "%§1 ")?.show()
    }

    @EventHandler
    fun onSanityUpdate(event: RPlayerSanityUpdateEvent){
        scoreboardMap[event.player.uniqueId]?.setScore(-12, event.sanity.toString() + "%§2 ")?.show()
    }

    private fun setupScoreboard(scoreboard: RScoreboard, player: RPlayer) {
        scoreboard.setScore(-1, "§1 ").setScore(-2, RConfig.Scoreboard.timeString).setScore(-3, "00:00")
        scoreboard.setScore(-4, "§2 ").setScore(-5, RConfig.Scoreboard.killsString).setScore(-6, "0")
        scoreboard.setScore(-7, "§3 ").setScore(-8, RConfig.Scoreboard.aliveString).setScore(-9, this.match.alivePlayers.size.toString())
        if (match.alivePlayers.contains(player)) {
            scoreboard.setScore(-10, "§4 ").setScore(-11, RConfig.Scoreboard.sanityString).setScore(-12, "100%§1 ")
            scoreboard.setScore(-13, "§5 ").setScore(-14, RConfig.Scoreboard.thirstString).setScore(-15, "100%§2 ")
        }
    }
}
