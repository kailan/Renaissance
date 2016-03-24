package net.hungerstruck.renaissance.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException
import com.sk89q.minecraft.util.commands.CommandPermissions;
import net.hungerstruck.renaissance.rplayer
import net.hungerstruck.renaissance.xml.RMap
import net.hungerstruck.renaissance.xml.RMapInfo
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MapCommands {
    @Command(aliases = arrayOf("map"), desc = "List map info for current map or the supplied one", usage = "[map]", min = 0, max = -1)
    public fun map(args: CommandContext, sender: CommandSender) {
        val player: Player = CommandUtils.getPlayer(sender)
        if (player.rplayer.match == null && player.rplayer.lobby == null)
            throw CommandException("You must be in a match or lobby to execute this command")
        val map: RMap = if (player.rplayer.match != null) CommandUtils.getPlayer(sender).rplayer.match!!.map else CommandUtils.getPlayer(sender).rplayer.lobby!!.lobbyMap
        val mapInfo: RMapInfo = map.mapInfo;

        sender.sendMessage(mapInfo.name);
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Objective: " + ChatColor.GOLD + mapInfo.objective);
        sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Author(s): ${mapInfo.authors.map { it.name }.joinToString(", ")}");

        if (mapInfo.contributors.size > 0)
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Contributor(s): ${mapInfo.contributors.map { it.name }.joinToString(", ")}");

        val rules: List<String> = mapInfo.rules;
        if (rules.size > 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Rules:");
            for (i in rules.indices)
                sender.sendMessage("${(i + 1)}) " + ChatColor.GOLD + rules.get(i));
        }
    }
}
