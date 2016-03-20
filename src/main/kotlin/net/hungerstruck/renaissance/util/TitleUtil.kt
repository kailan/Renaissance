package net.hungerstruck.renaissance.util;

import net.hungerstruck.renaissance.RPlayer
import net.minecraft.server.v1_8_R3.IChatBaseComponent
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PlayerConnection
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;

object TitleUtil {
    public fun sendTitle(player: RPlayer, title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int ) {
        var  craftplayer: CraftPlayer = player.bukkit as CraftPlayer;
        var connection: PlayerConnection = craftplayer.getHandle().playerConnection;
        var titleJSON: IChatBaseComponent = ChatSerializer.a("{'text': '" + title + "'}");
        var subtitleJSON: IChatBaseComponent = ChatSerializer.a("{'text': '" + subtitle + "'}");
        var titlePacket: PacketPlayOutTitle =  PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        var subtitlePacket: PacketPlayOutTitle = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
}