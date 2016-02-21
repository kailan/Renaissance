package net.hungerstruck.renaissance.util

import net.hungerstruck.renaissance.RPlayer
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
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
}

private fun sendActionBar(player: Player, msg: String) {
    val packet = PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"$msg\",\"color\":\"white\"}"), 2)
    (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
}
