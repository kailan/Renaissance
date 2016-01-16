package net.hungerstruck.renaissance.util

import net.hungerstruck.renaissance.RPlayer
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import net.minecraft.server.v1_8_R3.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player

/**
 * Regularly sends the action bar message.
 *
 * Created by molenzwiebel on 16-01-16.
 */
object ActionBarSender : Runnable {
    override fun run() {
        var state = true

        while (true) {
            for (player in RPlayer.getPlayers()) {
                if (player.actionBarMessage == null) continue
                val msg = (if (state) "§r" else "") + player.actionBarMessage
                sendActionBar(player.bukkit, msg)
            }

            state = !state
            Thread.sleep(250)
        }
    }

    private fun sendActionBar(player: Player, msg: String) {
        val packet = PacketPlayOutChat()
        setValue(packet, "a", IChatBaseComponent.ChatSerializer.a("{\"text\":\"$msg\",\"color\":\"white\"}"))
        setValue(packet, "b", 2.toByte())
        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    private fun setValue(inst: Any, field: String, value: Any) {
        val f = inst.javaClass.getDeclaredField(field)
        f.isAccessible = true
        f.set(inst, value)
    }
}