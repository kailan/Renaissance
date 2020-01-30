package net.hungerstruck.renaissance.util

import net.hungerstruck.renaissance.RPlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

/**
 * Regularly sends the action bar message.
 */
object ActionBarSender : BukkitRunnable() {
    override fun run() {
        for (player in RPlayer.getPlayers()) {
            sendActionBar(player.bukkit, player.actionBarMessage ?: continue)
        }
    }

    private fun sendActionBar(player: Player, msg: String) {
        player.sendActionBar(msg)
    }

}