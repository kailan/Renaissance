package net.hungerstruck.renaissance.modules.scoreboard

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot

import java.util.*

/**
 * Created by teddy on 29/03/2016.
 */
class RScoreboard(private var title: String?, vararg players: UUID) {
    private val scores: MutableMap<Int, String>
    private val players: MutableList<UUID>

    init {
        scores = HashMap<Int, String>()
        this.players = ArrayList(Arrays.asList(*players))
    }

    fun setTitle(title: String): RScoreboard {
        this.title = title
        return this
    }

    fun getTitle(): String {
        return title!!
    }

    fun setScore(index: Int, entry: String): RScoreboard {
        scores.put(index, entry)
        return this
    }

    fun removeScore(index: Int): RScoreboard {
        scores.remove(index)
        return this
    }

    fun getScores(): Map<Int, String> {
        return scores
    }

    fun addPlayers(vararg players: UUID): RScoreboard {
        this.players.addAll(arrayListOf(*players))
        return this
    }

    fun removePlayers(vararg players: UUID): RScoreboard {
        this.players.removeAll(arrayListOf(*players))
        return this
    }

    fun getPlayers(): List<UUID> {
        return players
    }

    fun show() {
        val scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective = scoreboard.registerNewObjective(title, "dummy")
        objective.displaySlot = DisplaySlot.SIDEBAR

        for (index in scores.keys) objective.getScore(scores[index]).score = index

        for (uuid in players) {
            val offlinePlayer = Bukkit.getOfflinePlayer(uuid)
            if (offlinePlayer.isOnline) {
                val player = offlinePlayer as Player
                player.scoreboard = scoreboard
            }
        }
    }
}
