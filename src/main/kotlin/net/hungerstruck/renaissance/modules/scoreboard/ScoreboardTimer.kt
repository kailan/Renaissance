package net.hungerstruck.renaissance.modules.scoreboard

import net.hungerstruck.renaissance.match.RMatch

/**
 * Created by teddy on 29/03/2016.
 */
class ScoreboardTimer(private val module: ScoreboardModule) : Runnable {

    private var seconds = 0

    override fun run() {
        if (module.match.state !== RMatch.State.PLAYING) return

        val time = formatTime()

        for (p in module.match.players) {
            module.scoreboardMap[p.uniqueId]?.setScore(-3, time)?.setScore(-9, module.match.alivePlayers.size.toString())?.show()
        }

        seconds++
    }

    private fun formatTime(): String {
        val hours = seconds / 3600
        val hourRem = seconds % 3600
        val mins = hourRem / 60
        val secs = hourRem % 60

        var hourString = hours.toString()
        var minString = mins.toString()
        var secString = secs.toString()

        if(hours < 10) hourString = "0" + hourString
        if(mins < 10) minString = "0" + minString
        if(secs < 10) secString = "0" + secString

        var output = if (hours > 0) hourString + ":" else ""
        output = output + minString + ":" + secString

        return output
    }
}
