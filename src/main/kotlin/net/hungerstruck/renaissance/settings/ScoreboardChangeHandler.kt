package net.hungerstruck.renaissance.settings

import me.anxuiz.settings.Setting
import me.anxuiz.settings.bukkit.PlayerSettingCallback
import net.hungerstruck.renaissance.modules.scoreboard.RScoreboard
import net.hungerstruck.renaissance.modules.scoreboard.ScoreboardModule
import net.hungerstruck.renaissance.rplayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ScoreboardChangeCallback : PlayerSettingCallback() {
    override fun notifyChange(player: Player, setting: Setting, oldValue: Any?, newValue: Any?) {
        if (player.rplayer.match == null) return
        var scoreboardModule: ScoreboardModule = player.rplayer.match!!.moduleContext.getModule<ScoreboardModule>()!!
        if (newValue == true) {
            scoreboardModule.showScoreboard(player.rplayer)
        } else {
            scoreboardModule.hideScoreboard(player.rplayer)
        }
    }
}