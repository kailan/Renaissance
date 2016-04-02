package net.hungerstruck.renaissance.util;

import net.hungerstruck.renaissance.RPlayer
import net.minecraft.server.IChatBaseComponent
import net.minecraft.server.PacketPlayOutTitle
import net.minecraft.server.PlayerConnection
import org.bukkit.craftbukkit.entity.CraftPlayer

object TitleUtil {
    public fun sendTitle(player: RPlayer, title: String, subtitle: String, fadeIn: Int, stay: Int, fadeOut: Int ) {
        var  craftplayer: CraftPlayer = player.bukkit as CraftPlayer;
        var connection: PlayerConnection = craftplayer.getHandle().playerConnection;
        var titleJSON: IChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{'text': '" + title + "'}");
        var subtitleJSON: IChatBaseComponent = IChatBaseComponent.ChatSerializer.a("{'text': '" + subtitle + "'}");
        var titlePacket: PacketPlayOutTitle =  PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        var subtitlePacket: PacketPlayOutTitle = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }
}